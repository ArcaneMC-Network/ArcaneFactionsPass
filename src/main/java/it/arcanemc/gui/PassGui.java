package it.arcanemc.gui;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import it.arcanemc.data.Reward;
import it.arcanemc.data.TimedFaction;
import it.arcanemc.util.*;
import it.arcanemc.util.loader.ItemStackLoader;
import it.arcanemc.util.loader.SoundLoader;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class PassGui extends GenericGui {
    private final TimedFaction timedFaction;
    private final FileConfiguration messages;
    private final FileConfiguration sounds;
    private final boolean soundEnabled;
    private final String name;
    private final GenericGui mainGui;
    private final List<Reward> rewards;
    private final ItemStack genericClaimed;
    private final int navigationRow;
    private final boolean fillNavigationRowEmptySlot;
    private final ItemStack emptyNavigationItem;

    public PassGui(FileConfiguration guiConfig, FileConfiguration messages, FileConfiguration sounds, String name, TimedFaction timedFaction, GenericGui mainGui, ArrayList<Reward> rewards) {
        ConfigurationSection passMenuSection = guiConfig.getConfigurationSection("pass-menu");
        this.name = Format.capitalize(name);
        this.initialize(passMenuSection, Map.of("{name}", this.name), Map.of("info", Map.of("{time}", Timer.getVerbose(timedFaction.getTimer()))));
        this.timedFaction = timedFaction;
        this.messages = messages;
        this.sounds = sounds;
        this.soundEnabled = this.sounds.getBoolean("enabled");
        this.mainGui = mainGui;
        this.rewards = rewards;
        this.genericClaimed = ItemStackLoader.get(guiConfig.getConfigurationSection("pass-menu.claimed-item"));
        this.navigationRow = guiConfig.getInt("pass-menu.navigation-row");
        this.fillNavigationRowEmptySlot = guiConfig.getBoolean("pass-menu.fill-navigation-row-empty-slot.enabled");
        this.emptyNavigationItem = ItemStackLoader.get(guiConfig.getConfigurationSection("pass-menu.fill-navigation-row-empty-slot.item"));
        this.create(Gui.paginated());
        this.setItems();
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
    public void setItems() {
        this.items = new ArrayList<>();
        for (Reward reward : this.rewards) {
            ItemStack itemStack = this.genericClaimed.clone();
            if (!this.timedFaction.getClaimedRewardNames().contains(reward.getPassRewardName())){
                itemStack = reward.getItemStack().clone();
                long timedLeft = reward.getRequiredTime() - timedFaction.getTimer();
                String formattedTimer = Timer.getVerbose(timedLeft);
                if ("0s".equals(formattedTimer)) {
                    formattedTimer = this.messages.getString("reward.available");
                }
                Map<String, String> replaces = Map.of("{time}", formattedTimer);
                ItemStackLoader.replacePlaceholders(itemStack, Map.of(), replaces);
            }

            this.items.add(new NumberedGuiItem(itemStack, 0, e -> handleRewardClick(e, reward)));
        }
    }

    public void playSound(Player p, String soundPath) {
        if (this.soundEnabled) {
            SoundLoader.play(p, this.sounds.getConfigurationSection(soundPath));
        }
    }

    private void handleRewardClick(InventoryClickEvent e, Reward reward) {
        FPlayer player = FPlayers.getInstance().getByPlayer((Player) e.getWhoClicked());
        if (player == null) return;

        int slot = e.getSlot();

        long timeLeft = reward.getRequiredTime() - timedFaction.getTimer();
        if (!reward.getPass().getIsDefault() && !timedFaction.getAvailablePassNames().contains(reward.getPass().getName())) {
            Msg.player(player.getPlayer(), messages.getString("reward.claim.locked")
                    .replace("{name}", this.name));
            this.playSound(player.getPlayer(), "sounds.pass-locked");
            return;
        }
        if (timeLeft > 0) {
            Msg.player(player.getPlayer(), messages.getString("reward.claim.deny"));
            this.playSound(player.getPlayer(), "sounds.reward-claim-failed");
        } else if (!timedFaction.getPermissions().get(player.getRole().value)) {
            Msg.player(player.getPlayer(), messages.getString("permissions.deny"));
            this.playSound(player.getPlayer(), "sounds.error");
        } else if (timedFaction.claimReward(player, reward)) {
            String successMessage = messages.getString("reward.claim.success")
                    .replace("{number}", String.valueOf(rewards.indexOf(reward) + 1))
                    .replace("{name}", this.name);
            ItemStack claimedItem = this.genericClaimed.clone();
            ((PaginatedGui) this.gui).updatePageItem(slot, claimedItem);
            this.refresh();
            Msg.player(player.getPlayer(), successMessage);
            this.playSound(player.getPlayer(), "sounds.reward-claimed");
        } else {
            Msg.player(player.getPlayer(), messages.getString("reward.claim.already-claimed"));
            this.playSound(player.getPlayer(), "sounds.reward-already-claimed");
        }
    }

    @Override
    public Map<String, GuiAction<InventoryClickEvent>> getDefaultActions() {
        return Map.of(
                "previous", e -> {
                    ((PaginatedGui) this.gui).previous();
                    this.playSound((Player) e.getWhoClicked(), "sounds.previous-page");
                },
                "next", e -> {
                    ((PaginatedGui) this.gui).next();
                    this.playSound((Player) e.getWhoClicked(), "sounds.next-page");
                },
                "back", e -> {
                    FPlayer player = FPlayers.getInstance().getByPlayer((Player) e.getWhoClicked());
                    if (player != null) {
                        mainGui.gui.open(player.getPlayer());
                        this.playSound(player.getPlayer(), "sounds.back-button");
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

    public void refresh() {
        for (HumanEntity viewer : new ArrayList<>(this.gui.getInventory().getViewers())) ((Player) viewer).updateInventory();
    }

    public void update() {
        if (this.gui.isUpdating()){
            return;
        }
        this.gui.setUpdating(true);
        this.defaultItemsReplaces = Map.of("info", Map.of("{time}", Timer.getVerbose(timedFaction.getTimer())));
        this.setDefaultItems();
        this.setItems();
        this.gui.getGuiItems().clear();
        ((PaginatedGui) this.gui).clearPageItems();
        if (this.fillNavigationRowEmptySlot){
            this.gui.getFiller().fillBetweenPoints(
                    navigationRow,
                    1,
                    navigationRow,
                    9,
                    ItemBuilder.from(this.emptyNavigationItem).asGuiItem()
            );
        }
        Map<@NotNull Integer, @NotNull GuiItem> newGuiItems = new HashMap<>();
        List<NumberedGuiItem> defaultItems = new ArrayList<>(this.defaultItems.values());
        defaultItems.forEach(item -> {
            newGuiItems.put((this.navigationRow - 1)*9+item.getEntry().getKey(), item.getEntry().getValue());
        });
        this.gui.getGuiItems().putAll(newGuiItems);
        this.gui.addItem(this.items.stream().map(NumberedGuiItem::getGuiItem).toArray(GuiItem[]::new));
        this.gui.update();
        this.refresh();
        this.gui.setUpdating(false);
    }
}