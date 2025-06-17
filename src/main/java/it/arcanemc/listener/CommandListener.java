package it.arcanemc.listener;

import it.arcanemc.command.CommandHandler;
import it.arcanemc.manager.FactionPassManager;
import it.arcanemc.util.Msg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {
    private final FactionPassManager factionPassManager;

    public CommandListener(FactionPassManager factionPassManager) {
        this.factionPassManager = factionPassManager;
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase();

        switch (command) {
            case "/faction pass":
            case "/f pass": {
                event.setCancelled(true);
                CommandHandler.openPassMainGUI(this.factionPassManager, player);
                break;
            }

            case "/faction pass reload":
            case "/f pass reload": {
                event.setCancelled(true);
                CommandHandler.reload(this.factionPassManager);
                break;
            }
        }

        if (command.startsWith("/faction pass show") || command.startsWith("/f pass show")) {
            if (!player.hasPermission("arcanefactionspass.admin.show")) {
                Msg.player(player, this.factionPassManager.getPlugin().getConfigurationManager().get("message").getString("errors.no-permission"));
                return;
            }
            event.setCancelled(true);
            String[] parts = event.getMessage().split(" ");
            if (parts.length != 4) {
                Msg.player(player, this.factionPassManager.getPlugin().getConfigurationManager().get("message").getString("errors.invalid-command"));
                return;
            }
            String factionName = parts[3];
            CommandHandler.showTimedFactionAvailablePasses(this.factionPassManager, factionName, player);
            return;
        }

        if (command.startsWith("/faction pass add") || command.startsWith("/f pass add")) {
            if (!player.hasPermission("arcanefactionspass.admin.add")) {
                Msg.player(player, this.factionPassManager.getPlugin().getConfigurationManager().get("message").getString("errors.no-permission"));
                return;
            }
            event.setCancelled(true);
            String[] parts = event.getMessage().split(" ");
            if (parts.length != 5) {
                Msg.player(player, this.factionPassManager.getPlugin().getConfigurationManager().get("message").getString("errors.invalid-command"));
                return;
            }
            String playerName = parts[3];
            String passName = parts[4];
            CommandHandler.addPassToPlayer(this.factionPassManager, playerName, passName, player);
            return;
        }

        if (command.startsWith("/faction pass remove") || command.startsWith("/f pass remove")) {
            if (!player.hasPermission("arcanefactionspass.admin.add")) {
                Msg.player(player, this.factionPassManager.getPlugin().getConfigurationManager().get("message").getString("errors.no-permission"));
                return;
            }
            event.setCancelled(true);
            String[] parts = event.getMessage().split(" ");
            if (parts.length != 5) {
                Msg.player(player, this.factionPassManager.getPlugin().getConfigurationManager().get("message").getString("errors.invalid-command"));
                return;
            }
            String factionName = parts[3];
            String passName = parts[4];
            CommandHandler.removePassToFaction(this.factionPassManager, factionName, passName, player);
            return;
        }
    }
}
