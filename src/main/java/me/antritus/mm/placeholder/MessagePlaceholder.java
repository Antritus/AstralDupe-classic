package me.antritus.mm.placeholder;

import me.antritus.mm.Message;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MessagePlaceholder extends Placeholder {
	private final Message message;
	private final Message.Type type;
	private MessagePlaceholder(@NotNull String name, @NotNull Message message, @NotNull Message.Type type) {
		super(name, Objects.requireNonNull(message.componentValue(type)));
		this.message = message;
		this.type = type;
	}
	private MessagePlaceholder(String name){
		super(name, name, false);
		this.message = null;
		this.type = null;
	}

	public static MessagePlaceholder emptyPlaceholder(String name){
		return new EmptyPlaceholder(name);
	}

	public static MessagePlaceholder create(String name, Message message, Message.Type type){
		if (message.componentValue(type) == null){
			return new EmptyPlaceholder(name);
		}
		return new MessagePlaceholder(name, message, type);
	}
	public static MessagePlaceholder createChat(String name, Message message){
		return create(name, message, Message.Type.CHAT);
	}

	@Override
	public @Nullable Component componentValue() {
		return message.componentValue(type);
	}

	@Override
	public String stringValue() {
		return message.stringValue(type);
	}

	@Override
	public boolean isComponentValue() {
		return true;
	}

	private static class EmptyPlaceholder extends MessagePlaceholder {
		private final String key;
		private final Component keyComponent;
		public EmptyPlaceholder(String key) {
			super(key);
			this.key = key;
			this.keyComponent = Component.text(key);
		}

		@Override
		public @Nullable Component componentValue() {
			return keyComponent;
		}

		@Override
		public String stringValue() {
			return key;
		}

	}
}