package it.arcanemc.util;

import it.arcanemc.util.loader.ItemStackLoader;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@Getter
public class NumberedItemStack {
    private final ItemStack itemStack;
    private int slot;

    public NumberedItemStack(ConfigurationSection itemConfig, Map<String, String> replaces) {
        this.itemStack = ItemStackLoader.get(itemConfig, replaces);
        this.setSlot(itemConfig);
    }

    public NumberedItemStack(ConfigurationSection itemConfig) {
        this.itemStack = ItemStackLoader.get(itemConfig);
        this.setSlot(itemConfig);
    }

    public NumberedItemStack(ItemStack itemStack, ConfigurationSection itemConfig) {
        this.itemStack = itemStack;
        this.setSlot(itemConfig);
    }

    public NumberedItemStack(ItemStack itemStack, int slot) {
        this.itemStack = itemStack;
        this.slot = slot;
    }

    public void setSlot(ConfigurationSection itemConfig){
        this.slot = itemConfig.getInt("slot");
    }
}
