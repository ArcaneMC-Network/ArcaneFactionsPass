package it.arcanemc.gui;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import it.arcanemc.data.Reward;
import it.arcanemc.data.TimedFaction;
import it.arcanemc.util.*;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
public class PassGui extends GenericGui {
    private final TimedFaction timedFaction;
    private final FileConfiguration messages;
    private final GenericGui mainGui;
    private final ArrayList<Reward> rewards;
    private final ItemStack genericClaimed;
    private final int navigationRow;
    private final boolean fillNavigationRowEmptySlot;
    private final ItemStack emptyNavigationItem;

    public PassGui(FileConfiguration guiConfig, FileConfiguration messages, TimedFaction timedFaction, GenericGui mainGui, ArrayList<Reward> rewards) {
        ConfigurationSection passMenuSection = guiConfig.getConfigurationSection("pass-menu");
        if (passMenuSection != null) {
            Map<String, String> replaces = new HashMap<>();
            if (!rewards.isEmpty()){
                replaces.put("{name}", Format.capitalize(rewards.get(0).getPass().getName()));
            }
            this.initialize(passMenuSection, replaces, Map.of("info", Map.of("{time}", Timer.getVerbose(timedFaction.getTimer()))));
        }
        this.timedFaction = timedFaction;
        this.messages = messages;
        this.mainGui = mainGui;
        this.rewards = rewards;
        this.genericClaimed = ItemStackLoader.get(guiConfig.getConfigurationSection("pass-menu.claimed-item"));
        this.navigationRow = guiConfig.getInt("pass-menu.navigation-row");
        this.fillNavigationRowEmptySlot = guiConfig.getBoolean("pass-menu.fill-navigation-row-empty-slot.enabled");
        this.emptyNavigationItem = ItemStackLoader.get(guiConfig.getConfigurationSection("pass-menu.fill-navigation-row-empty-slot.item"));
        this.create(Gui.paginated());
        this.setItems(guiConfig);
        this.populate(true, this.navigationRow);
    }

    @Override
    public void reload(FileConfiguration guiConfig) {
        ConfigurationSection passMenuSection = guiConfig.getConfigurationSection("pass-menu");
        if (passMenuSection != null) {
            this.initialize(passMenuSection, Map.of(), Map.of("info", Map.of("{time}", Timer.getVerbose(timedFaction.getTimer()))));
        }
    }

    @Override
    public void setItems(ConfigurationSection guiConfig) {
        this.items = new ArrayList<>();
        for (Reward reward : this.rewards) {
            ItemStack itemStack = reward.getItemStack().clone();
            long timedLeft = reward.getRequiredTime() - timedFaction.getTimer();
            String formattedTimer = Timer.getVerbose(timedLeft);
            if ("0s".equals(formattedTimer)) {
                formattedTimer = this.messages.getString("reward.available");
            }
            Map<String, String> replaces = Map.of("{time}", formattedTimer);
            ItemStackLoader.replacePlaceholders(itemStack, Map.of(), replaces);

            this.items.add(new NumberedGuiItem(itemStack, 0, e -> handleRewardClick(e, reward, guiConfig)));
        }
    }

    private void handleRewardClick(InventoryClickEvent e, Reward reward, ConfigurationSection guiConfig) {
        FPlayer player = FPlayers.getInstance().getByPlayer((Player) e.getWhoClicked());
        if (player == null) return;

        long timeLeft = reward.getRequiredTime() - timedFaction.getTimer();
        if (!reward.getPass().getIsDefault() && !timedFaction.getAvailablePassNames().contains(reward.getPass().getName())) {
            Msg.player(player.getPlayer(), messages.getString("reward.claim.locked"));
            return;
        }
        if (timeLeft > 0) {
            Msg.player(player.getPlayer(), messages.getString("reward.claim.deny"));
        } else if (!timedFaction.getPermissions().get(player.getRole().nicename)) {
            Msg.player(player.getPlayer(), messages.getString("permissions.deny"));
        } else if (timedFaction.claimReward(player, reward)) {
            String successMessage = messages.getString("reward.claim.success")
                    .replace("{number}", String.valueOf(rewards.indexOf(reward) + 1))
                    .replace("{name}", reward.getPass().getName());
            if (guiConfig.getBoolean("settings.broadcast-messages.claim-reward.success")) {
                Msg.all(successMessage);
            } else {
                Msg.player(player.getPlayer(), successMessage);
            }
        } else {
            Msg.player(player.getPlayer(), messages.getString("reward.claim.already-claimed"));
        }
    }

    @Override
    public Map<String, GuiAction<InventoryClickEvent>> getDefaultActions() {
        return Map.of(
                "previous", e -> ((PaginatedGui) this.gui).previous(),
                "next", e -> {
                    ((PaginatedGui) this.gui).next();
                },
                "back", e -> {
                    FPlayer player = FPlayers.getInstance().getByPlayer((Player) e.getWhoClicked());
                    if (player != null) {
                        mainGui.gui.open(player.getPlayer());
                    }
                }
        );
    }

    @Override
    public void populate(boolean fillEmptySlots, Integer navigationRow) {
        if (this.fillNavigationRowEmptySlot){
            this.gui.getFiller().fillBetweenPoints(
                    navigationRow,
                    1,
                    navigationRow,
                    9,
                    ItemBuilder.from(this.emptyNavigationItem).asGuiItem()
            );
        }

        super.populate(fillEmptySlots, navigationRow);
    }
}