package it.arcanemc.util;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Getter
public class NumberedGuiItem extends NumberedItemStack{
    private final GuiItem guiItem;

    public NumberedGuiItem(ItemStack itemStack, int slot, @Nullable GuiAction<@NotNull InventoryClickEvent> action) {
        super(itemStack, slot);
        this.guiItem = ItemBuilder.from(this.getItemStack()).asGuiItem();
        if (action != null)
            this.guiItem.setAction(action);
    }

    public NumberedGuiItem(ConfigurationSection itemConfig, Map<String, String> replaces, @Nullable GuiAction<@NotNull InventoryClickEvent> action) {
        super(itemConfig, replaces);
        this.guiItem = ItemBuilder.from(this.getItemStack()).asGuiItem();
        if (action != null)
            this.guiItem.setAction(action);
    }

    public NumberedGuiItem(ConfigurationSection itemConfig, Map<String, String> replaces) {
        this(itemConfig, replaces, null);
    }

    public NumberedGuiItem(ConfigurationSection itemConfig, @Nullable GuiAction<@NotNull InventoryClickEvent> action) {
        this(itemConfig, Map.of(), action);
    }

    public NumberedGuiItem(ConfigurationSection itemConfig) {
        this(itemConfig, Map.of());
    }

    public NumberedGuiItem(ItemStack itemStack, ConfigurationSection itemConfig) {
        super(itemStack, itemConfig);
        this.guiItem = ItemBuilder.from(this.getItemStack()).asGuiItem();
    }

    public NumberedGuiItem(ItemStack itemStack, int slot) {
        this(itemStack, slot, null);
    }

    public Map.Entry<Integer, GuiItem> getEntry() {
        return Map.entry(this.getSlot(), this.guiItem);
    }
}
