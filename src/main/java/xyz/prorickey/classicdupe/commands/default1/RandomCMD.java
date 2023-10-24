package xyz.prorickey.classicdupe.commands.default1;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.events.JoinEvent;

import java.util.Collections;
import java.util.List;

public class RandomCMD extends AstralCommand {

    public RandomCMD(AstralDupe astralDupe) {
        super(astralDupe, "toggle");
        setAliases(List.of("random"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof ConsoleCommandSender) return false;
        Player player = (Player) sender;
        Boolean to = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(player.getUniqueId()).swapRandomItem();
        if (to) {
            player.sendMessage(Utils.cmdMsg("<green>Turned on random items. You will now receive a random item every 60 seconds."));
            JoinEvent.randomItemList.add(player);
        } else {
            player.sendMessage(Utils.cmdMsg("<green>Turned off random items. You will no longer receive a random item every 60 seconds."));
            JoinEvent.randomItemList.remove(player);
        }
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
