package com.booksaw.betterTeams.integrations;

import com.booksaw.betterTeams.Main;
import com.booksaw.betterTeams.Team;
import com.booksaw.betterTeams.TeamPlayer;
import com.booksaw.betterTeams.message.MessageManager;
import com.booksaw.betterTeams.team.storage.team.StoredTeamValue;
import com.tcoded.folialib.FoliaLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Handles Redis connection, publishing and subscription for cross-server sync.
 */
public final class RedisSyncService {

    public enum EventType {
        MONEY_SET,
        SCORE_SET,
        TEAM_META // name, tag, color
    }

    private static final String KEY_EVENT = "e";
    private static final String KEY_TEAM = "t";
    private static final String KEY_VAL = "v";
    private static final String KEY_NAME = "n";
    private static final String KEY_TAG = "g";
    private static final String KEY_COLOR = "c";

    private final String channel;
    private final boolean syncMoney;
    private final boolean syncScore;
    private final boolean syncMeta;

    private volatile boolean enabled;
    private volatile boolean closed;

    private JedisPool pool; // used for publishers
    private Thread subscriberThread;

    private final FoliaLib scheduler;
    private final ConcurrentMap<String, Long> lastPublishAtMs = new ConcurrentHashMap<>();
    private final long debounceMs;
    private final ThreadLocal<Boolean> suppressPublishTL = ThreadLocal.withInitial(() -> false);

