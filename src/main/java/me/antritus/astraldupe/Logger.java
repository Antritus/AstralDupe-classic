package me.antritus.astraldupe;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public interface Logger {
	String getChannelIDString();
	long getChannelID();
	JDA getJDA();

	default void log(String message){
		JDA jda = getJDA();
		if (jda==null){
			return;
		}
		TextChannel channel = jda.getTextChannelById(getChannelID());
		if (channel==null){
			return;
		}
		channel.sendMessage(message).queue();
	}

}
