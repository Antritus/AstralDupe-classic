package me.antritus.mm;

import me.antritus.mm.placeholder.Placeholder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public final class Message {
	private final String key;
	private final Map<Type, Component> messages;
	private final Map<String, Placeholder> builtInComponents;
	public boolean enabled = false;

	public Message(@NotNull String key, @NotNull Map<Type, Component> messages, @Nullable Map<String, Placeholder> builtInComponents) {
		this.key = key;
		this.messages = messages;
		this.builtInComponents = builtInComponents;
	}
	public Message(@NotNull String key, @NotNull Map<Type, Component> messages) {
		this.key = key;
		this.messages = messages;
		this.builtInComponents = null;
	}
	public Message(@NotNull String key, @NotNull Component message) {
		this.key = key;
		this.messages = Map.of(Type.CHAT, message);
		this.builtInComponents = null;
	}



	/**
	 * Returns given the message as serialized mini message value
	 * @param type type
	 * @return value
	 */
	@Nullable
	public String stringValue(Type type) {
		if (messages.get(type) == null){
			return null;
		}
		return MiniMessage.miniMessage().serialize(messages.get(type));
	}

	@NotNull
	public String key() {
		return key;
	}

	@Nullable
	public Component componentValue(Type type) {
		if (messages.get(type) == null){
			return null;
		}
		return messages.get(type);
	}

	@Nullable
	public Map<String, Placeholder> builtInPlaceholders() {
		return builtInComponents;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, messages, builtInComponents);
	}


	public enum Type{
		CHAT,
		ACTION_BAR,
		TITLE,
		SUBTITLE;

		@Override
		public String toString() {
			return name();
		}
	}
}
