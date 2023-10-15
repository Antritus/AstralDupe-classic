package xyz.prorickey.classicdupe.commands.admin;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.utils.PlayerUtils;
import me.antritus.astraldupe.commands.AstralCommand; // Updated import
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class PvAddCMD extends AstralCommand {

    public PvAddCMD(AstralDupe main) {
        super(main, "pvadd");
        setPermission("astraldupe.admin.pvadd");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Utils.cmdMsg("<red>Please include a player to add a player vault to"));
            return true;
        }
        OfflinePlayer tarj = Bukkit.getOfflinePlayer(args[0]);
        ClassicDupe.getPVDatabase().addVault(tarj.getUniqueId().toString());
        sender.sendMessage(Utils.cmdMsg("<green>Gave <yellow>" + tarj.getName() + "<green> one vault"));
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
