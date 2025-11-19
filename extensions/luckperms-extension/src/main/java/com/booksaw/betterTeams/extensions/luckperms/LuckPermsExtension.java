package com.booksaw.betterTeams.extensions.luckperms;

import com.booksaw.betterTeams.extension.BetterTeamsExtension;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.configuration.file.FileConfiguration;

public class LuckPermsExtension extends BetterTeamsExtension {

     private LuckPermsManager luckPermsManager;

    @Override
    public void onEnable() {
        if (getPlugin().getServer().getPluginManager().isPluginEnabled("LuckPerms")) {
            try {
                FileConfiguration config = getConfig().config;

                LuckPerms luckPerms = LuckPermsProvider.get();
                luckPermsManager = new LuckPermsManager(luckPerms, config);
                luckPermsManager.register();
            } catch (Exception e) {
                getLogger().warning("Could not properly hook into LuckPerms, extension Disabled" + e.getMessage());
                e.printStackTrace();
                luckPermsManager = null;
                selfDisable();
            }
        } else {
            getLogger().warning("LuckPerms plugin not found. Disabling extension.");
            selfDisable();
        }
    }

    @Override
    public void onDisable() {
        if (luckPermsManager != null) {
            luckPermsManager.unregister();
        }
    }
}