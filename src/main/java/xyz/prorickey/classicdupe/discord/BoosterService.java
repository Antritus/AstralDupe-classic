package xyz.prorickey.classicdupe.discord;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BoosterService extends GroupsLink implements Listener {


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        Player player = e.getPlayer();

        if (isPlayerInGroup(player, "discord_booster")) {
            if (!LinkRewards.checkRewardsForBoosting(player)) {
                removeGroupFromPlayer(player.getUniqueId(), "discord_booster");
            }

        } else if (LinkRewards.checkRewardsForBoosting(player)) {
            //                                                            30 days if they remove the boost,
            //                                                            the reward is removed.
            grantGroup(player.getUniqueId(), "discord_booster");
        }
    }


}
