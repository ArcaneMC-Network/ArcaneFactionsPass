package it.arcanemc.utils;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemStackLoader {
    public static ItemStack get(ConfigurationSection itemConfig) {
        String displayName = Colors.translate(itemConfig.getString("display-name"));
        List<String> lore = new ArrayList<>(itemConfig.getStringList("lore"));
        lore.replaceAll(Colors::translate);
        String materialName = itemConfig.getString("material");
        int data = itemConfig.getInt("data");
        int quantity = itemConfig.getInt("quantity");
        boolean glow = itemConfig.getBoolean("glow", false);

        Material material = Material.getMaterial(materialName);
        if (material == null) {
            throw new IllegalArgumentException("Invalid material: " + materialName);
        }

        ItemStack item = new ItemStack(material, quantity, (short) data);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(lore);

            if (glow) {
                meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    public static ItemStack get
            (
                    ConfigurationSection itemConfig,
                    Map<String, String> replacesDisplayName,
                    Map<String, String> replacesLore
            ) {
        ItemStack item = get(itemConfig);
        return replacePlaceholders(item, replacesDisplayName, replacesLore);
    }

    public static ItemStack get(ConfigurationSection itemConfig, Map<String, String> replaces) {
        return get(itemConfig, Map.of(), replaces);
    }

    public static ItemStack replacePlaceholders
            (
                    ItemStack item,
                    Map<String, String> displayNameMap,
                    Map<String, String> loreMap
            ) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String displayName = meta.getDisplayName();
            for (String key : displayNameMap.keySet()) {
                displayName = displayName.replace(key, displayNameMap.get(key));
            }
            meta.setDisplayName(Colors.translate(displayName));
            if (meta.getLore() != null) {
                List<String> lore = new ArrayList<>(meta.getLore());
                for (String key : loreMap.keySet()) {
                    lore.replaceAll(s -> s.replace(key, loreMap.get(key)));
                }
                lore.replaceAll(Colors::translate);
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
