package it.arcanemc.gui;

import dev.triumphteam.gui.builder.gui.PaginatedBuilder;
import dev.triumphteam.gui.builder.gui.SimpleBuilder;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import it.arcanemc.util.Colors;
import it.arcanemc.util.loader.ItemStackLoader;
import it.arcanemc.util.NumberedGuiItem;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GenericGui {
    @Getter
    protected BaseGui gui;

    protected ConfigurationSection configSection;
    protected int row;
    protected String title;

    protected Map<String, String> displayNameReplaces;
    protected Map<String, Map<String, String>> defaultItemsReplaces;

    protected List<NumberedGuiItem> items;
    protected Map<String, NumberedGuiItem> defaultItems;

    protected boolean isEmptyItem;
    protected ItemStack emptyItem;

    public void initialize(
            ConfigurationSection configSection,
            Map<String, String> displayNameReplaces,
            Map<String, Map<String, String>> defaultItemsReplaces
    ){
        this.configSection = configSection;
        this.row = this.configSection.getInt("rows");
        this.title = Colors.translate(this.configSection.getString("title"));
        this.displayNameReplaces = displayNameReplaces;
        this.replaceTitle(this.displayNameReplaces);

        this.defaultItemsReplaces = defaultItemsReplaces;

        this.setDefaultItems();

        this.isEmptyItem = this.configSection.getBoolean("fill-empty-slots.enabled");
        this.emptyItem = ItemStackLoader.get(this.configSection.getConfigurationSection("fill-empty-slots.item"));
    }

    public void initialize(
            FileConfiguration guiConfig,
            Map<String, String> displayNameReplaces
    ){
        this.initialize(guiConfig, displayNameReplaces, Map.of());
    }

    public void initialize(
            FileConfiguration guiConfig
    ){
        this.initialize(guiConfig, Map.of());
    }

    public void replaceTitle(Map<String, String> replaces) {
        for (String key : replaces.keySet()) {
            this.title = this.title.replace(key, replaces.get(key));
        }
    }

    public void setDefaultItems(){
        this.defaultItems = new HashMap<>();
        ConfigurationSection defaultItemConfig = this.configSection.getConfigurationSection("default-items");
        for (String key : defaultItemConfig.getKeys(false)) {
            ConfigurationSection itemConfig = defaultItemConfig.getConfigurationSection(key);
            Map<String, String> itemReplaces = defaultItemsReplaces.get(key);
            GuiAction<InventoryClickEvent> action = this.getDefaultActions().get(key);
            if (itemReplaces != null) {
                this.defaultItems.put(key, new NumberedGuiItem(itemConfig, itemReplaces, action));
            } else {
                this.defaultItems.put(key, new NumberedGuiItem(itemConfig, action));
            }
        }
    }

    public void create(SimpleBuilder builder) {
        this.gui = builder
                .title(Component.text(this.title))
                .rows(this.row)
                .create();

        this.gui.setDefaultClickAction(e -> e.setCancelled(true));
    }


    public void create(PaginatedBuilder builder) {
        this.gui = builder
                .title(Component.text(this.title))
                .rows(this.row)
                .create();

        this.gui.setDefaultClickAction(e -> e.setCancelled(true));
    }

    public void populate(boolean fillEmptySlots, Integer navigationRow) {
        this.gui.getInventory().clear();
        for (NumberedGuiItem item : this.defaultItems.values()) {
            if (navigationRow == null){
                this.gui.setItem(item.getSlot(), item.getGuiItem());
            } else {
                this.gui.setItem(navigationRow, item.getSlot() + 1, item.getGuiItem());
            }
        }
        for (NumberedGuiItem item : this.items) {
            if (navigationRow != null)
                this.gui.addItem(item.getGuiItem());
            else {
                this.gui.setItem(item.getSlot(), item.getGuiItem());
            }
        }

        if (fillEmptySlots) {
            if (this.isEmptyItem)
                this.gui.getFiller().fill(ItemBuilder.from(this.emptyItem).asGuiItem());
        }
        this.gui.update();
    }

    public void populate(boolean fillEmptySlots) {
        this.populate(fillEmptySlots, null);
    }

    public void update(){
        if (this.gui.isUpdating()){
            return;
        }
        this.gui.setUpdating(true);
        this.setDefaultItems();
        this.setItems();
        this.gui.getGuiItems().clear();
        Map<@NotNull Integer, @NotNull GuiItem> newGuiItems = new HashMap<>();
        List<NumberedGuiItem> defaultItems = new ArrayList<>(this.defaultItems.values());
        defaultItems.forEach(item -> {
                newGuiItems.put(item.getEntry().getKey(), item.getEntry().getValue());
        });
        this.items.forEach(item -> {
                newGuiItems.put(item.getEntry().getKey(), item.getEntry().getValue());
        });
        this.gui.getGuiItems().putAll(newGuiItems);
        if (this.isEmptyItem)
            this.gui.getFiller().fill(ItemBuilder.from(this.emptyItem).asGuiItem());
        this.gui.update();
        this.gui.setUpdating(false);
    }

    public boolean isViewing(){
        return !this.gui.getInventory().getViewers().isEmpty();
    }

    public void open(Player player) {
        if (!isViewing()){
            this.update();
        }
        this.gui.open(player);
    }

    public abstract void reload(FileConfiguration guiConfig);

    public abstract void setItems();

    public abstract Map<String, GuiAction<InventoryClickEvent>> getDefaultActions();
}
