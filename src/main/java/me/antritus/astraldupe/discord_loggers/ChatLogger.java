package me.antritus.astraldupe.discord_loggers;

import io.papermc.paper.event.player.AsyncChatEvent;
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
	public ChatLogger(String name) {
		super(name);
	}


	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChat(AsyncChatEvent event){
		String date = dateFormat.format(Date.from(Instant.now()));
		String serialized = miniMessage.serialize(event.message());
		String untagged = miniMessage.stripTags(serialized);
		log("**CHAT LOG MM** ["+ date +"]" + event.getPlayer().getName() + ": ``"+serialized+"``"
				+"\n**CHAT LOG** ["+ date +"]" + event.getPlayer().getName() + ": ``"+untagged+"``");
	}
}
