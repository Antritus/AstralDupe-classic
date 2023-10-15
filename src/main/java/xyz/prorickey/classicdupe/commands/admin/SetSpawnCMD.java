package xyz.prorickey.classicdupe.commands.admin;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.utils.PlayerUtils;
import me.antritus.astraldupe.commands.AstralCommand; // Updated import
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class SetSpawnCMD extends AstralCommand {

    public SetSpawnCMD(AstralDupe main) {
        super(main, "placeholder");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(Utils.cmdMsg("<red>What spawn would you like to set? <gray>(hub, overworld, nether)"));
            return true;
        } else if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "overworld", "spawn" -> {
                    ClassicDupe.getDatabase().setSpawn("spawn", player.getLocation());
                    ClassicDupe.getDatabase().setSpawn("overworld", player.getLocation());
                    player.sendMessage(Utils.cmdMsg("<green>Set the overworld spawn location to your location"));
                    return true;
                }
                case "nether" -> {
                    ClassicDupe.getDatabase().setSpawn("nether", player.getLocation());
                    player.sendMessage(Utils.cmdMsg("<green>Set the nether spawn location to your location"));
                    return true;
                }
                case "end" -> {
                    ClassicDupe.getDatabase().setSpawn("end", player.getLocation());
                    player.sendMessage(Utils.cmdMsg("<green>Set the end spawn location to your location"));
                    return true;
                }
                case "afk" -> {
                    ClassicDupe.getDatabase().setSpawn("afk", player.getLocation());
                    player.sendMessage(Utils.cmdMsg("<green>Set the afk spawn location to your location"));
                    return true;
                }
                default -> {
                    player.sendMessage(Utils.cmdMsg("<red>What spawn would you like to set? <gray>(hub, overworld, nether)"));
                    return true;
                }
            }
        } else {
            Player tarj = Bukkit.getServer().getPlayer(args[1]);
            if (tarj == null || !tarj.isOnline()) {
                sender.sendMessage(Utils.cmdMsg("<yellow>" + args[1] + " <red>is not currently online"));
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "overworld", "spawn" -> {
                    ClassicDupe.getDatabase().setSpawn("spawn", tarj.getLocation());
                    ClassicDupe.getDatabase().setSpawn("overworld", tarj.getLocation());
                    sender.sendMessage(Utils.cmdMsg("<green>Set the overworld spawn location to <yellow>" + tarj.getName() + "'s <green>location"));
                    return true;
                }
                case "nether" -> {
                    ClassicDupe.getDatabase().setSpawn("nether", tarj.getLocation());
                    sender.sendMessage(Utils.cmdMsg("<green>Set the nether spawn location to <yellow>" + tarj.getName() + "'s <green>location"));
                    return true;
                }
                case "end" -> {
                    ClassicDupe.getDatabase().setSpawn("end", tarj.getLocation());
                    sender.sendMessage(Utils.cmdMsg("<green>Set the end spawn location to <yellow>" + tarj.getName() + "'s <green>location"));
                    return true;
                }
                case "afk" -> {
                    ClassicDupe.getDatabase().setSpawn("afk", tarj.getLocation());
                    sender.sendMessage(Utils.cmdMsg("<green>Set the afk spawn location to <yellow>" + tarj.getName() + "'s <green>location"));
                    return true;
                }
                default -> {
                    sender.sendMessage(Utils.cmdMsg("<red>What spawn would you like to set? <gray>(hub, overworld, nether)"));
                    return true;
                }
            }
        }
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("overworld", "nether", "end");
        } else if (args.length == 2) {
            return PlayerUtils.getVisiblePlayerNames(sender);
        }
        return new ArrayList<>();
    }
}
