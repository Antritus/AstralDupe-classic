package me.antritus.astraldupe.listeners;

import bet.astral.messagemanager.Message;
import bet.astral.messagemanager.MessageManager;
import bet.astral.messagemanager.placeholder.LegacyPlaceholder;
import bet.astral.messagemanager.placeholder.Placeholder;
import io.papermc.paper.event.player.AsyncChatDecorateEvent;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.nms.ItemConverter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class ChatItemListener implements Listener {
	@SuppressWarnings("UnstableApiUsage")
	@EventHandler(priority = EventPriority.LOW)
	public void onDecorate(@NotNull AsyncChatDecorateEvent e) {
		Player player = e.player().getPlayer();
		if (player==null){
			return;
		}
		MiniMessage mm = MiniMessage.miniMessage();
		String serialized = mm.serialize(e.originalMessage());
		Component component = mm.deserialize(serialized);
		if (!serialized.contains("[item]") || !serialized.contains("[i]")){
			return;
		}
		if (player.hasPermission("astraldupe.chat.item")) {
			final String itemFlag = "<hover:show_item:%key%:%amount%:%nbt%>";
			ItemStack item = player.getInventory().getItemInMainHand();
			CompoundTag tagsCompoundTag = ItemConverter.tagsNBT(item);
			String fixedFlag = itemFlag.replace("%key%", item.translationKey());
			fixedFlag = fixedFlag.replace("%amount%", String.valueOf(item.getAmount()));
			fixedFlag = fixedFlag.replace("%nbt%", tagsCompoundTag.getAsString());

			ItemMeta meta = item.getItemMeta();

			Component displayName = meta.displayName() != null ? meta.displayName() : Component.translatable(item.translationKey());
			assert displayName != null;
			String serializedName = mm.serialize(displayName);



			Placeholder[] flags = new Placeholder[]{
					new LegacyPlaceholder("item", fixedFlag),
					new LegacyPlaceholder("amount", String.valueOf(item.getAmount())),
					new LegacyPlaceholder("displayname", serializedName),
					new LegacyPlaceholder("player", mm.serialize(player.name())),
					new LegacyPlaceholder("player_displayname", mm.serialize(player.displayName()))
			};
			MessageManager<AstralDupe> messageManager = AstralDupe.getInstance().messageManager();;
			Message message;
			if (item.getType().isAir()) {
				message = messageManager.getMessage("chat.item.air");
			} else {
				message = messageManager.getMessage("chat.item.item");
			}

			assert message != null;
			final Component finalHoverComponent = messageManager.parse(message, Message.Type.CHAT, flags);;
			component = component.replaceText((builder) -> {
				builder.match(Pattern.compile("\\[(?i)item]")).replacement(finalHoverComponent);
			});
			component = component.replaceText((builder) -> {
				builder.match(Pattern.compile("\\[(?i)i]")).replacement(finalHoverComponent);
			});
		}
		e.result(component);
	}
}
