package xyz.prorickey.classicdupe.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.proutils.TabComplete;

import java.util.ArrayList;
import java.util.List;

public class GmsCMD implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length < 1) {
            if(sender instanceof ConsoleCommandSender) {
                sender.sendMessage(Utils.cmdMsg("<red>You cannot change console's gamemode"));
                return true;
            }
            Player p = (Player) sender;
            if(!p.hasPermission("admin.gamemode.survival")) {
                sender.sendMessage("<red>You do not have permission to change your gamemode to survival mode");
                return true;
            }
            if(p.getGameMode() == GameMode.SURVIVAL) {
                sender.sendMessage(Utils.cmdMsg("<red>You're already in survival mode"));
                return true;
            }
            p.setGameMode(GameMode.SURVIVAL);
            p.sendMessage(Utils.cmdMsg("<green>Set your gamemode to <yellow>survival <green>mode"));
        } else {
            Player p = Bukkit.getServer().getPlayer(args[0]);
            if(p == null || !p.isOnline()) {
                sender.sendMessage(Utils.cmdMsg("<red>" + args[0] + " is not currently online"));
                return true;
            }
            if(!sender.hasPermission("admin.gamemode.survival.others") && !(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(Utils.cmdMsg("<red>You do not have permission to set other player's gamemode to survival"));
                return true;
            }
            if(p.getGameMode() == GameMode.SURVIVAL) {
                sender.sendMessage(Utils.cmdMsg("<yellow>" + p.getName() + "'s <red>gamemode is already survival mode"));
                return true;
            }
            p.setGameMode(GameMode.SURVIVAL);
            p.sendMessage(Utils.cmdMsg("<green>Your gamemode has been set to <yellow>survival<green> mode by <yellow>" + sender.getName()));
            sender.sendMessage(Utils.cmdMsg("<green>Set <yellow>" + p.getName() + "'s <green>gamemode to <yellow>survival"));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 1) return TabComplete.tabCompletionsSearch(args[0], ClassicDupe.getOnlinePlayerUsernames());
        return new ArrayList<>();
    }

}
