package xyz.prorickey.classicdupe.commands.default1;

import me.antritus.astral.fluffycombat.FluffyCombat;
import me.antritus.astral.fluffycombat.manager.CombatManager;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class HomeCMD extends AstralCommand {

    public HomeCMD(AstralDupe astralDupe) {
        super(astralDupe, "home");
    }

    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from the console"));
            return true;
        }
        FluffyCombat fluffyCombat = AstralDupe.fluffyCombat;
        CombatManager combatManager = fluffyCombat.getCombatManager();
        if (combatManager.hasTags(player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot teleport while in combat"));
            return true;
        }
        if (args.length == 0) {
            Location loc = ClassicDupe.getDatabase().getHomesDatabase().getHome(player, "default");
            if (loc == null) {
                sender.sendMessage(Utils.cmdMsg("<red>You do not have a default home set"));
                return true;
            }
            player.teleport(loc);
            player.sendMessage(Utils.cmdMsg("<green>Teleported to your default home"));
        } else {
            Location loc = ClassicDupe.getDatabase().getHomesDatabase().getHome(player, args[0]);
            if (loc == null) {
                sender.sendMessage(Utils.cmdMsg("<red>The home <yellow>" + args[0] + " <red>does not exist"));
                return true;
            }
            player.teleport(loc);
            player.sendMessage(Utils.cmdMsg("<green>Teleported to <yellow>" + args[0] + "<green> home"));
        }
        return true;
    }

    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender instanceof Player player) {
            return ClassicDupe.getDatabase().getHomesDatabase().getHomes(player.getUniqueId()).keySet().stream().toList();
        }
        return new ArrayList<>();
    }
}
