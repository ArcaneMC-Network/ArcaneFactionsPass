package it.arcanemc.command;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import it.arcanemc.manager.FactionPassManager;
import it.arcanemc.util.Colors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(name = "fpass")
public class FPassCommand {
    private final FactionPassManager factionPassManager;

    public FPassCommand(FactionPassManager factionPassManager) {
        this.factionPassManager = factionPassManager;
    }

    @Execute
    void executeFPass(@Context Player player) {
        CommandHandler.openPassMainGUI(this.factionPassManager, player);
    }

    @Execute(name="reload")
    @Permission("arcanefactionspass.admin.reload")
    void executeFPassReload(@Context CommandSender commandSender) {
        try {
            CommandHandler.reload(this.factionPassManager);
            commandSender.sendMessage(Colors.translate(factionPassManager.getPlugin().getConfigurationManager().get("message").getString("admin.reload.success")));
        } catch (Exception e){
            commandSender.sendMessage(Colors.translate(factionPassManager.getPlugin().getConfigurationManager().get("message").getString("admin.reload.deny")));
            e.printStackTrace();
        }
    }

    @Execute(name="show")
    @Permission("arcanefactionspass.admin.show")
    void executeFPassShow(
            @Context CommandSender commandSender,
            @Arg String factionName
    ) {
        CommandHandler.showTimedFactionAvailablePasses(this.factionPassManager, factionName, commandSender);
    }
}