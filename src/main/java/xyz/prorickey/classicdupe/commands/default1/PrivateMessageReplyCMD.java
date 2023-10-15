package xyz.prorickey.classicdupe.commands.default1;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.Utils;

import java.util.Collections;
import java.util.List;

public class PrivateMessageReplyCMD extends AstralCommand {

    public PrivateMessageReplyCMD(AstralDupe astralDupe) {
        super(astralDupe, "pmr");
        setAliases(List.of("r", "wr", "reply"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot use this command from console"));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(Utils.cmdMsg("<red>You must include a message to send them"));
            return true;
        }
        StringBuilder msg = new StringBuilder();
        for (String arg : args) msg.append(arg).append(" ");
        if (PrivateMessageCMD.lastInConvo.containsKey(player)) {
            Player recipient = PrivateMessageCMD.lastInConvo.get(player);
            recipient.sendMessage(Utils.cmdMsg("<gray>[PM] <yellow>" + player.getName() + " <gray>-> <yellow>" + recipient.getName() + " <dark_gray>\u00BB <gray>" + msg.toString().trim()));
            player.sendMessage(Utils.cmdMsg("<gray>[PM] <yellow>" + player.getName() + " <gray>-> <yellow>" + recipient.getName() + " <dark_gray>\u00BB <gray>" + msg.toString().trim()));
            PrivateMessageCMD.lastInConvo.put(recipient, player);
            PrivateMessageCMD.lastInConvo.put(player, recipient);
            AstralDupe.messageLogger.log(sender, recipient, msg.toString().trim(), true);
        } else player.sendMessage(Utils.cmdMsg("<red>You have no one to reply to"));
        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
