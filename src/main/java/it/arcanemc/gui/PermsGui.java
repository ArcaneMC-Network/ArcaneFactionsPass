package it.arcanemc.gui;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import it.arcanemc.data.TimedFaction;
import it.arcanemc.util.loader.ItemStackLoader;
import it.arcanemc.util.NumberedGuiItem;
import it.arcanemc.util.Timer;
import it.arcanemc.util.loader.SoundLoader;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class PermsGui extends GenericGui {
    private final TimedFaction timedFaction;
    private final ItemStack genericPerm;
    private final FileConfiguration messages;
    private final FileConfiguration sounds;
    private final boolean soundEnabled;
    private final GenericGui mainGui;

    public PermsGui(
            FileConfiguration guiConfig,
            FileConfiguration messages,
            FileConfiguration sounds,
            TimedFaction timedFaction,
            GenericGui mainGui
    ) {
        this.initialize(guiConfig.getConfigurationSection("perms-menu"), Map.of(), Map.of(
                "info", Map.of("{time}", Timer.getVerbose(timedFaction.getTimer()))
        ));
        this.timedFaction = timedFaction;
        this.genericPerm = ItemStackLoader.get(guiConfig.getConfigurationSection("perms-menu.perm"));
        this.messages = messages;
        this.sounds = sounds;
        this.soundEnabled = this.sounds.getBoolean("enabled");
        this.mainGui = mainGui;
        this.create(Gui.gui());
        this.setItems();
        this.populate(true);
    }

    @Override
    public void reload(FileConfiguration guiConfig) {
        this.initialize(
                guiConfig.getConfigurationSection("pass-menu"),
                Map.of(),
                Map.of("info", Map.of("{time}", Timer.getVerbose(timedFaction.getTimer())))
        );
    }

    public void playSound(Player p, String soundPath) {
        if (this.soundEnabled) {
            SoundLoader.play(p, this.sounds.getConfigurationSection(soundPath));
        }
    }

    @Override
    public void setItems() {
        this.items = new ArrayList<>();
        ConfigurationSection roleConfig = this.messages.getConfigurationSection("permissions.roles");
        ConfigurationSection toggleConfig = this.messages.getConfigurationSection("permissions.toggle");
        ConfigurationSection slotsConfig = this.configSection.getConfigurationSection("perm.slots");
        for (String key : roleConfig.getKeys(false)) {
            String role = roleConfig.getString(key);
            String state = toggleConfig.getString("disabled");
            if (this.timedFaction.getPermissions().get(key)) {
                state = toggleConfig.getString("enabled");
            }
            ItemStack item = this.genericPerm.clone();
            ItemStackLoader.replacePlaceholders(
                    item,
                    Map.of("{perm}", role),
                    Map.of("{state}", state)
            );
            Optional<String> optionalPerm = slotsConfig.getKeys(false).stream().filter(perm -> perm.equalsIgnoreCase(key)).findFirst();

            optionalPerm.ifPresent(perm -> this.items.add(
                    new NumberedGuiItem(
                            item,
                            slotsConfig.getInt(perm),
                            e -> {
                                String s = toggleConfig.getString("disabled");
                                if (this.timedFaction.getPermissions().toggle(key)) {
                                    s = toggleConfig.getString("enabled");
                                }
                                ItemStack i = this.genericPerm.clone();
                                ItemStackLoader.replacePlaceholders(
                                        i,
                                        Map.of("{perm}", role),
                                        Map.of("{state}", s)
                                );
                                GuiItem oldItem = this.gui.getGuiItem(slotsConfig.getInt(perm));
                                if (oldItem != null) {
                                    oldItem.setItemStack(i);
                                    this.gui.updateItem(slotsConfig.getInt(perm), oldItem);
                                    this.playSound((Player) e.getWhoClicked(), "sounds.toggle-permission");
                                }
                            }
                    )
            ));
        }
    }

    public Map<String, GuiAction<InventoryClickEvent>> getDefaultActions() {
        Map<String, GuiAction<InventoryClickEvent>> actions = new HashMap<>();
        actions.put("back", e -> {
            FPlayer p = FPlayers.getInstance().getByPlayer((Player) e.getWhoClicked());
            if (p == null) {
                return;
            }
            this.mainGui.gui.open(p.getPlayer());
            this.playSound((Player) e.getWhoClicked(), "sounds.back-button");
        });

        return actions;
    }
}

