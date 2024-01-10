package me.antritus.astraldupe.listeners;

import io.papermc.paper.event.entity.EntityPortalReadyEvent;
import me.antritus.astraldupe.AstralDupe;
import org.bukkit.Location;
import org.bukkit.PortalType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;

public class PortalFixerListener implements Listener {
	private final AstralDupe astralDupe;

	public PortalFixerListener(AstralDupe astralDupe) {
		this.astralDupe = astralDupe;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTeleport(@NotNull PlayerTeleportEvent event){
		Player player = event.getPlayer();
		World.Environment environment = event.getTo().getWorld().getEnvironment();
		Location location;
		if (event.getCause()== PlayerTeleportEvent.TeleportCause.END_PORTAL){
			if (environment == World.Environment.THE_END){
				location = AstralDupe.getDatabase().getSpawn("end");
			} else{
				location = AstralDupe.getDatabase().getSpawn("overworld");
			}
		} else {
			location = null;
		}
		if (location != null) {
			event.setCancelled(true);
			event.setTo(location);
			astralDupe.getServer().getScheduler().runTaskLater(astralDupe, ()->player.teleportAsync(location), 2);
		}
	}
	@EventHandler
	public void onTeleport(@NotNull EntityPortalReadyEvent event){
		if (event.getPortalType()== PortalType.NETHER){
			Location location = null;
			if (event.getTargetWorld()==null){
				location = AstralDupe.getDatabase().getSpawn("overworld");
			} else {
				if (event.getTargetWorld().getEnvironment()== World.Environment.NORMAL){
					location = AstralDupe.getDatabase().getSpawn("overworld");
				} else if (event.getTargetWorld().getEnvironment()==World.Environment.NETHER){
					location = AstralDupe.getDatabase().getSpawn("nether");
				}
			}
 			event.setCancelled(true);
			event.setTargetWorld(null);
			if (location != null) {
				event.getEntity().teleportAsync(location);
			}
		}
	}
}
