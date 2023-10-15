package xyz.prorickey.classicdupe.commands.default1;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.database.PlayerData;

import java.util.ArrayList;
import java.util.List;

public class DeathMessagesCMD extends AstralCommand {

    public DeathMessagesCMD(AstralDupe astralDupe) {
        super(astralDupe, "death-messages");
    }

    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        PlayerData playerData = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(player.getUniqueId());
        if (playerData.getDeathMessages()) {
            playerData.setDeathMessages(false);
            player.sendMessage(Utils.cmdMsg("<green>Turned off death messages"));
        } else {
            playerData.setDeathMessages(true);
            player.sendMessage(Utils.cmdMsg("<green>Turned on death messages"));
        }
        return true;
    }

    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
