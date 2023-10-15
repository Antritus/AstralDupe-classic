package xyz.prorickey.classicdupe.commands.moderator;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.ForRemoval;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

@ForRemoval(reason = "Staff commands should go to their own plugin.")
@Deprecated(forRemoval = true)
public class CspyCMD extends AstralCommand {

    public static final List<Player> cspyList = new ArrayList<>();

    public CspyCMD(AstralDupe astralDupe) {
        super(astralDupe, "commandspy");
        setAliases(List.of("cspy"));
        setPermission("astraldupe.staff.commandspy");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if (!cspyList.contains(player)) {
            cspyList.add(player);
            player.sendMessage(Utils.cmdMsg("<green>Turned on Command Spy"));
        } else {
            cspyList.remove(player);
            player.sendMessage(Utils.cmdMsg("<red>Turned off Command Spy"));
        }
        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
