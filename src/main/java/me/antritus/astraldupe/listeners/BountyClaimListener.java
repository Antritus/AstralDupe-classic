package me.antritus.astraldupe.listeners;

import me.antritus.astral.cosmiccapital.api.CosmicCapitalAPI;
import me.antritus.astral.cosmiccapital.api.managers.IAccountManager;
import me.antritus.astral.cosmiccapital.api.types.IAccount;
import me.antritus.astral.fluffycombat.FluffyCombat;
import me.antritus.astral.fluffycombat.manager.CombatManager;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.ForRemoval;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.json.simple.JSONObject;
import xyz.prorickey.classicdupe.ClassicDupe;

import java.util.LinkedHashMap;
import java.util.Map;

import static me.antritus.astral.cosmiccapital.api.types.IAccount.CustomAction.ADD;

@Deprecated(forRemoval = true)
@ForRemoval(reason = "This is a temporary file to handle bounties as cosmic capital is in development and requires testing (cosmic v1.1)")
public class BountyClaimListener implements Listener {

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player player = e.getEntity();
		FluffyCombat fluffyCombat = FluffyCombat.getPlugin(FluffyCombat.class);
		CombatManager combatManager = fluffyCombat.getCombatManager();
		if (!combatManager.hasTags(player)){
			return;
		} else {
			Player killer = e.getEntity().getKiller();
			if(e.getEntity().getKiller() != null && e.getEntity().getKiller() != player) {
				if (killer != null && killer.getUniqueId() != player.getUniqueId()) {
					ClassicDupe.getDatabase().getPlayerDatabase().addKill(killer.getUniqueId().toString());
					ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(killer.getUniqueId()).addKillStreak(1);
					try {
						// Checking if cosmic capital is found.
						// This is set, so we know if it is a beta season and cosmic capital
						// is still in development.
						Class.forName("me.antritus.astral.cosmiccapital.CosmicCapital");

						CosmicCapitalAPI cosmicCapital = AstralDupe.cosmicCapital;
						IAccountManager userDatabase = cosmicCapital.playerManager();
						IAccount playerAccount = userDatabase.get(killer.getUniqueId());
						if (playerAccount == null){
							return;
						}
						{
							Integer bounty = ClassicDupe.getDatabase().getBountyDatabase().getBounty(player.getUniqueId());
							if (bounty != null) {
								Map<String, Object> json = new LinkedHashMap<>();
								json.put("type", "BOUNTY_CLAIM");
								json.put("victim", player.getUniqueId());
								json.put("attacker", killer.getUniqueId());
								json.put("date", System.currentTimeMillis());
								JSONObject jsonObject = new JSONObject(json);
								playerAccount.custom(AstralDupe.economy, ADD, bounty, jsonObject);
							}
						}
					} catch (ClassNotFoundException ex) {
						throw new RuntimeException(ex);
					}
				}
			}
		}
	}
}
