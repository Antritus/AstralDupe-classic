package xyz.prorickey.classicdupe.commands.default1;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.utils.PlayerUtils;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Utils;

import java.util.*;

public class PrivateMessageCMD extends AstralCommand {

    public static final Map<Player, Player> lastInConvo = new HashMap<>();

    public PrivateMessageCMD(AstralDupe astralDupe) {
        super(astralDupe, "pm");
        setAliases(List.of("msg", "whisper", "w", "message"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(args.length == 0) {
            sender.sendMessage(Utils.cmdMsg("<red>You must include a recipient when privately messaging"));
            return true;
        }
        if(args.length < 2) {
            sender.sendMessage(Utils.cmdMsg("<red>You must include a message to send them"));
            return true;
        }
        Player recipient = Bukkit.getServer().getPlayer(args[0]);
        if(recipient == null || !recipient.isOnline()) {
            sender.sendMessage(Utils.cmdMsg("<yellow>" + args[0] + " <red>is not currently online"));
            return true;
        }
        String nameOfSender;
        if(!(sender instanceof Player player)) nameOfSender = "<red>Console<gray>";
        else nameOfSender = player.getName();
        StringBuilder msg = new StringBuilder();
        for(int i = 1; i < args.length; i++) { msg.append(args[i]).append(" "); }
        recipient.sendMessage(Utils.cmdMsg("<gray>[PM] <yellow>" + nameOfSender + " <gray>-> <yellow>" + recipient.getName() + " <dark_gray>\u00BB <gray>" + msg.toString().trim()));
        sender.sendMessage(Utils.cmdMsg("<gray>[PM] <yellow>" + nameOfSender + " <gray>-> <yellow>" + recipient.getName() + " <dark_gray>\u00BB <gray>" + msg.toString().trim()));
        if(sender instanceof Player player) {
            lastInConvo.put(recipient, player);
            lastInConvo.put(player, recipient);
        }
        AstralDupe.messageLogger.log(sender, recipient, msg.toString().trim(), false);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if(args.length == 1) return PlayerUtils.getVisiblePlayerNames(sender);
        return Collections.emptyList();
    }
}
