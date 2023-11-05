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

public class PortalFixerListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTeleport(PlayerTeleportEvent event){
		if (true)
			return;
		Player player = event.getPlayer();
		World.Environment environment = event.getTo().getWorld().getEnvironment();
		Location location =  null;
		if (event.getCause()== PlayerTeleportEvent.TeleportCause.END_PORTAL){
			if (environment == World.Environment.THE_END){
				location = AstralDupe.getDatabase().getSpawn("end");
			} else{
				location = AstralDupe.getDatabase().getSpawn("overworld");
			}
		} else if (event.getCause()== PlayerTeleportEvent.TeleportCause.NETHER_PORTAL){
			if (environment == World.Environment.NETHER){
				location = AstralDupe.getDatabase().getSpawn("nether");
			} else {
				location = AstralDupe.getDatabase().getSpawn("overworld");
			}
		}
		if (location != null){
			event.setTo(location);
		}
	}

	@EventHandler
	public void onTeleport(EntityPortalReadyEvent event){
		if (event.getPortalType()== PortalType.ENDER){
			Location location = null;
			if (event.getTargetWorld()==null){
				location = AstralDupe.getDatabase().getSpawn("overworld");
			} else {
				if (event.getTargetWorld().getEnvironment()== World.Environment.NORMAL){
					location = AstralDupe.getDatabase().getSpawn("overworld");
				} else if (event.getTargetWorld().getEnvironment()==World.Environment.THE_END){
					location = AstralDupe.getDatabase().getSpawn("end");
				}
			}
			if (location == null){
				return;
			}
			event.setCancelled(true);
			event.setTargetWorld(null);
			event.getEntity().teleportAsync(location);
		}

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
