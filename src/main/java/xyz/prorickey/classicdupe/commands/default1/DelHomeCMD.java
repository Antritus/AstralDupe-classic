package xyz.prorickey.classicdupe.commands.default1;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.utils.PlayerUtils;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class DelHomeCMD extends AstralCommand {

    public DelHomeCMD(AstralDupe astralDupe) {
        super(astralDupe, "delhome");
    }

    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if (args.length == 0) {
            if (ClassicDupe.getDatabase().getHomesDatabase().getHome(player, "default") == null) {
                player.sendMessage(Utils.cmdMsg("<red>You do not have a default home set"));
                return true;
            }
            ClassicDupe.getDatabase().getHomesDatabase().delHome(player, "default");
            player.sendMessage(Utils.cmdMsg("<green>Deleted your default home"));
        } else {
            if (ClassicDupe.getDatabase().getHomesDatabase().getHome(player, args[0]) == null) {
                player.sendMessage(Utils.cmdMsg("<red>You do not have a home named <yellow>" + args[0] + "<red> set"));
                return true;
            }
            ClassicDupe.getDatabase().getHomesDatabase().delHome(player, args[0]);
            player.sendMessage(Utils.cmdMsg("<green>Deleted your home named <yellow>" + args[0]));
        }
        return true;
    }

    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender instanceof Player player) {
            return new ArrayList<>(PlayerUtils.getVisiblePlayerNames(sender));
        }
        return new ArrayList<>();
    }
}
