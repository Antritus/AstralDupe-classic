package me.antritus.astraldupe.discord_loggers;

import me.antritus.astraldupe.Logger;
import net.dv8tion.jda.api.JDA;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.discord.ClassicDupeBot;

public class DiscordLogger implements Logger {
	private final String name;
	public DiscordLogger(String name){
		this.name = name;
	}
	@Override
	public String getChannelIDString() {
		return String.valueOf(Config.getConfig().getLong("discord.loggers." + name));
	}

	@Override
	public long getChannelID() {
		return Config.getConfig().getLong("discord.loggers."+name);
	}

	@Override
	public JDA getJDA() {
		return ClassicDupeBot.getJDA();
	}
}
