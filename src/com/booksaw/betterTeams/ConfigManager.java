package com.booksaw.betterTeams;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class ConfigManager {

    public final YamlConfiguration config;
    private final String configName;

    public ConfigManager(String configName) {
        this.configName = configName;
        File f = new File(Main.plugin.getDataFolder() + File.separator + configName + ".yml");

        if (!f.exists()) {
            Main.plugin.saveResource(configName + ".yml", false);
        }

        config = YamlConfiguration.loadConfiguration(f);

    }

    public void save() {
        save(true);
    }

    public void save(boolean log) {
        if (log) {
            Bukkit.getLogger().info("Saving new values to " + configName + ".yml");
        }
        File f = new File(Main.plugin.getDataFolder() + File.separator + configName + ".yml");
        try {
            config.save(f);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save config to " + f, ex);
        }
    }

}
