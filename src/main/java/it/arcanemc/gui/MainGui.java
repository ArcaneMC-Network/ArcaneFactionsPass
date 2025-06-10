package it.arcanemc.gui;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.perms.Role;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.Gui;
import it.arcanemc.data.Pass;
import it.arcanemc.data.TimedFaction;
import it.arcanemc.managers.PassManager;
import it.arcanemc.utils.Msg;
import it.arcanemc.utils.NumberedGuiItem;
import it.arcanemc.utils.Timer;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.*;

@Getter
public class MainGui extends GenericGui{
    private final TimedFaction timedFaction;
    private final GenericGui permsGui;
    private final FileConfiguration messages;
    private final HashMap<Pass, GenericGui> passesGui;

    public MainGui(
            FileConfiguration guiConfig,
            FileConfiguration messages,
            TimedFaction timedFaction,
            PassManager passes
    ) {
        this.timedFaction = timedFaction;
        this.messages = messages;
        this.permsGui = new PermsGui(guiConfig, messages, timedFaction, this);
        this.passesGui = new HashMap<>();
        this.initialize(
                guiConfig.getConfigurationSection("main-menu"),
                Map.of(),
                Map.of("info", Map.of("{time}", Timer.getVerbose(timedFaction.getTimer())))
        );
        passes.getPasses().keySet().forEach(pass -> this.passesGui.put(pass, new PassGui(
                guiConfig,
                messages,
                timedFaction,
                this,
                passes.getPasses().get(pass))
        ));
        this.setItems(guiConfig);
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

    @Override
    public void setItems(ConfigurationSection guiConfig) {
        this.items = new ArrayList<>();
        List<Map<?, ?>> passList = guiConfig.getMapList("main-menu.passes");

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
            } else {
                this.permsGui.gui.open(p.getPlayer());
            }
        });

        return actions;
    }
}
