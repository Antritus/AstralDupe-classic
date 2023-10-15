package me.antritus.astraldupe.listeners;

import me.antritus.astral.fluffycombat.FluffyCombat;
import me.antritus.astral.fluffycombat.manager.CombatManager;
import me.antritus.astraldupe.AstralDupe;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.util.Vector;
import xyz.prorickey.classicdupe.Utils;

public class CombatListener implements Listener {
	@EventHandler
	public void onEntityToggleGlide(EntityToggleGlideEvent e) {
		if(!(e.getEntity() instanceof Player player)) return;
		FluffyCombat fluffyCombat = AstralDupe.fluffyCombat;
		CombatManager combatManager = fluffyCombat.getCombatManager();
		if (combatManager.hasTags(player)) {
			e.setCancelled(true);
			player.sendMessage(Utils.cmdMsg("<red>You cannot use an elytra while in combat"));
		}
	}

	// An interesting approach offered by one of the paper devs
	@EventHandler
	public void onPlayerRiptide(PlayerRiptideEvent e) {
		FluffyCombat fluffyCombat = AstralDupe.fluffyCombat;
		CombatManager combatManager = fluffyCombat.getCombatManager();
		Player player = e.getPlayer();
		if (combatManager.hasTags(player)) {
			e.getPlayer().sendMessage(Utils.cmdMsg("<red>You cannot riptide while in combat"));
			Vector vel = player.getVelocity();
			Bukkit.getScheduler().scheduleSyncDelayedTask(AstralDupe.getInstance(), () -> player.setVelocity(vel), 1L);
		}
	}
}
