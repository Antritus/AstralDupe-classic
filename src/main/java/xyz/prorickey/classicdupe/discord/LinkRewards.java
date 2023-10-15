package xyz.prorickey.classicdupe.discord;

import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.database.LinkingDatabase;

import java.util.concurrent.atomic.AtomicBoolean;

public class LinkRewards extends GroupsLink{

    public static void checkRewardsForLinking(Player player) {
        LinkingDatabase.Link link = ClassicDupe.getDatabase().getLinkingDatabase().getLinkFromUUID(player.getUniqueId().toString());
        if (link==null){
            if (BoosterService.isPlayerInGroup(player, "discord_linked")){
                BoosterService.removeGroupFromPlayer(player.getUniqueId(), "discord_linked");
            }
        } else {
            if (!BoosterService.isPlayerInGroup(player, "discord_linked")){
                BoosterService.grantGroup(player.getUniqueId(), "discord_linked");
            }
        }
    }

    public static boolean checkRewardsForBoosting(Player player) {
        LinkingDatabase.Link link = ClassicDupe.getDatabase().getLinkingDatabase().getLinkFromUUID(player.getUniqueId().toString());
        if(link == null) return false;
        AtomicBoolean isBoosting = new AtomicBoolean(false);
        ClassicDupeBot.getJDA().getGuildById(Config.getConfig().getLong("discord.guild")).retrieveMemberById(link.id).queue(member -> {
            if(member.isBoosting()) {
                isBoosting.set(true);
                if(ClassicDupe.getLuckPerms().getUserManager().getUser(player.getUniqueId()).getPrimaryGroup().equals("default")) {
                    grantGroup(player.getUniqueId(), "discord_booster");
                    player.sendMessage(Utils.cmdMsg("<yellow>Thank you for boosting the discord. You have recieved the booster role."));
                }
            }
        });

        return isBoosting.get();
    }

}
