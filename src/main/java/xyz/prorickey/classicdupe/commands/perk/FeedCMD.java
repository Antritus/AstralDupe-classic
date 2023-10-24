package xyz.prorickey.classicdupe.commands.perk;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.SoundCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Utils;

import java.util.*;


public class FeedCMD extends AstralCommand {

    private static final Map<UUID, Long> feedCooldown = new HashMap<>();

    public FeedCMD(AstralDupe main) {
        super(main, "feed");
        setPermission("astraldupe.perk.feed");
        setAliases(List.of("feedme", "eat"));
    }

    // Basic: 1 minute cooldown
    // Vip: 1 minute cooldown
    // MVP: 30 second cooldown
    // Legend 0 minute cooldown

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if (player.hasPermission("perks.feed.legend")) {
            player.setFoodLevel(20);
            player.sendMessage(Utils.cmdMsg("<green>You have been fed"));
            player.playSound(player, "entity.generic.eat", SoundCategory.MASTER, 1F, 1F);
        } else if (player.hasPermission("perks.feed.mvp")) {
            if (feedCooldown.containsKey(player.getUniqueId()) && feedCooldown.get(player.getUniqueId()) + (1000 * 30) > System.currentTimeMillis()) {
                long wait = feedCooldown.get(player.getUniqueId()) + (1000 * 30) - System.currentTimeMillis();
                player.sendMessage(Utils.cmdMsg("<red>You cannot execute this command for " + (wait / 1000) + " seconds(s)"));
                return true;
            }
            feedCooldown.put(player.getUniqueId(), System.currentTimeMillis());
            player.setFoodLevel(20);
            player.sendMessage(Utils.cmdMsg("<green>You have been fed"));
            player.playSound(player, "entity.generic.eat", SoundCategory.MASTER, 1F, 1F);
        } else {
            if (feedCooldown.containsKey(player.getUniqueId()) && feedCooldown.get(player.getUniqueId()) + (1000 * 60) > System.currentTimeMillis()) {
                long wait = feedCooldown.get(player.getUniqueId()) + (1000 * 60) - System.currentTimeMillis();
                player.sendMessage(Utils.cmdMsg("<red>You cannot execute this command for " + (wait / 1000) + " seconds(s)"));
                return true;
            }
            feedCooldown.put(player.getUniqueId(), System.currentTimeMillis());
            player.setFoodLevel(20);
            player.sendMessage(Utils.cmdMsg("<green>You have been fed"));
            player.playSound(player, "entity.generic.eat", SoundCategory.MASTER, 1F, 1F);
        }
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
