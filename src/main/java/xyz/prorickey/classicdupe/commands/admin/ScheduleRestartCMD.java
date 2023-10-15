package xyz.prorickey.classicdupe.commands.admin;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.commands.AstralCommand; // Updated import
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class ScheduleRestartCMD extends AstralCommand {

    public ScheduleRestartCMD(AstralDupe main) {
        super(main, "reboot");
        setPermission("astraldupe.admin.reboot");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length > 0 && args[0].equals("cancel") && ClassicDupe.restartInProgress) {
            ClassicDupe.scheduledRestartCanceled = true;
            ClassicDupe.restartInProgress = false;
            ClassicDupe.rawBroadcast("<green>The server restart has been cancelled");
            return true;
        }
        ClassicDupe.scheduleRestart();
        sender.sendMessage(Utils.cmdMsg("<green>The server will restart in 60 seconds"));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("cancel");
        }
        return new ArrayList<>();
    }
}
