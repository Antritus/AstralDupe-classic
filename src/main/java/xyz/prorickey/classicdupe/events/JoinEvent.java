package xyz.prorickey.classicdupe.events;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.database.PlayerData;
import xyz.prorickey.classicdupe.discord.LinkRewards;

import java.util.*;

public class JoinEvent implements Listener {

    public static final Map<Player, RandomItemTask> randomTaskMap = new HashMap<>();
    public static final List<Player> randomItemList = new ArrayList<>();
    public static final Map<Player, Long> nakedProtection = new HashMap<>();
    public static final List<Player> nightVision = new ArrayList<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(e.getPlayer().getUniqueId()) == null) {
            ClassicDupe.getDatabase().getPlayerDatabase().playerDataUpdateAndLoad(e.getPlayer());
            if(ClassicDupe.getDatabase().getSpawn("hub") != null) e.getPlayer().teleport(ClassicDupe.getDatabase().getSpawn("hub"));
            e.joinMessage(Utils.format("<yellow>" + e.getPlayer().getName() + " <green>Just joined for the first time! Give them a warm welcome"));
            e.getPlayer().sendMessage(Utils.cmdMsg("<green>Every <yellow>60 <green>you will recieve a random item. Execute /random to disable or enable this"));
            nakedProtection.put(e.getPlayer(), System.currentTimeMillis());

            // Starting Gear
            e.getPlayer().getInventory().addItem(
                    new ItemStack(Material.IRON_SWORD),
                    new ItemStack(Material.IRON_PICKAXE),
                    new ItemStack(Material.IRON_AXE),
                    new ItemStack(Material.COOKED_BEEF, 16)
            );

            RandomItemTask task = new RandomItemTask(e.getPlayer());
            randomItemList.add(e.getPlayer());
            randomTaskMap.put(e.getPlayer(), task);
            task.runTaskTimer(ClassicDupe.getInstance(), 0, 20*60);

            e.getPlayer().sendMessage(Utils.cmdMsg("<green>You currently have naked protection on. This means you cannot pvp but you are safe for 10 minutes. To turn this off execute /nakedoff"));
            return;
        }
        ClassicDupe.getDatabase().getPlayerDatabase().playerDataUpdateAndLoad(e.getPlayer());
        ClassicDupe.getDatabase().getHomesDatabase().loadPlayer(e.getPlayer());
        PlayerData playerData = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(e.getPlayer().getUniqueId());
        if(playerData.nickname != null && Utils.convertColorCodesToAdventure(playerData.nickname).length() != playerData.nickname.length())
            playerData.setNickname(Utils.convertColorCodesToAdventure(playerData.nickname));
        if(playerData.night) nightVision.add(e.getPlayer());
        RandomItemTask task = new RandomItemTask(e.getPlayer());
        randomTaskMap.put(e.getPlayer(), task);
        task.runTaskTimer(ClassicDupe.getInstance(), 0, 20*60);
        if(playerData.randomitem) {
            randomItemList.add(e.getPlayer());
            e.getPlayer().sendMessage(Utils.cmdMsg("<green>Every <yellow>60 <green>you will recieve a random item. Execute /random to disable or enable this"));
        }
        LinkRewards.checkRewardsForLinking(e.getPlayer());
        LinkRewards.checkRewardsForBoosting(e.getPlayer());
        e.joinMessage(Utils.format("<dark_gray>[<green>+<dark_gray>] ")
                .append(MiniMessage.miniMessage().deserialize(Utils.getPrefix(e.getPlayer()) + e.getPlayer().getName())));
    }

    public static final Map<Player, Long> afkTime = new HashMap<>();
    public static final Integer AFK_TIME_NEEDED = 5*60*1000;

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
                Player player = nakedProtection.keySet().stream().toList().get(i);
                Long time = nakedProtection.get(player);
                if(time + (10*60*1000) < System.currentTimeMillis()) {
                    nakedProtection.remove(player);
                    if(player.isOnline()) player.sendMessage(Utils.cmdMsg("<red>You are no longer protected by naked protection"));
                }
            }
        }
    }

    public static class RandomItemTask extends BukkitRunnable {

        private final Player player;

        public RandomItemTask(Player pl) { player = pl; }

        @Override
        public void run() {
            if(!randomItemList.contains(player)) return;
            ItemStack random = ClassicDupe.randomItems.get(new Random().nextInt(ClassicDupe.randomItems.size()));
            player.getInventory().addItem(random);
        }
    }

}
