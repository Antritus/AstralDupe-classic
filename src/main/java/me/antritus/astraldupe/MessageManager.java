package me.antritus.astraldupe;

import com.google.common.collect.ImmutableMap;
import me.antritus.astraldupe.utils.Configuration;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This is a message manager which loads messages from plugins/plugin/messages.yml
 * It is designed to work in 1.8 as for my factions were first made for 1.8.
 * Removing some of the methods that allow 1.8 use it is possible, and could
 * speed up the progress.
 * But removing the support for 1.8 is not supported by @antritus.
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public class MessageManager {
	private final MiniMessage miniMessage = MiniMessage.miniMessage();
	private final JavaPlugin main;
	public final Configuration messageConfig;
	private final BukkitAudiences bukkitAudiences;
	private final ImmutableMap<String, Component> placeholders;
	private final Map<String, Component> messages = new LinkedHashMap<>();
	private final Map<String, String> commandExceptions = new LinkedHashMap<>();
	private final Map<String, Boolean> unused = new LinkedHashMap<>();
	public MessageManager(JavaPlugin main){
		BukkitAudiences bukkitAudiences1;
		this.main = main;
		try {
			Player.class.getMethod("sendMessage", Component.class);
			bukkitAudiences1 = null;
		} catch (NoSuchMethodException e) {
			bukkitAudiences1 = BukkitAudiences.create(main);
		}
		bukkitAudiences = bukkitAudiences1;

		try {
			messageConfig = new Configuration(main, new File(main.getDataFolder(), "messages.yml"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		placeholders = loadPlaceholders();
	}

	public Collection<String> messageKeys(){
		return messages.keySet();
	}
	private @NotNull ImmutableMap<String, Component> loadPlaceholders() {
		List<Map<?, ?>> placeholderMap = messageConfig.getMapList("placeholders");
		ImmutableMap.Builder<String, Component> builder = ImmutableMap.builder();
		for (Map<?, ?> map : placeholderMap) {
			String name = (String) map.get("name");
			String txt = (String) map.get("text");
			if (name.equalsIgnoreCase("prefix")){
				txt = AstralDupe.astralDupe;
			}
			builder.put(name, miniMessage.deserialize(txt));
		}
		return builder.build();
	}

	public String parseCommandException(String key, String... placeholders) {
		loadCommandException(key);
		if (unused.get(key)!=null&&unused.get(key)){
			return key;
		}
		String message = commandExceptions.get(key);
		for (String placeholder : placeholders) {
			try {
				String[] split = placeholder.split("=");
				if (!split[0].startsWith("%")) {
					split[0] = "%" + split[0];
				}
				if (!split[0].endsWith("%")) {
					split[0] = split[0] + "%";
				}
				message = message.replaceAll("(?i)" + split[0], split[1]);
			} catch (ArrayIndexOutOfBoundsException ignore) {
			}
		}
		return message;
	}
	private void loadCommandException(String key){
		if (commandExceptions.get(key) == null && unused.get(key) == null){
			String message = messageConfig.getString(key, key);
			commandExceptions.put(key, message);
		}
	}


	private Component checkPlaceholders(Component msg){
		if (placeholders.size() == 0){
			return msg;
		}
		for (String placeholder : placeholders.keySet()) {
			if (placeholder == null){
				continue;
			}
			placeholder = placeholder.replace("%", "");
			String finalPlaceholder = placeholder;
			msg = msg.replaceText((TextReplacementConfig.Builder builder) -> builder.match("%" + finalPlaceholder + "%").replacement(placeholders.get(finalPlaceholder)));
		}
		return msg;
	}
	private Component checkLocalPlaceholders(Component msg, String @NotNull ... placeholders){
		if (placeholders.length == 0){
			return msg;
		}
		MiniMessage miniMessage = MiniMessage.miniMessage();
		for (String placeholder : placeholders) {
			if (placeholder == null){
				continue;
			}
			String[] split = placeholder.split("=", 2);
			if (split.length == 1){
				continue;
			}
			placeholder = placeholder.replace("%", "");
			split = placeholder.split("=", 2);
			String[] finalSplit = split;
			msg = msg.replaceText((TextReplacementConfig.Builder builder) -> builder.match("%" + finalSplit[0] + "%").replacement(miniMessage.deserialize(finalSplit[1])));
		}
		return msg;
	}
	private void load(String key){
		if (unused.get(key) != null && unused.get(key)){
			return;
		}
		if (messages.get(key) == null){
			if (messageConfig.isList(key)){
				ArrayList<String> msg = new ArrayList<>(messageConfig.getStringList(key));
				ArrayList<Component> components = new ArrayList<>();
				for (String value : msg) {
					try {
						Component s = miniMessage.deserialize(value);
						s = checkPlaceholders(s);
						components.add(s);
					} catch (IndexOutOfBoundsException ignore) {
					}
				}
				Component combinedComponent = null;
				for (Component comp : components) {
					if (combinedComponent == null) {
						combinedComponent = comp;
					} else {
						combinedComponent = combinedComponent.appendNewline().append(comp);
					}
				}
				messages.put(key, combinedComponent);
			} else {
				String msg = messageConfig.getString(key);
				if (msg == null){
					msg = key;
				}
				if (msg.equalsIgnoreCase("UNUSED")){
					unused.put(key, true);
				}
				Component component;
				component = miniMessage.deserialize(msg);
				component = checkPlaceholders(component);
				messages.put(key, component);
			}
		}
	}
	public Component parse(boolean reparse, String key, String... placeholders){
		MiniMessage miniMessage = MiniMessage.miniMessage();
		load(key);
		if (unused.get(key) != null && unused.get(key)) {
			return null;
		}
		Component msg = ((messages.get(key) != null ? messages.get(key) : Component.text(key)).appendSpace());
		msg = checkLocalPlaceholders(msg, placeholders);
		if (reparse){
			String deserialiazed = miniMessage.serialize(msg);
			for (String placeholder : placeholders) {
				try {
					deserialiazed = deserialiazed.replace(placeholder.split("=")[0], placeholder.split("=")[1]);
				} catch (ArrayIndexOutOfBoundsException ignore){
				}
			}
			return miniMessage.deserialize(deserialiazed);
		}
		return msg;
	}
	// Switch to async messages to reduce parse time from the server.
	public void message(CommandSender player, String key, String... placeholders) {
		if (unused.get(key) != null && unused.get(key)){
			return;
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				send(player, parse(true, key, placeholders));
			}
		}.runTaskAsynchronously(main);
	}
	// Switch to async messages to reduce parse time from the server.
	public void message(boolean reparse, CommandSender sender, String key, String... placeholders) {
		if (unused.get(key) != null && unused.get(key)){
			return;
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				if (sender instanceof Player player) {
					advancedMessage(player, key, placeholders);
				} else {
					send(sender, parse(true, key, placeholders));
				}
			}
		}.runTaskAsynchronously(main);
	}
	public void messageDelayed(int delayTicks, CommandSender sender, String key, String... placeholders) {
		if (unused.get(key) != null && unused.get(key)){
			return;
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				if (sender instanceof Player player) {
					advancedMessage(player, key, placeholders);
				} else {
					send(sender, parse(true, key, placeholders));
				}
			}
		}.runTaskLaterAsynchronously(main, delayTicks);
	}
	private void advancedMessage(Player player, String key, String... placeholders){
		if (messageConfig.isConfigurationSection(key)){
			if (messageConfig.isSet(key+".chat")){
				send(player, parse(true, key, placeholders));
			}
			if (messageConfig.isSet(key+".title")){
				if (messageConfig.isSet(key+".subtitle")){
					sendTitle(player, key+".title", key+".subtitle", placeholders);
				} else {
					sendTitle(player, key+".title", placeholders);
				}
			} else if (messageConfig.isSet(key+".subtitle")){
				sendSubtitle(player, key+".title", placeholders);
			}
			if (messageConfig.isSet(key+".hotbar")){
				sendHotbar(player, key+".hotbar", placeholders);
			}
		} else {
			send(player, parse(true, key, placeholders));
		}
	}
	public void sendHotbar(Player player, String key, String... placeholders){
		Component parsed = parse(true, key, placeholders);
		if (parsed==null){
			return;
		}
		if (bukkitAudiences==null){
			player.sendActionBar(parsed);
		} else {
			//noinspection RedundantCast
			((Audience) player).sendActionBar(parsed);
		}
	}
	public void sendTitle(Player player, String keyTitle, String keySubtitle, String... placeholders){
		new BukkitRunnable() {
			@Override
			public void run() {
				Component title = parse(true, keyTitle, placeholders);
				Component subtitle = parse(true, keySubtitle, placeholders);
				if (title!=null)
					player.sendTitlePart(TitlePart.TITLE, title);
				if (subtitle!=null)
					player.sendTitlePart(TitlePart.SUBTITLE, subtitle);
			}
		}.runTaskAsynchronously(main);
	}
	public void sendTitleDelay(int tickDelay, Player player, String keyTitle, String keySubtitle, String... placeholders){
		new BukkitRunnable() {
			@Override
			public void run() {
				Component title = parse(true, keyTitle, placeholders);
				Component subtitle = parse(true, keySubtitle, placeholders);
				if (bukkitAudiences == null) {
					if (title!=null)
						player.sendTitlePart(TitlePart.TITLE, title);
					if (subtitle!=null)
						player.sendTitlePart(TitlePart.SUBTITLE, subtitle);
				} else {
					Audience audience = bukkitAudiences.player(player);
					if (title!=null)
						audience.sendTitlePart(TitlePart.TITLE, title);
					if (subtitle!=null)
						audience.sendTitlePart(TitlePart.SUBTITLE, subtitle);
				}
			}
		}.runTaskLaterAsynchronously(main, tickDelay);
	}
	public void sendTitle(Player player, String keyTitle, String... placeholders){
		new BukkitRunnable() {
			@Override
			public void run() {
				Component title = parse(true, keyTitle, placeholders);
				if (title != null){
					if (bukkitAudiences == null) {
						player.sendTitlePart(TitlePart.TITLE, title);
					} else {
						bukkitAudiences.player(player).sendTitlePart(TitlePart.TITLE, title);
					}
				}

			}
		}.runTaskAsynchronously(main);
	}
	public void sendTitleDelay(int tickDelay, Player player, String keyTitle, String... placeholders){
		new BukkitRunnable() {
			@Override
			public void run() {
				Component title = parse(true, keyTitle, placeholders);
				if (title!=null) {
					if (bukkitAudiences == null) {
						player.sendTitlePart(TitlePart.TITLE, title);
					} else {
						bukkitAudiences.player(player).sendTitlePart(TitlePart.TITLE, title);
					}
				}
			}
		}.runTaskLaterAsynchronously(main, tickDelay);	}
	public void sendSubtitle(Player player, String keySubtitle, String... placeholders){
		new BukkitRunnable() {
			@Override
			public void run() {
				Component subtitle = parse(true, keySubtitle, placeholders);
				if (subtitle != null) {
					if (bukkitAudiences == null) {
						player.sendTitlePart(TitlePart.SUBTITLE, subtitle);
					} else {
						bukkitAudiences.player(player).sendTitlePart(TitlePart.SUBTITLE, subtitle);
					}
				}
			}
		}.runTaskAsynchronously(main);
	}
	public void sendSubtitleDelay(int tickDelay, Player player, String keySubtitle, String... placeholders){
		new BukkitRunnable() {
			@Override
			public void run() {
				Component subtitle = parse(true, keySubtitle, placeholders);
				if (subtitle != null)
					if (bukkitAudiences == null) {
						player.sendTitlePart(TitlePart.SUBTITLE, subtitle);
					} else {
						bukkitAudiences.player(player).sendTitlePart(TitlePart.SUBTITLE, subtitle);
					}
			}
		}.runTaskLaterAsynchronously(main, tickDelay);
	}
	public void clearTitle(@NotNull Player player){
		player.clearTitle();
	}
	private void send(CommandSender sender, Component component){
		if (component==null){
			return;
		}
		if (bukkitAudiences != null) {
			if (sender instanceof Player) {
				bukkitAudiences.player((Player) sender).sendMessage(component);
			} else {
				bukkitAudiences.sender(sender).sendMessage(component);
			}
		} else {
			((Audience) sender).sendMessage(component);
		}
	}
	public void broadcast(String key, String... placeholders){
		Component component = parse(true, key, placeholders);
		if (component == null){
			return;
		}
		if (bukkitAudiences == null) {
			broadcast(component);
			return;
		}
		bukkitAudiences.all().sendMessage(component);
	}
	public String formatList(String formatKey, @NotNull List<String> list) {
		String comma = messageConfig.getString(formatKey + ".comma", ", ");
		String valuePlaceholder = messageConfig.getString(formatKey + ".value", "%value%");
		boolean shouldEndWithAnd = messageConfig.getBoolean(formatKey + ".end-with-and.yes", false);
		String endWithAnd = messageConfig.getString(formatKey + ".end-with-and.and", " and ");
		String empty = messageConfig.getString(formatKey+".empty", "None");

		StringBuilder builder = new StringBuilder();
		int size = list.size();

		for (int i = 0; i < size; i++) {
			String val = list.get(i);

			if (i > 0) {
				if (shouldEndWithAnd && i == size - 1) {
					builder.append(endWithAnd);
				} else {
					builder.append(comma);
				}
			}

			builder.append(valuePlaceholder.replace("%value%", val));
		}

		if (builder.isEmpty()){
			builder.append(empty);
		}
		return builder.toString();
	}

	public String formatArray(String formatKey, String @NotNull ... array) {
		String comma = messageConfig.getString(formatKey + ".comma", ", ");
		String valuePlaceholder = messageConfig.getString(formatKey + ".value", "%value%");
		boolean shouldEndWithAnd = messageConfig.getBoolean(formatKey + ".end-with-and.yes", false);
		String endWithAnd = messageConfig.getString(formatKey + ".end-with-and.and", " and ");
		String empty = messageConfig.getString(formatKey+".empty", "None");

		StringBuilder builder = new StringBuilder();
		int length = array.length;

		for (int i = 0; i < length; i++) {
			String val = array[i];

			if (i > 0) {
				if (shouldEndWithAnd && i == length - 1) {
					builder.append(endWithAnd);
				} else {
					builder.append(comma);
				}
			}

			builder.append(valuePlaceholder.replace("%value%", val));
		}

		if (builder.isEmpty()){
			builder.append(empty);
		}
		return builder.toString();
	}
	private final Method broadcast;
	{
		//noinspection RedundantSuppression
		try {
			//noinspection JavaReflectionMemberAccess
			broadcast = Bukkit.class.getMethod("broadcast", Component.class);
			broadcast.setAccessible(true);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
	private void broadcast(Component components){
		try {
			broadcast.invoke(null, components);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Contract(pure = true)
	public static @NotNull String placeholder(String prefix, String data){
		return "%"+prefix+"%="+data;
	}

}