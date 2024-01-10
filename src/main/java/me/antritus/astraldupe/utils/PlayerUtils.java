package me.antritus.astraldupe.utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class PlayerUtils {
	public static List<Player> getVisiblePlayers(CommandSender player) {
		List<Player> players = new ArrayList<>();
		Bukkit.getOnlinePlayers().forEach(p->{
			if (Utils.isVanished(player, p) && player.hasPermission("classicdupe.staff.vanish.see")){
				players.add(p);
			} else if (!Utils.isVanished(player, p)){
				players.add(p);
			}
		});
		return players;
	}
	public static List<String> getVisiblePlayerNames(CommandSender player) {
		List<String> names = new ArrayList<>();
		Bukkit.getOnlinePlayers().forEach(p->{
			if (Utils.isVanished(player, p) && player.hasPermission("classicdupe.staff.vanish.see")){
				names.add(p.getName());
			} else if (!Utils.isVanished(player, p)){
				names.add(p.getName());
			}
		});
		return names;
	}
}