    public RedisSyncService(@NotNull Main plugin) {
        ConfigurationSection cfg = plugin.getConfig().getConfigurationSection("redis");
        if (cfg == null || !cfg.getBoolean("enabled", false)) {
            this.enabled = false;
            this.scheduler = plugin.getFoliaLib();
            this.channel = "";
            this.syncMoney = false;
            this.syncScore = false;
            this.syncMeta = false;
            this.debounceMs = 0L;
            return;
        }

        this.enabled = true;
        this.scheduler = plugin.getFoliaLib();
        String ns = cfg.getString("channelNamespace", "betterteams:v1");
        this.channel = ns + ":events";
        this.syncMoney = cfg.getBoolean("sync.money", true);
        this.syncScore = cfg.getBoolean("sync.score", true);
        this.syncMeta = cfg.getBoolean("sync.teamMeta", true);
        this.debounceMs = cfg.getLong("publishDebounceMs", 100L);

        try {
            String url = cfg.getString("url", "");
            if (url != null && !url.isEmpty()) {
                this.pool = new JedisPool(new JedisPoolConfig(), URI.create(url));
            } else {
                String host = cfg.getString("host", "localhost");
                int port = cfg.getInt("port", 6379);
                String username = Objects.toString(cfg.getString("username", ""), "");
                String password = Objects.toString(cfg.getString("password", ""), "");
                int database = cfg.getInt("database", 0);
                JedisPoolConfig pc = new JedisPoolConfig();
                pc.setMaxTotal(16);
                pc.setMinIdle(0);
                pc.setMaxIdle(8);
                if (!username.isEmpty() || !password.isEmpty()) {
                    this.pool = new JedisPool(pc, host, port, 2000, username.isEmpty() ? null : username,
                            password.isEmpty() ? null : password, database, null);
                } else {
                    this.pool = new JedisPool(pc, host, port, 2000, null, database, null);
                }
            }
            startSubscriber();
            plugin.getLogger().info("Redis sync enabled on channel: " + channel);
        } catch (Throwable t) {
            plugin.getLogger().warning("Failed to initialize Redis. Disabling Redis sync. Error: " + t.getMessage());
            this.enabled = false;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void close() {
        closed = true;
        if (subscriberThread != null) {
            try { subscriberThread.interrupt(); } catch (Exception ignored) {}
        }
        if (pool != null) {
            try { pool.close(); } catch (Exception ignored) {}
        }
    }

    private void startSubscriber() {
        if (!enabled) return;
        final JedisPubSub sub = new JedisPubSub() {
            @Override
            public void onMessage(String ch, String message) {
                if (!channel.equals(ch)) return;
                handleIncoming(message);
            }
        };

        subscriberThread = new Thread(() -> {
            while (!closed) {
                try (Jedis jedis = pool.getResource()) {
                    jedis.subscribe(sub, channel);
                } catch (Throwable t) {
                    if (!closed)
                        Main.plugin.getLogger().warning("Redis subscriber error: " + t.getMessage());
                    try { Thread.sleep(1000L); } catch (InterruptedException ignored) {}
                }
            }
        }, "BetterTeams-RedisSubscriber");
        subscriberThread.setDaemon(true);
        subscriberThread.start();
    }

    private void handleIncoming(@NotNull String json) {
        // Trivial parser for our small JSON (avoid heavy deps). Format: {"e":"MONEY_SET","t":"uuid","v":123.0}
        try {
            String event = extract(json, KEY_EVENT);
            String teamStr = extract(json, KEY_TEAM);
            if (event == null || teamStr == null) return;
            UUID teamId = UUID.fromString(teamStr);

            switch (EventType.valueOf(event)) {
                case MONEY_SET: {
                    if (!syncMoney) return;
                    String val = extract(json, KEY_VAL);
                    if (val == null) return;
                    double newMoney = Double.parseDouble(val);
                    applyTeamMoney(teamId, newMoney);
                    break;
                }
                case SCORE_SET: {
                    if (!syncScore) return;
                    String val = extract(json, KEY_VAL);
                    if (val == null) return;
                    int newScore = Integer.parseInt(val);
                    applyTeamScore(teamId, newScore);
                    break;
                }
                case TEAM_META: {
                    if (!syncMeta) return;
                    String name = extract(json, KEY_NAME);
                    String tag = extract(json, KEY_TAG);
                    String colorChar = extract(json, KEY_COLOR);
                    applyTeamMeta(teamId, name, tag, colorChar);
                    break;
                }
            }
        } catch (Throwable t) {
            Main.plugin.getLogger().warning("Failed to parse Redis message: " + json + " error: " + t.getMessage());
        }
    }

    private void applyTeamMoney(UUID teamId, double newMoney) {
        scheduler.getScheduler().runAsync(task -> {
            Team team = Team.getTeam(teamId);
            if (team == null) return;
            // update storage and memory without re-publishing
            team.getMoneyComponent().set(newMoney);
            team.getMoneyComponent().save(team.getStorage());
        });
    }

    private void applyTeamScore(UUID teamId, int newScore) {
        scheduler.getScheduler().runAsync(task -> {
            Team team = Team.getTeam(teamId);
            if (team == null) return;
            team.getScoreComponent().set(newScore);
            team.getScoreComponent().save(team.getStorage());
        });
    }

    private void applyTeamMeta(UUID teamId, String name, String tag, String colorChar) {
        // Apply on main/server thread due to Bukkit events/scoreboard interactions within Team setters
        scheduler.getScheduler().runLater(task -> {
            Team team = Team.getTeam(teamId);
            if (team == null) return;
            runWithoutPublish(() -> {
                boolean changed = false;
                if (name != null && !name.isEmpty() && !Objects.equals(team.getName(), name)) {
                    try { team.setName(name, null); changed = true; } catch (Exception ignored) {}
                }
                if (tag != null && !Objects.equals(team.getOriginalTag(), tag)) {
                    try { team.setTag(tag); changed = true; } catch (Exception ignored) {}
                }
                if (colorChar != null && !colorChar.isEmpty()) {
                    ChatColor cc = ChatColor.getByChar(colorChar.charAt(0));
                    if (cc != null && cc != team.getColor()) {
                        try { team.setColor(cc); changed = true; } catch (Exception ignored) {}
                    }
                }
                if (changed) {
                    // ensure persisted
                    team.getStorage().set(StoredTeamValue.NAME, team.getName());
                    team.getStorage().set(StoredTeamValue.TAG, team.getOriginalTag());
                    team.getStorage().set(StoredTeamValue.COLOR, team.getColor().getChar());
                }
            });
        }, 1L);
    }

    public void publishMoney(@NotNull UUID teamId, double newMoney) {
        if (!enabled || !syncMoney) return;
        publishDebounced("money:" + teamId, toJsonEvent(EventType.MONEY_SET, teamId, Double.toString(newMoney)));
    }

    public void publishScore(@NotNull UUID teamId, int newScore) {
        if (!enabled || !syncScore) return;
        publishDebounced("score:" + teamId, toJsonEvent(EventType.SCORE_SET, teamId, Integer.toString(newScore)));
    }

    public void publishTeamMeta(@NotNull UUID teamId, String name, String tag, char colorChar) {
        if (!enabled || !syncMeta) return;
        String json = '{' +
                '"' + KEY_EVENT + '"' + ':' + '"' + EventType.TEAM_META.name() + '"' + ',' +
                '"' + KEY_TEAM + '"' + ':' + '"' + teamId + '"' + ',' +
                '"' + KEY_NAME + '"' + ':' + (name == null ? "null" : '"' + escape(name) + '"') + ',' +
                '"' + KEY_TAG + '"' + ':' + (tag == null ? "null" : '"' + escape(tag) + '"') + ',' +
                '"' + KEY_COLOR + '"' + ':' + '"' + colorChar + '"' +
                '}';
        publishDebounced("meta:" + teamId, json);
    }

    private void publishDebounced(String key, String payload) {
        long now = System.currentTimeMillis();
        Long last = lastPublishAtMs.get(key);
        if (last != null && (now - last) < debounceMs) {
            return; // drop burst
        }
        lastPublishAtMs.put(key, now);
        scheduler.getScheduler().runAsync(task -> publish(payload));
    }

    private void publish(String payload) {
        try (Jedis jedis = pool.getResource()) {
            jedis.publish(channel, payload);
        } catch (Throwable t) {
            Main.plugin.getLogger().warning("Failed to publish Redis event: " + t.getMessage());
        }
    }

    private static String toJsonEvent(EventType eventType, UUID teamId, String val) {
        return '{' +
                '"' + KEY_EVENT + '"' + ':' + '"' + eventType.name() + '"' + ',' +
                '"' + KEY_TEAM + '"' + ':' + '"' + teamId + '"' + ',' +
                '"' + KEY_VAL + '"' + ':' + val +
                '}';
    }

    public boolean isPublishingSuppressed() {
        Boolean v = suppressPublishTL.get();
        return v != null && v;
    }

    public void runWithoutPublish(Runnable action) {
        boolean prev = isPublishingSuppressed();
        suppressPublishTL.set(true);
        try {
            action.run();
        } finally {
            suppressPublishTL.set(prev);
        }
    }

    private static String extract(String json, String key) {
        // very small helper; this assumes flat JSON without nested braces for our fields
        String quotedKey = '"' + key + '"' + ':';
        int idx = json.indexOf(quotedKey);
        if (idx < 0) return null;
        int start = idx + quotedKey.length();
        // determine if quoted string or raw number/null
        if (start < json.length() && json.charAt(start) == '"') {
            int end = json.indexOf('"', start + 1);
            if (end < 0) return null;
            return unescape(json.substring(start + 1, end));
        } else {
            int end = json.indexOf(',', start);
            if (end < 0) end = json.indexOf('}', start);
            if (end < 0) return null;
            String raw = json.substring(start, end).trim();
            if ("null".equals(raw)) return null;
            return raw;
        }
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private static String unescape(String s) {
        return s.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
    }
}

