package xyz.prorickey.classicdupe.commands.moderator;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.ForRemoval;
import me.antritus.astraldupe.commands.AstralCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_20_R1.command.CraftRemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.discord.ClassicDupeBot;

import java.util.ArrayList;
import java.util.List;

@ForRemoval(reason = "Staff commands should go to their own plugin.")
@Deprecated(forRemoval = true)
public class StaffChatCMD extends AstralCommand {

    public StaffChatCMD(AstralDupe astralDupe) {
        super(astralDupe, "staffchat");
        setPermission("astraldupe.staff.chat");
        setAliases(List.of("sc"));
    }

    public static final List<Player> staffChatPlayers = new ArrayList<>();

    public static void sendToStaffChat(String text) {
        ClassicDupe.getInstance().getServer().getOnlinePlayers().forEach(p -> {
            if (p.hasPermission("classicdupe.staff.chat")) p.sendMessage(Utils.format(text));
        });
    }

    public static void sendToStaffChat(Component comp) {
        ClassicDupe.getInstance().getServer().getOnlinePlayers().forEach(p -> {
            if (p.hasPermission("classicdupe.staff.chat")) p.sendMessage(comp);
        });
    }

    public static void logChat(CommandSender sender, String message){
        String name = sender.getName();
        if (sender instanceof CommandBlock){
            name = "COMMAND_BLOCK("+name+")";
        } else if (sender instanceof CraftRemoteConsoleCommandSender) {
            name = "REMOTE CONSOLE";
        } else if (sender instanceof ConsoleCommandSender){
            name = "CONSOLE";
        }
        ClassicDupeBot.getJDA().getTextChannelById(Config.getConfig().getLong("discord.staffchat")).sendMessage(
                name+":" + message
        );
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console."));
            return true;
        }
        if (args.length == 0) {
            if (staffChatPlayers.contains(p)) {
                staffChatPlayers.remove(p);
                p.sendMessage(Utils.cmdMsg("<red>Turned off StaffChat"));
            } else {
                staffChatPlayers.add(p);
                p.sendMessage(Utils.cmdMsg("<green>Turned on StaffChat"));
            }
        } else {
            if (args[0].equalsIgnoreCase("on")) {
                if (staffChatPlayers.contains(p)) {
                    p.sendMessage(Utils.cmdMsg("<red>Your staffchat is already on"));
                    return true;
                }
                staffChatPlayers.add(p);
                p.sendMessage(Utils.cmdMsg("<green>Turned your staffchat on"));
            } else if (args[0].equalsIgnoreCase("off")) {
                if (!staffChatPlayers.contains(p)) {
                    p.sendMessage(Utils.cmdMsg("<red>Your staffchat is already off"));
                    return true;
                }
                staffChatPlayers.remove(p);
                p.sendMessage(Utils.cmdMsg("<green>Turned your staffchat off"));
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return List.of("on", "off");
        return new ArrayList<>();
    }
}
