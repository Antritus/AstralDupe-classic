package me.antritus.mm.placeholder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class LegacyPlaceholder extends Placeholder {
	private static final MiniMessage miniMessage = MiniMessage.miniMessage();
	private static final LegacyComponentSerializer legacyAmpersand = LegacyComponentSerializer.legacyAmpersand();
	private static final LegacyComponentSerializer legacySection = LegacyComponentSerializer.legacySection();
	private static Component deserializeLegacy(@NotNull final String legacyValue){
		Component modernValue = legacyAmpersand.deserialize(legacyValue);
		String serialized = miniMessage.serialize(modernValue);
		modernValue = legacySection.deserialize(serialized);
		return modernValue;
	}
	public LegacyPlaceholder(@NotNull String key, @NotNull String legacyValue) {
		super(key, deserializeLegacy(legacyValue));
	}
}
