package me.antritus.astraldupe.discord_loggers;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class MessageLogger extends DiscordLogger {

	public MessageLogger(String name) {
		super(name);
	}

	public void log(Player player, Player receiving, String message, boolean reply){
		log((reply ? "**REPLY** " : "**DIRECT** ") + "``"+player.getName()+" ("+player.getUniqueId()+")`` > ``" + receiving.getName()+" ("+receiving.getUniqueId()+")``: ``"+message+"``");
	}
	public void log(CommandSender sender, Player receiving, String message, boolean reply){
		if (sender instanceof Player){
			log((Player) sender, receiving, message, reply);
			return;
		}
		log((reply ? "**REPLY** " : "**DIRECT**") + "``"+(sender instanceof ConsoleCommandSender ? "**CONSOLE**" : "**"+sender.getName()+"**")+" `` > ``" + receiving.getName()+" ("+receiving.getUniqueId()+")``: ``"+message+"``");
	}
}
