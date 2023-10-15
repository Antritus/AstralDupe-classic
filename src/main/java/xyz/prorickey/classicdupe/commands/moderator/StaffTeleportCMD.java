package xyz.prorickey.classicdupe.commands.moderator;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.ForRemoval;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

@ForRemoval(reason = "Staff commands should go to their own plugin.")
@Deprecated(forRemoval = true)
public class StaffTeleportCMD extends AstralCommand {

    public StaffTeleportCMD(AstralDupe astralDupe) {
        super(astralDupe, "staffteleport");
        setAliases(List.of("st"));
        setPermission("astraldupe.staff.teleport");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from the console"));
            return true;
        }
        if (args.length == 0) {
            p.sendMessage(Utils.cmdMsg("<red>Who would you like to teleport to?"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            p.sendMessage(Utils.cmdMsg("<red>That player is not currently online"));
            return true;
        }
        p.setGameMode(GameMode.SPECTATOR);
        p.teleport(target);
        p.setGameMode(GameMode.SPECTATOR);
        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 3.0F, 0.533F);
        p.sendMessage(Utils.cmdMsg("<green>You were successfully teleported! Use /back to get to your old location!"));
        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return ClassicDupe.getOnlinePlayerUsernames();
        return new ArrayList<>();
    }
}
