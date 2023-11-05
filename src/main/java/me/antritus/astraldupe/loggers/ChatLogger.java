package me.antritus.astraldupe.loggers;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.entity.AstralPlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class ChatLogger extends DiscordLogger implements Listener {
	private final DateFormat dateFormat = new SimpleDateFormat("dd:MM:yyy hh:mm:ss");
	private final MiniMessage miniMessage = MiniMessage.miniMessage();
	private final AstralDupe astralDupe;
	public ChatLogger(AstralDupe astral, String name) {
		super(name);
		this.astralDupe = astral;
	}


	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChat(AsyncChatEvent event){
		AstralPlayer player = astralDupe.astralPlayer(event.getPlayer());
		if (player.isStaffChatEnabled()){
			return;
		}
		String date = dateFormat.format(Date.from(Instant.now()));
		String serialized = miniMessage.serialize(event.message());
		log((AstralDupe.dev ? "**DEV SERVER** " : "")+"**CHAT** ["+ date +"] " + event.getPlayer().getName() + " (``"+event.getPlayer().getUniqueId()+"``)" + ": ``"+serialized+"``");
	}
}
