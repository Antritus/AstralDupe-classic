package xyz.prorickey.classicdupe.commands.default1;

import me.antritus.astral.fluffycombat.FluffyCombat;
import me.antritus.astral.fluffycombat.manager.CombatManager;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class SpawnCMD extends AstralCommand {

    private static final String PERMISSION_OTHERS = "default.spawn.others";

    public SpawnCMD(AstralDupe astralDupe) {
        super(astralDupe, "spawn");
        setAliases(List.of(
                "nether", "end", "overworld"
        ));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        String name;
        if (alias.equals("spawn")) {
            name = "spawn"; // Default spawn name
        } else {
            name = alias;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
                return true;
            }

            // Check if the player is in combat
            FluffyCombat fluffyCombat = AstralDupe.fluffyCombat;
            CombatManager combatManager = fluffyCombat.getCombatManager();
            if (combatManager.hasTags(player)) {
                player.sendMessage(Utils.cmdMsg("<red>You cannot execute this command in combat"));
                return true;
            }

            // Check if the spawn point exists
            if (isNotValidSpawn(name)) {
                player.sendMessage(Utils.cmdMsg("<red>Invalid spawn point: " + name));
                return true;
            }

            player.teleport(ClassicDupe.getDatabase().getSpawn(name));
            player.sendMessage(Utils.cmdMsg("<green>Teleported you to " + alias));
        } else {
            if (!sender.hasPermission(PERMISSION_OTHERS)) {
                sender.sendMessage(Utils.cmdMsg("<red>You don't have permission to use this command on others"));
                return true;
            }

            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null || !targetPlayer.isOnline()) {
                sender.sendMessage(Utils.cmdMsg("<red>That player is not currently on the server"));
                return true;
            }

            // Check if the spawn point exists
            if (isNotValidSpawn(name)) {
                sender.sendMessage(Utils.cmdMsg("<red>Invalid spawn point: " + name));
                return true;
            }

            targetPlayer.teleport(ClassicDupe.getDatabase().getSpawn(name));
            sender.sendMessage(Utils.cmdMsg("<green>Sent <yellow>" + targetPlayer.getName() + "<green> to " + alias));
            targetPlayer.sendMessage(Utils.cmdMsg("<green>You were sent to spawn by <yellow>" + sender.getName()));
        }
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission(PERMISSION_OTHERS)) {
            return ClassicDupe.getOnlinePlayerUsernames();
        }
        return new ArrayList<>();
    }

    private boolean isNotValidSpawn(String spawnName) {
        return !spawnName.equalsIgnoreCase("spawn") && !spawnName.equalsIgnoreCase("nether") && !spawnName.equalsIgnoreCase("end");
    }
}