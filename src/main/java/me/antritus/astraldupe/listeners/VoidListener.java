package me.antritus.astraldupe.listeners;

import me.antritus.astral.fluffycombat.manager.CombatManager;
import me.antritus.astraldupe.AstralDupe;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import xyz.prorickey.classicdupe.ClassicDupe;

public class VoidListener implements Listener {
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntityType()!= EntityType.PLAYER){
			return;
		}
		Player player = (Player) e.getEntity();
		if (e.getCause()== EntityDamageEvent.DamageCause.VOID) {
			CombatManager combatManager = AstralDupe.fluffyCombat.getCombatManager();
			if (combatManager.hasTags(player)) {
				ClassicDupe.rawBroadcast("<yellow>" + player.getName() + "<red> just jumped into the void while in combat");
				ClassicDupe.getDatabase().getPlayerDatabase().addDeath(player.getUniqueId().toString());
				World world = e.getEntity().getWorld();
				boolean gameRule;
				if (world.getGameRuleValue(GameRule.KEEP_INVENTORY) == null){
					gameRule = world.getGameRuleDefault(GameRule.KEEP_INVENTORY).booleanValue();
				} else {
					gameRule = world.getGameRuleValue(GameRule.KEEP_INVENTORY).booleanValue();
				}
				if (gameRule){
					player.getInventory().clear();
					player.getInventory().setHelmet(null);
					player.getInventory().setChestplate(null);
					player.getInventory().setLeggings(null);
					player.getInventory().setBoots(null);
					player.getInventory().setItemInOffHand(null);
					player.setLevel(0);
					player.setTotalExperience(0);
				}
			}
			player.teleportAsync(ClassicDupe.getDatabase().getSpawn("overworld"));
		}
	}
}
