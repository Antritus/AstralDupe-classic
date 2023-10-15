package xyz.prorickey.classicdupe.commands.admin;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.utils.PlayerUtils;
import me.antritus.astraldupe.commands.AstralCommand; // Updated import
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class SudoCMD extends AstralCommand {

    public SudoCMD(AstralDupe main) {
        super(main, "sudo");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Utils.cmdMsg("<red>You need to provide a player to sudo"));
            return true;
        } else if (args.length == 1) {
            sender.sendMessage(Utils.cmdMsg("<red>You need to provide a message or command for the player to execute"));
            return true;
        } else {
            Player p = Bukkit.getServer().getPlayer(args[0]);
            if (p == null || !p.isOnline()) {
                sender.sendMessage(Utils.cmdMsg("<red>That player is not currently on the server"));
                return true;
            }
            StringBuilder msg = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                msg.append(args[i]).append(" ");
            }
            String message = msg.toString();
            if (message.startsWith("/")) {
                p.chat(message);
                sender.sendMessage(Utils.cmdMsg("<green>Made <yellow>" + p.getName() + "<green> execute the command <yellow>" + message));
            } else {
                p.chat(message);
                sender.sendMessage(Utils.cmdMsg("<green>Made <yellow>" + p.getName() + "<green> send the message <yellow>" + message));
            }
        }
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return PlayerUtils.getVisiblePlayerNames(sender);
        }
        return new ArrayList<>();
    }
}
