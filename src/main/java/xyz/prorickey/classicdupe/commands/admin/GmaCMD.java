package xyz.prorickey.classicdupe.commands.admin;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.ForRemoval;
import me.antritus.astraldupe.utils.PlayerUtils;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

@ForRemoval(reason = "Staff commands should go to their own plugin.")
@Deprecated(forRemoval = true)
public class GmaCMD extends AstralCommand {


    public GmaCMD(AstralDupe main) {
        super(main, "gma");
        setPermission("astraldupe.admin.gamemode.adventure");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(args.length < 1) {
            if(sender instanceof ConsoleCommandSender) {
                sender.sendMessage(Utils.cmdMsg("<red>You cannot change console's gamemode"));
                return true;
            }
            Player p = (Player) sender;
            if(!p.hasPermission("admin.gamemode.adventure")) {
                sender.sendMessage("<red>You do not have permission to change your gamemode to adventure mode");
                return true;
            }
            if(p.getGameMode() == GameMode.ADVENTURE) {
                sender.sendMessage(Utils.cmdMsg("<red>You're already in adventure mode"));
                return true;
            }
            p.setGameMode(GameMode.ADVENTURE);
            p.sendMessage(Utils.cmdMsg("<green>Set your gamemode to <yellow>adventure <green>mode"));
        } else {
            Player p = Bukkit.getServer().getPlayer(args[0]);
            if(p == null || !p.isOnline()) {
                sender.sendMessage(Utils.cmdMsg("<red>" + args[0] + " is not currently online"));
                return true;
            }
            if(!sender.hasPermission("admin.gamemode.adventure.others") && !(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(Utils.cmdMsg("<red>You do not have permission to set other player's gamemode to adventure"));
                return true;
            }
            if(p.getGameMode() == GameMode.ADVENTURE) {
                sender.sendMessage(Utils.cmdMsg("<yellow>" + p.getName() + "'s <red>gamemode is already adventure mode"));
                return true;
            }
            p.setGameMode(GameMode.ADVENTURE);
            p.sendMessage(Utils.cmdMsg("<green>Your gamemode has been set to <yellow>adventure<green> mode by <yellow>" + sender.getName()));
            sender.sendMessage(Utils.cmdMsg("<green>Set <yellow>" + p.getName() + "'s <green>gamemode to <yellow>adventure"));
        }
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if(args.length == 1) return PlayerUtils.getVisiblePlayerNames(sender);
        return new ArrayList<>();
    }
}
