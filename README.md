## Better Teams

# Introduction:

Create teams to compete to be the best. This plugin is designed to encourage teamwork and foster a sense of community
within a server. BetterTeams includes features such as:

- Teaming up with friends
- Having private chats, unique to each team
- Protecting team members from team-killing.
- Individual homes for each team

[View the wiki for this project](https://booksaw.github.io/BetterTeams/)
[Looking for the Discord Server for support?](https://discord.gg/JF9DNs3)

---

## Multi-Server (Network) Setup

This plugin can run across multiple Minecraft servers (SMP/minigames/etc.) while sharing team data and live updates.

- Important: Use SQL storage to share core team data (members, allies, warps, homes, etc.)
- Optional: Enable Redis Pub/Sub for live sync of money, score and team metadata (name, tag, color)

### 1) Configure shared SQL

On each server, set the same SQL connection in `plugins/BetterTeams/config.yml`:

```yaml
storageType: SQL

database:
  host: your-mysql-host
  port: 3306
  database: your_db
  user: your_user
  password: your_password
  # Optional per-network table isolation (change if sharing DB with other networks)
  tablePrefix: BetterTeams_
```

All servers must point to the same database and use the same `tablePrefix` to share data.

### 2) Enable Redis live sync (optional but recommended)

Enables near real-time updates of balances, scores and simple team metadata across servers.

In `plugins/BetterTeams/config.yml` on each server:

```yaml
redis:
  enabled: true
  # Use a single URL or host/port credentials
  url: "redis://localhost:6379/0"
  channelNamespace: "betterteams:v1"
  sync:
    money: true
    score: true
    teamMeta: true
  publishDebounceMs: 100
```

- Set the same `channelNamespace` on all servers in the same network
- TLS endpoints are supported with `rediss://`

### 4) Recommended topology

- Put the SQL database and Redis on reliable infrastructure
- Each game server runs BetterTeams with identical `config.yml` for SQL/Redis
- Use a proxy (Velocity/Bungee) if you need network-wide chat/commands (not required for team data)

### 5) Verifying

- Create a team on Server A and check visibility on Server B (`/team info`)
- Deposit/withdraw money and confirm balance changes appear on the other server
- Change team name/tag/color and verify updates propagate

### 6) Troubleshooting

- If teams don’t appear on other servers:
  - Confirm `storageType: SQL` on all servers
  - Ensure all servers point to the same DB and `tablePrefix`
- If balances/scores don’t update live:
  - Confirm `redis.enabled: true` and credentials
  - Ensure servers share the same `channelNamespace`
- Check console logs for database or Redis connectivity errors

---
