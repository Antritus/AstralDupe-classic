package xyz.prorickey.classicdupe.events;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.entity.AstralPlayer;
import me.antritus.astraldupe.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.database.PlayerData;
import xyz.prorickey.classicdupe.discord.LinkRewards;

import java.util.*;

import static me.antritus.astraldupe.AstralDupe.astralDupe;

public class JoinEvent implements Listener {

    public static final Map<UUID, Long> nakedProtection = new HashMap<>();
    public static final List<Player> nightVision = new ArrayList<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        // Astral Begin
        AstralPlayer astralPlayer = AstralDupe.getInstance().astralPlayer(e.getPlayer());
        Player player = e.getPlayer();
        if(ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(e.getPlayer().getUniqueId()) == null) {
            Component title = ColorUtils.translateComp(astralDupe);
            Component subTitle = ColorUtils.translateComp("<white>Welcome to "+astralDupe+ "<white>!");
            player.sendTitlePart(TitlePart.TITLE, title);
            player.sendTitlePart(TitlePart.SUBTITLE, subTitle);
            // Astral End


            ClassicDupe.getDatabase().getPlayerDatabase().playerDataUpdateAndLoad(player);
            e.joinMessage(Utils.format("<yellow>" + e.getPlayer().getName() + " <green>Just joined for the first time! Give them a warm welcome"));
            player.sendMessage(Utils.cmdMsg("<green>Every <yellow>15 <green>you will receive a random item. Execute /random to disable or enable this"));
            nakedProtection.put(player.getUniqueId(), System.currentTimeMillis());

            // Astral Begin
            astralPlayer.setToggleEnabled(true);
            // Astral end

            player.sendMessage(Utils.cmdMsg("<green>You currently have naked protection on. This means you cannot pvp but you are safe for 10 minutes. To turn this off execute /nakedoff"));
            return;
        }
        // Astral Begin
        Component title = ColorUtils.translateComp(astralDupe);
        Component subTitle = ColorUtils.translateComp("<white>Welcome back to "+astralDupe+ "<white>!");
        player.sendTitlePart(TitlePart.TITLE, title);
        player.sendTitlePart(TitlePart.SUBTITLE, subTitle);
        // Astral end

        ClassicDupe.getDatabase().getPlayerDatabase().playerDataUpdateAndLoad(e.getPlayer());
        ClassicDupe.getDatabase().getHomesDatabase().loadPlayer(e.getPlayer());
        PlayerData playerData = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(e.getPlayer().getUniqueId());
        if(playerData.nickname != null && Utils.convertColorCodesToAdventure(playerData.nickname).length() != playerData.nickname.length())
            playerData.setNickname(Utils.convertColorCodesToAdventure(playerData.nickname));
        if(playerData.night) nightVision.add(e.getPlayer());

        // Astral begin
        astralPlayer.setToggleEnabled(false);
        if(playerData.randomitem) {
            astralPlayer.setToggleEnabled(true);
            e.getPlayer().sendMessage(Utils.cmdMsg("<green>Every <yellow>15 <green>you will receive a random item. Execute /random to disable or enable this"));
        }
        // Astral end
        LinkRewards.checkRewardsForLinking(e.getPlayer());
        LinkRewards.checkRewardsForBoosting(e.getPlayer());
        e.joinMessage(Utils.format("<dark_gray>[<green>+<dark_gray>] ")
                .append(MiniMessage.miniMessage().deserialize(Utils.getPrefix(e.getPlayer()) + e.getPlayer().getName())));
    }

    public static class JoinEventTasks extends BukkitRunnable {
        @Override
        public void run() {
            Bukkit.getOnlinePlayers()
                    .stream().filter(nightVision::contains)
                    .forEach(p -> p.addPotionEffect(new PotionEffect(
                    PotionEffectType.NIGHT_VISION,
                    999999999,
                    1
            )));
            for(int i = 0; i < nakedProtection.size(); i++) {
                Player player = Bukkit.getPlayer(nakedProtection.keySet().stream().toList().get(i));
                if (player == null){
                    cancel();
                    return;
                }
                Long time = nakedProtection.get(player.getUniqueId());
                if(time + (10*60*1000) < System.currentTimeMillis()) {
                    nakedProtection.remove(player.getUniqueId());
                    if(player.isOnline()) player.sendMessage(Utils.cmdMsg("<red>You are no longer protected by naked protection"));
                }
            }
        }
    }
}
