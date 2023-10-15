package xyz.prorickey.classicdupe.events;

import me.antritus.astral.fluffycombat.FluffyCombat;
import me.antritus.astral.fluffycombat.manager.CombatManager;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.ForRemoval;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.prorickey.classicdupe.Utils;

import java.util.HashMap;
import java.util.Map;

@ForRemoval(reason = "This should just be implemented in fluffy combat.")
@Deprecated(forRemoval = false)
public class PearlCooldown implements Listener {

    private static final Map<Player, Long> pearlCooldown = new HashMap<>();
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        FluffyCombat fluffyCombat = AstralDupe.fluffyCombat;
        CombatManager combatManager = fluffyCombat.getCombatManager();
        if (combatManager.hasTags(e.getPlayer())) {
            if (e.getMaterial().equals(Material.ENDER_PEARL)) {
                if (pearlCooldown.containsKey(e.getPlayer()) && (pearlCooldown.get(e.getPlayer()) + 30) > System.currentTimeMillis()) {
                    e.setCancelled(true);
                    long wait = (pearlCooldown.get(e.getPlayer()) + (45 * 1000)) - System.currentTimeMillis();
                    e.getPlayer().sendMessage(Utils.cmdMsg("<red>You cannot throw a pearl for another " + Math.round(wait / 1000.0) + " second(s)"));
                } else pearlCooldown.put(e.getPlayer(), System.currentTimeMillis());
            }
        }
    }
}
