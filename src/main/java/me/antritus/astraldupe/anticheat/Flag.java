package me.antritus.astraldupe.anticheat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Flag {
	private final String flagName;
	private Map<UUID, Integer> flagged = new HashMap<>();
	private final int max;
	private final boolean kick;

	public Flag(String flagName, int max, boolean kick) {
		this.flagName = flagName;
		this.max = max;
		this.kick = kick;
	}

	public void flag(Player player) {
		flagged.putIfAbsent(player.getUniqueId(), 0);
		flagged.put(player.getUniqueId(), flagged.get(player.getUniqueId())+1);
		String format = "<dark_purple><b>Astral<reset> <green>"+player.getName()+"<red> has flagged<white> "+
				flagName +
				" <dark_gray>[<red>"+
				flagged.get(player.getUniqueId())+
				"<gray>/<red>"+max+"<dark_gray>]";
		Component message = MiniMessage.miniMessage().deserialize(format);
		Bukkit.broadcast(message, "astraldupe.anticheat");
		if (flagged.get(player.getUniqueId()) > max && kick){
			player.kick(
					MiniMessage.miniMessage().deserialize(
							"<dark_purple><b>Astral<reset>\n<gray>You have been kicked for violating our server policy!"
					),
					PlayerKickEvent.Cause.PLUGIN
			);
		}
		if (flagged.get(player.getUniqueId())>max){
			flagged.remove(player.getUniqueId());
		}
	}

	public String flagName() {
		return flagName;
	}

	public Map<UUID, Integer> flagged() {
		return flagged;
	}

	public int max() {
		return max;
	}

	public boolean kick() {
		return kick;
	}
}
