package me.antritus.astraldupe.listeners;

import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.api.PlayerAccount;
import me.antritus.astral.cosmiccapital.database.PlayerAccountDatabase;
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
import xyz.prorickey.classicdupe.Config;

import java.util.LinkedHashMap;
import java.util.Map;

@Deprecated(forRemoval = true)
@ForRemoval(reason = "This is a temporary file to handle economy as cosmic capital is in development and requires testing (cosmic v1.1)")
public class GiveKillMoneyListener implements Listener {
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

						double moneyPerKill = Config.getConfig().getDouble("economy.moneyMaking.kill");
						CosmicCapital cosmicCapital = CosmicCapital.getPlugin(CosmicCapital.class);
						PlayerAccountDatabase userDatabase = cosmicCapital.getPlayerDatabase();
						PlayerAccount playerAccount = userDatabase.get(killer);
						{
							Map<String, Object> json = new LinkedHashMap<>();
							json.put("type", "PLAYER_KILL");
							json.put("victim", player.getUniqueId());
							json.put("attacker", killer.getUniqueId());
							json.put("date", System.currentTimeMillis());
							JSONObject jsonObject = new JSONObject(json);
							playerAccount.plugin(AstralDupe.economy, moneyPerKill, jsonObject.toJSONString());
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
								playerAccount.plugin(AstralDupe.economy, bounty, jsonObject.toString());
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
