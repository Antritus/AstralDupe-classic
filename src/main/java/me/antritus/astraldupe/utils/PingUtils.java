package me.antritus.astraldupe.utils;

import org.bukkit.entity.Player;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public final class PingUtils {
	private PingUtils(){}

	public static int getPing(Player player){
		return player.getPing();
	}

	public static String getPingFormat(Player player){
		int ping = getPing(player);
		if (ping < 60){
			return "<green>"+ping;
		}
		else if (ping < 150){
			return "<yellow>"+ping;
		}
		else if (ping < 300){
			return "<red>"+ping;
		}
		else {
			return "<dark_red>"+ping;
		}
	}

	public static String getPingMs(Player player){
		return player.getPing()+"ms";
	}

	public static String getPingFormatMs(Player player){
		int ping = getPing(player);
		if (ping == 0){
			return "<dark_green>Loading...";
		}
		if (ping < 60){
			return "<green>"+ping+"ms";
		}
		else if (ping < 150){
			return "<yellow>"+ping+"ms";
		}
		else if (ping < 300){
			return "<red>"+ping+"ms";
		}
		else {
			return "<dark_red>"+ping+"ms";
		}
	}
}
