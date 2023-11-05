package me.antritus.astraldupe.loggers;

import me.antritus.astraldupe.AstralDupe;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class MessageLogger extends DiscordLogger {
	private final DateFormat dateFormat = new SimpleDateFormat("dd:MM:yyy hh:mm:ss");

	public MessageLogger(String name) {
		super(name);
	}

	public void log(Player player, Player receiving, String message, String date, boolean reply){
		log((AstralDupe.dev ? "**DEV SERVER** " : "")+"**DM** " + (reply ? "**REPLY** " : "**DIRECT** ")+ " ["+date+"] " + "``"+player.getName()+" ("+player.getUniqueId()+")`` > ``" + receiving.getName()+" ("+receiving.getUniqueId()+")``: ``"+message+"``");
	}
	public void log(CommandSender sender, Player receiving, String message, boolean reply){
		String date = dateFormat.format(Date.from(Instant.now()));
		if (sender instanceof Player player){
			log(player, receiving, message, date, reply);
			return;
		}
		log((AstralDupe.dev ? "**DEV SERVER** " : "")+"**DM** " + (reply ? "**REPLY** " : "**DIRECT**") + " ["+date+"] "+"``"+(sender instanceof ConsoleCommandSender ? "**CONSOLE**" : "**"+sender.getName()+"**")+" `` > ``" + receiving.getName()+" ("+receiving.getUniqueId()+")``: ``"+message+"``");
	}
}
