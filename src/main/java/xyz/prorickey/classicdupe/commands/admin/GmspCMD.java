package xyz.prorickey.classicdupe.commands.admin;

import com.github.antritus.astral.AdvancedPlugin;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.ForRemoval;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

@ForRemoval(reason = "Staff commands should go to their own plugin.")
@Deprecated(forRemoval = true)
public class GmspCMD extends AstralCommand {


    public GmspCMD(AstralDupe main) {
        super(main, "gmsp");
        setPermission("astraldupe.admin.gamemode.spectator");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(args.length < 1) {
            if(sender instanceof ConsoleCommandSender) {
                sender.sendMessage(Utils.cmdMsg("<red>You cannot change console's gamemode"));
                return true;
            }
            Player p = (Player) sender;
            if(!p.hasPermission("admin.gamemode.spectator")) {
                sender.sendMessage("<red>You do not have permission to change your gamemode to spectator mode");
                return true;
            }
            if(p.getGameMode() == GameMode.SPECTATOR) {
                sender.sendMessage(Utils.cmdMsg("<red>You're already in spectator mode"));
                return true;
            }
            p.setGameMode(GameMode.SPECTATOR);
            p.sendMessage(Utils.cmdMsg("<green>Set your gamemode to <yellow>spectator <green>mode"));
        } else {
            Player p = Bukkit.getServer().getPlayer(args[0]);
            if(p == null || !p.isOnline()) {
                sender.sendMessage(Utils.cmdMsg("<red>" + args[0] + " is not currently online"));
                return true;
            }
            if(!sender.hasPermission("admin.gamemode.spectator.others") && !(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(Utils.cmdMsg("<red>You do not have permission to set other player's gamemode to spectator"));
                return true;
            }
            if(p.getGameMode() == GameMode.SPECTATOR) {
                sender.sendMessage(Utils.cmdMsg("<yellow>" + p.getName() + "'s <red>gamemode is already spectator mode"));
                return true;
            }
            p.setGameMode(GameMode.SPECTATOR);
            p.sendMessage(Utils.cmdMsg("<green>Your gamemode has been set to <yellow>spectator<green> mode by <yellow>" + sender.getName()));
            sender.sendMessage(Utils.cmdMsg("<green>Set <yellow>" + p.getName() + "'s <green>gamemode to <yellow>spectator"));
        }
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if(args.length == 1) return (ClassicDupe.getOnlinePlayerUsernames());
        return new ArrayList<>();
    }
}
