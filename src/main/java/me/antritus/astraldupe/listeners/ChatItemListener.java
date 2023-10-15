package me.antritus.astraldupe.listeners;

import com.github.antritus.astral.utils.ColorUtils;
import io.papermc.paper.event.player.AsyncChatDecorateEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.regex.Pattern;

public class ChatItemListener implements Listener {
	@SuppressWarnings("UnstableApiUsage")
	@EventHandler(priority = EventPriority.HIGH)
	public void onDecorate(AsyncChatDecorateEvent e) {
		Player player = e.player().getPlayer();
		if (player==null){
			return;
		}
		// ^ part of v2
		MiniMessage mm = MiniMessage.miniMessage();
		// serialize it to normal string format (STRING)
		String serialized = mm.serialize(e.originalMessage());
		// Creating component here, so we don't need to set result 2 tims
		Component component = mm.deserialize(serialized);
		// Showing [item] in chat if he/she has permissions.
		if (player.hasPermission("astroldp.chat.item")) {
			Component itemComponent;
			if (player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
				itemComponent = Component.text().append(ColorUtils.translateComp("<white><bold>").append(player.name())).append(ColorUtils.translateComp("'s hand")).build();
			} else {
				ItemStack itemStack = player.getInventory().getItemInMainHand();
				itemComponent = Component.text().append((itemStack.getItemMeta().displayName() != null) ? Objects.requireNonNull(itemStack.getItemMeta().displayName()) : Component.translatable(itemStack.translationKey())).hoverEvent(itemStack).build();
			}
			component = component.replaceText((builder) -> {
				builder.match(Pattern.compile("\\[(?i)item]")).replacement(itemComponent);
			});
			component = component.replaceText((builder) -> {
				builder.match(Pattern.compile("\\[(?i)i]")).replacement(itemComponent);
			});
		}
		e.result(component);
	}
}
