package me.antritus.astraldupe.listeners;

import me.antritus.astraldupe.AstralDupe;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.prorickey.classicdupe.ClassicDupe;

public class JoinListener implements Listener {
	private AstralDupe astralDupe;
	public JoinListener(AstralDupe astralDupe) {
		this.astralDupe = astralDupe;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		if (event.getPlayer().hasPlayedBefore()){
			return;
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				Location location = ClassicDupe.getDatabase().getSpawn("overworld");
				event.getPlayer().teleportAsync(location);
			}
		}.runTaskLater(astralDupe, 4);
	}
}