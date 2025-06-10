package it.arcanemc.command;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import it.arcanemc.data.Pass;
import it.arcanemc.gui.MainGui;
import it.arcanemc.manager.FactionPassManager;
import org.bukkit.entity.Player;

public class CommandHandler {
    public static void reload(FactionPassManager factionPassManager){
        factionPassManager.load();
        factionPassManager.getTimedFactionManager().start();
    }

    public static void openPassMainGUI(FactionPassManager factionPassManager, Player player){
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (fPlayer == null) {
            player.sendMessage(factionPassManager.getPlugin().getConfigurationManager().get("messages").getString("error.not-in-faction"));
            return;
        }
        MainGui mainGui = factionPassManager.findMainGui(fPlayer.getFaction());
        mainGui.getGui().open(player);
    }

    public static void openPassPermsGUI(FactionPassManager factionPassManager, Player player){
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (fPlayer == null) {
            player.sendMessage(factionPassManager.getPlugin().getConfigurationManager().get("messages").getString("error.not-in-faction"));
            return;
        }
        MainGui mainGui = factionPassManager.findMainGui(fPlayer.getFaction());
        mainGui.getPermsGui().getGui().open(player);
    }

    public static void openPassGUI(FactionPassManager factionPassManager, Player player, Pass pass){
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        if (fPlayer == null) {
            player.sendMessage(factionPassManager.getPlugin().getConfigurationManager().get("messages").getString("error.not-in-faction"));
            return;
        }
        MainGui mainGui = factionPassManager.findMainGui(fPlayer.getFaction());
        mainGui.getPassesGui().get(pass).getGui().open(player);
    }
}
