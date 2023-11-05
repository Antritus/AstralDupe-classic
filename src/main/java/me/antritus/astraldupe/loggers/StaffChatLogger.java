package me.antritus.astraldupe.loggers;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.entity.AstralPlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class StaffChatLogger extends DiscordLogger implements Listener {
	private final DateFormat dateFormat = new SimpleDateFormat("dd:MM:yyy hh:mm:ss");
	private final MiniMessage miniMessage = MiniMessage.miniMessage();
	private final PlainTextComponentSerializer plainTextSerializer = PlainTextComponentSerializer.plainText();
	private final AstralDupe astralDupe;
	public StaffChatLogger(AstralDupe astral, String name) {
		super(name);
		this.astralDupe = astral;
	}


	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChat(AsyncChatEvent event){
		AstralPlayer player = astralDupe.astralPlayer(event.getPlayer());
		if (!player.isStaffChatEnabled()){
			return;
		}
		event.setCancelled(true);
		String date = dateFormat.format(Date.from(Instant.now()));
		String plainText = plainTextSerializer.serialize(event.message());
		log((AstralDupe.dev ? "**DEV SERVER** " : "")+"**STAFF CHAT** ["+ date +"] " + event.getPlayer().getName() + " (``"+event.getPlayer().getUniqueId()+"``)" + ": ``"+plainText+"``");
	}

	public void log(CommandSender commandSender, String message){
	}
	public void log(Player player, String message){
		String date = dateFormat.format(Date.from(Instant.now()));
		log((AstralDupe.dev ? "**DEV SERVER** " : "")+"**STAFF CHAT** ["+ date +"]" + player.getName() + " (``"+player.getUniqueId()+"``)" + ": ``"+message+"``");
	}
}