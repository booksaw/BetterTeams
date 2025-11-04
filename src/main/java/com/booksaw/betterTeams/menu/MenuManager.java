package com.booksaw.betterTeams.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MenuManager {
    
    public static void openCreateMenu(Player player) {
        // Fixed typo: CreateTeama.yml -> CreateTeam.yml
        File file = new File("plugins/BetterTeams/menu/CreateTeam.yml");
        if (!file.exists()) {
            player.sendMessage("§cMenu file not found: menu/CreateTeam.yml");
            return;
        }
        
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        String title = cfg.getString("title", "Confirm?");
        int rows = cfg.getInt("rows", 3);
        
        // Fixed: ChatColor conversion using proper method
        Inventory inv = Bukkit.createInventory(null, rows * 9, title.replace('&', '§'));
        
        // Added null check for configuration section
        if (cfg.getConfigurationSection("items") == null) {
            player.sendMessage("§cNo items section found in menu file");
            return;
        }
        
        for (String key : cfg.getConfigurationSection("items").getKeys(false)) {
            String path = "items." + key + ".";
            
            String materialName = cfg.getString(path + "material", "STONE");
            Material mat = Material.matchMaterial(materialName);
            
            if (mat == null) {
                player.sendMessage("§cInvalid material: " + materialName + " for item: " + key);
                continue;
            }
            
            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            
            if (meta == null) continue;
            
            // Fixed: ChatColor conversion
            String displayName = cfg.getString(path + "name", key);
            meta.setDisplayName(displayName.replace('&', '§'));
            
            List<String> lore = cfg.getStringList(path + "lore");
            if (!lore.isEmpty()) {
                List<String> coloredLore = new ArrayList<>();
                for (String line : lore) {
                    coloredLore.add(line.replace('&', '§'));
                }
                meta.setLore(coloredLore);
            }
            
            item.setItemMeta(meta);
            
            int slot = cfg.getInt(path + "slot", 0);
            // Added bounds check for slot
            if (slot >= 0 && slot < inv.getSize()) {
                inv.setItem(slot, item);
            }
        }
        
        player.openInventory(inv);
    }
}
