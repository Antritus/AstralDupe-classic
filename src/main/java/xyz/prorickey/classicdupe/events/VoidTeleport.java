package xyz.prorickey.classicdupe.events;

import me.antritus.astral.fluffycombat.FluffyCombat;
import me.antritus.astral.fluffycombat.manager.CombatManager;
import me.antritus.astraldupe.AstralDupe;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import xyz.prorickey.classicdupe.ClassicDupe;

public class VoidTeleport implements Listener {
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if(e.getPlayer().getLocation().getY() < -64) {
            FluffyCombat fluffyCombat = AstralDupe.fluffyCombat;
            CombatManager combatManager = fluffyCombat.getCombatManager();
            if (combatManager.hasTags(e.getPlayer())){
                ClassicDupe.rawBroadcast("<yellow>" + e.getPlayer().getName() + "<red> just jumped into the void while in combat");
                ClassicDupe.getDatabase().getPlayerDatabase().addDeath(e.getPlayer().getUniqueId().toString());
            }
            e.getPlayer().teleport(ClassicDupe.getDatabase().getSpawn(e.getPlayer().getWorld().getName()));
        }
    }

}
