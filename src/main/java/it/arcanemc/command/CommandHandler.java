package it.arcanemc.command;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import it.arcanemc.gui.MainGui;
import it.arcanemc.manager.FactionPassManager;
import it.arcanemc.util.Msg;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler {
    public static void reload(FactionPassManager factionPassManager){
        factionPassManager.load();
    }

    public static void openPassMainGUI(FactionPassManager factionPassManager, Player player){
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (fPlayer == null) {
            player.sendMessage(factionPassManager.getPlugin().getConfigurationManager().get("message").getString("error.not-in-faction"));
            return;
        }
        MainGui mainGui = factionPassManager.findMainGui(fPlayer.getFaction());
        mainGui.open(player);
    }

    public static void showTimedFactionAvailablePasses(FactionPassManager factionPassManager, String factionName, CommandSender sender){
        factionPassManager.getTimedFactionManager().get().stream()
                .filter(timedFaction -> timedFaction.getFaction().getTag().equalsIgnoreCase(factionName))
                .findFirst()
                .ifPresentOrElse(
                        timedFaction -> {
                            Msg.sender(sender,
                                    factionPassManager.getPlugin().getConfigurationManager().get("message").getString("admin.show.success")
                                            .replace("{faction}", timedFaction.getFaction().getTag())
                                            .replace("{passes}", timedFaction.getAvailablePassNamesString())
                            );
                        },
                        () -> Msg.sender(sender, factionPassManager.getPlugin().getConfigurationManager().get("message").getString("errors.faction-not-found"))
                );

    }
}
