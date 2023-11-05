package xyz.prorickey.classicdupe.events;

import me.antritus.astral.fluffycombat.api.events.CombatLogEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.commands.default1.PrivateMessageCMD;
import xyz.prorickey.classicdupe.commands.moderator.CspyCMD;

import java.util.HashMap;

public class QuitEvent implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        PrivateMessageCMD.lastInConvo.remove(e.getPlayer());
        ClassicDupe.getDatabase().getHomesDatabase().unloadPlayer(e.getPlayer());
        ClassicDupe.getDatabase().getPlayerDatabase().playerDataUnload(e.getPlayer().getUniqueId());
        CspyCMD.cspyList.remove(e.getPlayer());
        if(PrivateMessageCMD.lastInConvo.containsValue(e.getPlayer())) new HashMap<>(PrivateMessageCMD.lastInConvo).forEach((sender, recipient) -> PrivateMessageCMD.lastInConvo.remove(sender));
        e.quitMessage(Utils.format("<dark_gray>[<red>-<dark_gray>] ")
                .append(Utils.format(Utils.getPrefix(e.getPlayer()) + e.getPlayer().getName())));
    }
    @EventHandler
    public void onCombatLog(CombatLogEvent event){
        ClassicDupe.getDatabase().getPlayerDatabase().addDeath(event.getWho().getUniqueId().toString());
    }
}
