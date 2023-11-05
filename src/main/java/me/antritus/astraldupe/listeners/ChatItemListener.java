package me.antritus.astraldupe.listeners;

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

import java.util.regex.Pattern;

import static me.antritus.astraldupe.MessageManager.placeholder;

public class ChatItemListener implements Listener {
	@SuppressWarnings("UnstableApiUsage")
	@EventHandler(priority = EventPriority.LOW)
	public void onDecorate(AsyncChatDecorateEvent e) {
		Player player = e.player().getPlayer();
		if (player==null){
			return;
		}
		MiniMessage mm = MiniMessage.miniMessage();
		String serialized = mm.serialize(e.originalMessage());
		Component component = mm.deserialize(serialized);
		if (player.hasPermission("astraldupe.chat.item")) {
			final String itemFlag = "<hover:show_item:%key%:%amount%:%nbt%>";
			ItemStack item = player.getInventory().getItemInMainHand();
			CompoundTag tagsCompoundTag = ItemConverter.tagsNBT(item);
			String fixedFlag = itemFlag.replace("%key%", item.translationKey());
			fixedFlag = itemFlag.replace("%amount%", String.valueOf(item.getAmount()));
			fixedFlag = itemFlag.replace("%nbt%", tagsCompoundTag.getAsString());

			ItemMeta meta = item.getItemMeta();

			Component displayName = meta.displayName() != null ? meta.displayName() : Component.translatable(item.translationKey());
			assert displayName != null;
			String serializedName = mm.serialize(displayName);



			String[] flags = new String[]{
					placeholder("item", fixedFlag),
					placeholder("amount", String.valueOf(item.getAmount())),
					placeholder("displayname", serializedName),
					placeholder("player", mm.serialize(player.name())),
					placeholder("player_displayname", mm.serialize(player.displayName()))
			};
			AstralDupe astralDupe = AstralDupe.getInstance();

			Component hoverComponent = AstralDupe.getInstance().messageManager().parse(true,
					"chat.item.item", flags);
			if (item.getType().isAir())
				hoverComponent = astralDupe.messageManager().parse(true,
						"chat.item.air", flags);

			final Component finalHoverComponent = hoverComponent;
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
