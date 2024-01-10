package me.antritus.astraldupe.listeners;

import me.antritus.astral.fluffycombat.manager.CombatManager;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.entity.AstralPlayer;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.ClassicDupe;

public class VoidListener implements Listener {
	@EventHandler
	public void onDamage(@NotNull EntityDamageEvent e) {
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
				// Check if keep inventory...
				// I had in the beta no keep inventory, and it made the server more interesting for players.
				if (world.getGameRuleValue(GameRule.KEEP_INVENTORY) == null){
					gameRule = world.getGameRuleDefault(GameRule.KEEP_INVENTORY).booleanValue();
				} else {
					gameRule = world.getGameRuleValue(GameRule.KEEP_INVENTORY).booleanValue();
				}
				 // Might give 20 minutes of keep inventory to all players on their first join.
				AstralPlayer astralPlayer = AstralDupe.getInstance().astralPlayer(player);
				if (astralPlayer.isHasKeepInventory()){
					gameRule = true;
				}
				if (!gameRule){
					 // Reset basically everything
					player.getInventory().clear();
					player.getInventory().setHelmet(null);
					player.getInventory().setChestplate(null);
					player.getInventory().setLeggings(null);
					player.getInventory().setBoots(null);
					player.getInventory().setItemInOffHand(null);
					// Reset xp and levels don't want to keep those
					player.setLevel(0);
					player.setTotalExperience(0);
				}
			}

			// Teleport to their rightful worlds instead of the main overworld
			switch (player.getWorld().getEnvironment()){
				case THE_END -> player.teleportAsync(ClassicDupe.getDatabase().getSpawn("end"));
				case NETHER -> player.teleportAsync(ClassicDupe.getDatabase().getSpawn("nether"));
				case NORMAL, CUSTOM -> player.teleportAsync(ClassicDupe.getDatabase().getSpawn("overworld"));
			}
		}
	}
}
