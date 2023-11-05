package me.antritus.astraldupe.listeners;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.entity.AstralPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.bukkit.event.EventPriority.HIGH;

public class ToggleListener implements Listener {
	private static Random random = new Random(System.currentTimeMillis());
	private final Map<UUID, ScheduledTask> runnable = new HashMap<>();
	private final AstralDupe astralDupe;

	public ToggleListener(AstralDupe astralDupe) {
		this.astralDupe = astralDupe;
	}

	@EventHandler(priority = HIGH)
	public void onPlayerJoin(PlayerJoinEvent event){
		AstralPlayer astralPlayer = astralDupe.astralPlayer(event.getPlayer());
		Player player = event.getPlayer();
		ScheduledTask task = astralDupe.getServer().getAsyncScheduler().runAtFixedRate(
				astralDupe,
				(taskConsumer)->{
					if (astralPlayer.isToggleEnabled()){
						int randomInt = random.nextInt(AstralDupe.randomItems.size());
						ItemStack itemStack = AstralDupe.randomItems.get(randomInt);
						Map<Integer, ItemStack> items = player.getInventory().addItem(itemStack);
						if (items.size()>0){
							World world = player.getWorld();
							Location location = player.getLocation();
							items.values().forEach(item->{
								world.dropItemNaturally(location, itemStack);
							});
						}
					}
				},
				0,
				15,
				TimeUnit.SECONDS
		);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		ScheduledTask task = runnable.get(event.getPlayer().getUniqueId());
		task.cancel();
		runnable.remove(event.getPlayer().getUniqueId());
	}
}
