package it.arcanemc.gui;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.perms.Role;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.Gui;
import it.arcanemc.data.Pass;
import it.arcanemc.data.TimedFaction;
import it.arcanemc.manager.PassManager;
import it.arcanemc.util.Msg;
import it.arcanemc.util.NumberedGuiItem;
import it.arcanemc.util.Timer;
import it.arcanemc.util.loader.SoundLoader;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;

@Getter
public class MainGui extends GenericGui{
    private final TimedFaction timedFaction;
    private final GenericGui permsGui;
    private final FileConfiguration messages;
    private final FileConfiguration sounds;
    private final boolean soundEnabled;
    private final Map<Pass, GenericGui> passesGui;

    public MainGui(
            FileConfiguration guiConfig,
            FileConfiguration messages,
            FileConfiguration sounds,
            TimedFaction timedFaction,
            PassManager passes
    ) {
        this.timedFaction = timedFaction;
        this.messages = messages;
        this.sounds = sounds;
        this.soundEnabled = this.sounds.getBoolean("enabled");
        this.permsGui = new PermsGui(guiConfig, messages, sounds, timedFaction, this);
        this.passesGui = new HashMap<>();
        this.initialize(
                guiConfig.getConfigurationSection("main-menu"),
                Map.of(),
                Map.of("info", Map.of("{time}", Timer.getVerbose(timedFaction.getTimer())))
        );
        passes.getPasses().keySet().forEach(pass -> this.passesGui.put(pass, new PassGui(
                guiConfig,
                messages,
                sounds,
                pass.getName(),
                timedFaction,
                this,
                passes.getPasses().get(pass))
        ));
        this.setItems();
        this.setDefaultItems();
        this.create(Gui.gui());
        this.populate(true);
    }

    @Override
    public void reload(FileConfiguration guiConfig) {
        this.getPermsGui().reload(guiConfig);
        this.getPassesGui().values().forEach(gui -> gui.reload(guiConfig));
        this.initialize(
                guiConfig.getConfigurationSection("main-menu"),
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
        List<Map<?, ?>> passList = this.configSection.getMapList("passes");

        for (Map<?, ?> passMap : passList) {
            String name = (String) passMap.get("name");
            int slot = (Integer) passMap.get("slot");
            Optional<Pass> optionalPass = this.passesGui.keySet().stream()
                    .filter(pass -> pass.getName().equalsIgnoreCase(name))
                    .findFirst();
            optionalPass.ifPresent(pass -> this.items.add(
                    new NumberedGuiItem(
                            pass.getItemStack(),
                            slot,
                            e -> {
                                FPlayer p = FPlayers.getInstance().getByPlayer((Player) e.getWhoClicked());
                                if (p == null) {
                                    return;
                                }
                                this.playSound(p.getPlayer(), "sounds.open-sub-gui");
                                this.passesGui.get(pass).gui.open(p.getPlayer());
                            }
                    )
            ));
        }
    }

    public Map<String, GuiAction<InventoryClickEvent>> getDefaultActions(){
        Map<String, GuiAction<InventoryClickEvent>> actions = new HashMap<>();
        actions.put("perms", e -> {
            FPlayer p = FPlayers.getInstance().getByPlayer((Player) e.getWhoClicked());
            if (p == null) {
                return;
            }
            if (p.getRole() != Role.ADMIN){
                Msg.player(p.getPlayer(), this.messages.getString("permissions.only-leader"));
                this.playSound(p.getPlayer(), "sounds.error");
            } else {
                this.playSound(p.getPlayer(), "sounds.open-sub-gui");
                this.permsGui.gui.open(p.getPlayer());
            }
        });

        return actions;
    }

    public boolean isViewing() {
        boolean thisGuiViewing = super.isViewing();
        boolean permsGuiViewing = this.permsGui.isViewing();
        boolean passesGuiViewing = this.passesGui.values().stream().anyMatch(GenericGui::isViewing);
        return thisGuiViewing || permsGuiViewing || passesGuiViewing;
    }

    public void update(){
        this.defaultItemsReplaces = Map.of("info", Map.of("{time}", Timer.getVerbose(timedFaction.getTimer())));
        super.update();
        // this.permsGui.update();
        this.passesGui.values().forEach(GenericGui::update);
    }
}
