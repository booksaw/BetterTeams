package com.booksaw.betterTeams;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

public final class FoliaUtils {
    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static void teleport(final Player player, final Location location) {
        teleport(player, location, false);
    }

    public static void teleport(final Player player, final Location location, final boolean sync) {
        final Runnable runnable = () -> {
            if (isFolia()) {
                try {
                    final Class<?> clazz = Entity.class;
                    final Method method = clazz.getDeclaredMethod("teleportAsync", Location.class);

                    method.setAccessible(true);
                    method.invoke(player, location);
                } catch (final Exception exception) {
                    exception.printStackTrace();
                }

                return;
            }

            FoliaUtils.teleport(player, location);
        };

        if (sync)
            Main.plugin.getScheduler().runTaskAtEntity(player, runnable);
        else
            runnable.run();
    }
}