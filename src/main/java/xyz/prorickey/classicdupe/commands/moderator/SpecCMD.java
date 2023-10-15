package xyz.prorickey.classicdupe.commands.moderator;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.ForRemoval;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

@ForRemoval(reason = "Staff commands should go to their own plugin.")
@Deprecated(forRemoval = true)
public class SpecCMD extends AstralCommand {

    public SpecCMD(AstralDupe astralDupe) {
        super(astralDupe, "spectator");
        setAliases(List.of("spec"));
        setPermission("astraldupe.staff.spectator");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if (p.getGameMode() == GameMode.SPECTATOR) {
            p.setGameMode(GameMode.SURVIVAL);
            p.sendMessage(Utils.cmdMsg("<green>Set your gamemode to survival"));
        } else {
            p.setGameMode(GameMode.SPECTATOR);
            p.sendMessage(Utils.cmdMsg("<green>Set your gamemode to spectator"));
        }
        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
