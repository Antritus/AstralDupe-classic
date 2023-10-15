package xyz.prorickey.classicdupe.commands.default1;

import me.antritus.astral.fluffycombat.FluffyCombat;
import me.antritus.astral.fluffycombat.manager.CombatManager;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.Collections;
import java.util.List;

public class SetHomeCMD extends AstralCommand {

    public SetHomeCMD(AstralDupe astralDupe) {
        super(astralDupe, "sethome");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        FluffyCombat fluffyCombat = AstralDupe.fluffyCombat;
        CombatManager combatManager = fluffyCombat.getCombatManager();
        if (combatManager.hasTags(player)) {
            player.sendRichMessage("<red>You cannot set homes in combat.");
            return true;
        }
        if (args.length == 0) {
            if (ClassicDupe.getDatabase().getHomesDatabase().getHome(player, "default") == null &&
                    ClassicDupe.getDatabase().getHomesDatabase().getHomes(player.getUniqueId()).size() >= Utils.getMaxHomes(player)) {
                player.sendMessage(Utils.cmdMsg("<red>You have reached the maximum amount of homes"));
            } else {
                ClassicDupe.getDatabase().getHomesDatabase().addHome(player, "default", player.getLocation());
                player.sendMessage(Utils.cmdMsg("<green>Set your default home"));
            }
        } else {
            if (ClassicDupe.getDatabase().getHomesDatabase().getHome(player, args[0]) == null &&
                    ClassicDupe.getDatabase().getHomesDatabase().getHomes(player.getUniqueId()).size() >= Utils.getMaxHomes(player)) {
                player.sendMessage(Utils.cmdMsg("<red>You have reached the maximum amount of homes"));
            } else {
                ClassicDupe.getDatabase().getHomesDatabase().addHome(player, args[0], player.getLocation());
                player.sendMessage(Utils.cmdMsg("<green>Set your <yellow>" + args[0] + " <green>home"));
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
