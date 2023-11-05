package me.antritus.mm;

import com.google.common.collect.ImmutableMap;
import me.antritus.mm.placeholder.LegacyPlaceholder;
import me.antritus.mm.placeholder.MessagePlaceholder;
import me.antritus.mm.placeholder.Placeholder;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Message manager which loads messages in runtime and parses them.
 * @param <P> Plugin
 * @param <C> Configuration
 * @param <M> Map to save messages & disabled messages
 */
public class MessageManager<P extends JavaPlugin, C extends FileConfiguration, M extends Map<String, ?>> {
	private final MiniMessage miniMessage = MiniMessage.miniMessage();
	private final P plugin;
	private final C config;
	private final BukkitAudiences bukkitAudiences;
	private final ImmutableMap<String, Placeholder> immutablePlaceholders;
	private final M messagesMap;
	private final List<String> disabledMessages;
	private @Nullable PlaceholderAPIPlugin placeholderAPI = null;

	public MessageManager(P plugin, C config, M messageMap){
		this.plugin = plugin;
		this.config = config;
		this.bukkitAudiences = createBukkitAudiences();
		this.immutablePlaceholders = loadPlaceholders();
		this.messagesMap = messageMap;
		this.disabledMessages = new LinkedList<>();
		if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null)
			placeholderAPI = PlaceholderAPIPlugin.getInstance();
	}


	private BukkitAudiences createBukkitAudiences() {
		return BukkitAudiences.create(plugin);
	}

	private ImmutableMap<String, Placeholder> loadPlaceholders(){
		Map<String, Placeholder> placeholderMap = new HashMap<>();
		List<Map<?, ?>> placeholderMapList = config.getMapList("placeholders");
		for (Map<?, ?> map : placeholderMapList){
			String name = (String) map.get("name");
			String value = (String) map.get("value");
			Component componentValue = miniMessage.deserialize(value);
			Placeholder placeholder = new Placeholder(name, componentValue);
			placeholderMap.put(placeholder.key(), placeholder);
		}
		return ImmutableMap.copyOf(placeholderMap);
	}

	public Message loadMessage(String messageKey){
		Object messageSection = config.get(messageKey);
		Message message = null;
		if (messageSection instanceof String){
			Component messageComponent = miniMessage.deserialize((String) messageSection);
			message = new Message(messageKey, messageComponent);
			return message;
		} else if (messageSection instanceof MemorySection memorySection) {
			boolean legacy = memorySection.getBoolean("legacy", false);
			String serializer = memorySection.getString("serializer", "mini-message").toLowerCase();
			ComponentSerializer<?, ?, ?> componentSerializer = null;
			switch (serializer){
				case "mini-message"->{

				}
			}
			Object chatObj = memorySection.get("chat");
			Object titleObj = memorySection.getString("title");
			Object subtitleObj = memorySection.getString("subtitle");
			Object actionBarObj = memorySection.get("action-bar");
			Map<Message.Type, Component> componentMap = new HashMap<>();


		} else {
			throw new IllegalStateException("Message configuration for "+ messageKey + " is illegal. Pleace fix it!");
		}
		return message;
	}


	public Placeholder placeholder(String name, String value, boolean legacy){
		if (legacy){
			return new LegacyPlaceholder(name, value);
		}
		return new Placeholder(name, value, false);
	}

	public Placeholder placeholder(String name, Component value){
		return new Placeholder(name, value);
	}

	public MessagePlaceholder placeholderMessage(String name, String messageKey, Message.Type type){
		Message message = (Message) messagesMap.get(messageKey);
		if (message==null){
			loadMessage(messageKey);
			message = (Message) messagesMap.get(messageKey);
			if (message == null){
				return MessagePlaceholder.emptyPlaceholder(name);
			}
		}
		return MessagePlaceholder.create(name, message, type);
	}



}
