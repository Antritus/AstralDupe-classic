package xyz.prorickey.classicdupe.commands.default1;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.events.JoinEvent;

import java.util.ArrayList;
import java.util.List;

public class NakedOffCMD extends AstralCommand {

    public NakedOffCMD(AstralDupe astralDupe) {
        super(astralDupe, "nakedoff");
        setAliases(List.of("pvp", "allowpvp", "pvpenable", "enablepvp", "offnaked"));
    }

    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if (!JoinEvent.nakedProtection.containsKey(player)) {
            player.sendMessage(Utils.cmdMsg("<red>You are not currently in naked protection"));
            return true;
        }
        JoinEvent.nakedProtection.remove(player);
        player.sendMessage(Utils.cmdMsg("<green>Turned off naked protection, pvp is now enabled"));
        return true;
    }

    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
