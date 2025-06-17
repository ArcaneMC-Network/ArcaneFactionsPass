package it.arcanemc.command;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import it.arcanemc.data.Pass;
import it.arcanemc.gui.MainGui;
import it.arcanemc.manager.FactionPassManager;
import it.arcanemc.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

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

    public static void addPassToPlayer(FactionPassManager factionPassManager, String playerName, String passName, CommandSender sender) {
        Optional<Pass> optionalPass = factionPassManager.getPassManager().getPasses().keySet().stream().filter(pass -> pass.getName().equalsIgnoreCase(passName)).findFirst();
        Player player = Bukkit.getPlayer(playerName);

        if (player == null) {
            Msg.sender(sender, factionPassManager.getPlugin().getConfigurationManager().get("message").getString("errors.player-not-found"));
            return;
        }
        Faction faction = FPlayers.getInstance().getByPlayer(player).getFaction();

        if (faction == null || faction.isPermanent()) {
            Msg.sender(sender, factionPassManager.getPlugin().getConfigurationManager().get("message").getString("errors.faction-not-found"));
            return;
        }

        if (optionalPass.isEmpty()) {
            Msg.sender(sender, factionPassManager.getPlugin().getConfigurationManager().get("message").getString("errors.pass-not-found")
                    .replace("{pass}", passName));
        } else {
            Pass newPass = optionalPass.get();
            factionPassManager.getTimedFactionManager().get().stream()
                    .filter(timedFaction -> timedFaction.getFaction().getId().equalsIgnoreCase(faction.getId()))
                    .findFirst()
                    .ifPresentOrElse(
                            timedFaction -> {
                                if (timedFaction.unlockPass(newPass)) {
                                    Msg.sender(sender,
                                            factionPassManager.getPlugin().getConfigurationManager().get("message").getString("admin.add.success")
                                                    .replace("{faction}", timedFaction.getFaction().getTag())
                                                    .replace("{pass}", newPass.getName())
                                    );
                                    Msg.all(factionPassManager.getPlugin().getConfigurationManager().get("message").getString("unlock-pass.success")
                                            .replace("{faction}", timedFaction.getFaction().getTag())
                                            .replace("{pass}", newPass.getName()));
                                } else {
                                    Msg.sender(sender,
                                            factionPassManager.getPlugin().getConfigurationManager().get("message").getString("admin.add.deny")
                                                    .replace("{faction}", timedFaction.getFaction().getTag())
                                                    .replace("{pass}", newPass.getName())
                                    );
                                }
                            },
                            () -> Msg.sender(sender, factionPassManager.getPlugin().getConfigurationManager().get("message").getString("errors.faction-not-found"))
                    );
        }
    }

    public static void removePassToFaction(FactionPassManager factionPassManager, String factionName, String passName, CommandSender sender) {
        Optional<Pass> optionalPass = factionPassManager.getPassManager().getPasses().keySet().stream().filter(pass -> pass.getName().equalsIgnoreCase(passName)).findFirst();
        Faction faction = Factions.getInstance().getByTag(factionName.toLowerCase());

        if (faction == null || faction.isPermanent()) {
            Msg.sender(sender, factionPassManager.getPlugin().getConfigurationManager().get("message").getString("errors.faction-not-found"));
            return;
        }

        if (optionalPass.isEmpty()) {
            Msg.sender(sender, factionPassManager.getPlugin().getConfigurationManager().get("message").getString("errors.pass-not-found")
                    .replace("{pass}", passName));
        } else {
            Pass oldPass = optionalPass.get();
            factionPassManager.getTimedFactionManager().get().stream()
                    .filter(timedFaction -> timedFaction.getFaction().getId().equalsIgnoreCase(faction.getId()))
                    .findFirst()
                    .ifPresentOrElse(
                            timedFaction -> {
                                if (timedFaction.removePass(oldPass)) {
                                    Msg.sender(sender,
                                            factionPassManager.getPlugin().getConfigurationManager().get("message").getString("admin.remove.success")
                                                    .replace("{faction}", timedFaction.getFaction().getTag())
                                                    .replace("{pass}", oldPass.getName())
                                    );
                                } else {
                                    Msg.sender(sender,
                                            factionPassManager.getPlugin().getConfigurationManager().get("message").getString("admin.remove.deny")
                                                    .replace("{faction}", timedFaction.getFaction().getTag())
                                                    .replace("{pass}", oldPass.getName())
                                    );
                                }
                            },
                            () -> Msg.sender(sender, factionPassManager.getPlugin().getConfigurationManager().get("message").getString("errors.faction-not-found"))
                    );
        }
    }
}
