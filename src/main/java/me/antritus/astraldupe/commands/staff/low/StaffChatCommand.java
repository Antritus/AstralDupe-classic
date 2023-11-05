package me.antritus.astraldupe.commands.staff.low;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.MessageManager;
import me.antritus.astraldupe.commands.AstralCommand;
import me.antritus.astraldupe.entity.AstralPlayer;
import me.antritus.astraldupe.loggers.StaffChatLogger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.discord.ClassicDupeBot;

import java.util.List;

import static me.antritus.astraldupe.MessageManager.placeholder;

public class StaffChatCommand extends AstralCommand implements Listener {
	private final StaffChatLogger logger;

	public StaffChatCommand(AstralDupe main) {
		super(main, "staffchat");
		setAliases(List.of("sc"));
		setPermission("astraldupe.staff.staffchat");
		this.logger = main.staffChatLogger;
		main.register(this);
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
		MessageManager mM = main.messageManager();
		if (args.length==0){
			if (sender instanceof Player player) {
				AstralPlayer astralPlayer = main.astralPlayer(player);
				astralPlayer.setStaffChatEnabled(!astralPlayer.isStaffChatEnabled());
				mM.message(sender, ("staff-chat.toggle."+astralPlayer.isStaffChatEnabled()).toLowerCase());
				return true;
			} else {
				mM.message(sender, "command-parse.incorrect-format", placeholder("command", "staffchat <message>"));
				return true;
			}
		} else {
			StringBuilder builder = new StringBuilder();
			for (String arg : args){
				if (builder.length()>0){
					builder.append(" ");
				}
				builder.append(arg);
			}
			final String message = builder.toString();
			String senderName = sender.getName();
			if (sender instanceof ConsoleCommandSender){
				senderName = "CONSOLE";
			} else if (sender instanceof BlockCommandSender){
				senderName = "COMMAND_BLOCK ("+senderName+")";
			} else if (sender instanceof RemoteConsoleCommandSender){
				senderName = "REMOTE_CONSOLE";
			}
			String[] placeholders = new String[]{
					placeholder("who", senderName),
					placeholder("message", message)
			};
			Bukkit.getOnlinePlayers().stream().filter(p->p.hasPermission("astraldupe.staff.chat")).forEach(player->{
				mM.message(player, "staff-chat.format", placeholders);
			});
			mM.message(Bukkit.getConsoleSender(), "staff-chat.format", placeholders);
			if (sender instanceof Player player){
				logger.log(player, message);
			} else {
				logger.log(sender, message);
			}
			JDA discordBot = ClassicDupeBot.getJDA();
			Guild guild = discordBot.getGuildById(main.getConfig().getString("discord.guild", "discord.guild"));
			assert guild != null;
			TextChannel channel = guild.getTextChannelById(main.getConfig().getString("discord.staff", "discord.staff"));
			assert channel != null;
			channel.sendMessage((AstralDupe.dev ? "**Dev Server** " : "") +"**"+senderName+"** "+message).queue();
		}
		return true;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onChat(AsyncChatEvent event){
		AstralPlayer astralPlayer = main.astralPlayer(event.getPlayer());
		if (!astralPlayer.isStaffChatEnabled()){
			return;
		}

		event.setCancelled(true);
		Component message = event.message();
		String serialized = MiniMessage.miniMessage().serialize(message);

		String[] placeholders = new String[]{
				placeholder("who", event.getPlayer().getName()),
				placeholder("message", serialized)
		};
		Bukkit.getOnlinePlayers().stream().filter(p->p.hasPermission("astraldupe.staff.chat")).forEach(player->{
			main.messageManager().message(player, "staff-chat.format", placeholders);
		});
		main.messageManager().message(Bukkit.getConsoleSender(), "staff-chat.format", placeholders);
		logger.log(event.getPlayer(), serialized);
		JDA discordBot = ClassicDupeBot.getJDA();
		Guild guild = discordBot.getGuildById(main.getConfig().getString("discord.guild", "discord.guild"));
		assert guild != null;
		TextChannel channel = guild.getTextChannelById(main.getConfig().getString("discord.staff", "discord.staff"));
		assert channel != null;
		channel.sendMessage((AstralDupe.dev ? "**Dev Server** " : "") +"**"+event.getPlayer().getName()+"** "+message).queue();
	}
}
