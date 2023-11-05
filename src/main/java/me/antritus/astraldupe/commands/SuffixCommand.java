package me.antritus.astraldupe.commands;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.SuffixNode;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;

import java.util.*;

public class SuffixCommand extends AstralCommand {
	public static SuffixCommand suffixCommand;
	private final Map<UUID, SuffixMenu> menus = new HashMap<>();
	private final Map<String, Suffix> suffixes = new HashMap<>();
	private static final NamespacedKey key_suffix = new NamespacedKey("astraldupe", "suffix_name");
	public SuffixCommand(AstralDupe main) {
		super(main, "suffix");
		setAliases(List.of("suffixes", "tags", "tag"));
		suffixCommand = this;
		new MenuListener(this);
	}

	private static final NamespacedKey key_reset = new NamespacedKey("astraldupe", "suffix_reset");

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
		if (sender instanceof Player player) {
			if (menus.get(player.getUniqueId()) == null) {
				SuffixMenu menu = new SuffixMenu(this);
				menus.put(player.getUniqueId(), menu);
				menu.open(player);
				return true;
			}
			menus.get(player.getUniqueId()).open(player);
			return true;
		} else {
			main.messageManager().message(sender, "command-parse.player-only", "%command%=suffix");
		}
		return true;
	}


	public final void loadSuffixes() {
		MiniMessage miniMessage = MiniMessage.miniMessage();
		Config config = main.getConfig();
		suffixes.clear();
		menus.clear();
		List<Map<?, ?>> suffixListMap = config.getMapList("suffixes");
		for (Map<?, ?> map : suffixListMap) {
			String name = (String) map.get("name");
			String format = (String) map.get("format");
			String description = (String) map.get("description");
			String category = (String) map.get("category");
			Material material = Material.valueOf(
					(String) map.get("material")
			);
			int priority = (int) (map.get("priority") != null ? map.get("priority") : 0);
			Component formatComp = miniMessage.deserialize(format);
			Component descriptionComp = miniMessage.deserialize(description);
			Suffix suffix = new Suffix(name, category, priority, formatComp, descriptionComp, material);
			suffixes.put(suffix.name.toLowerCase(), suffix);
		}
	}


	private void setSuffix(UUID id, Suffix suffix) {
		LuckPerms luckPerms = (LuckPerms) AstralDupe.cosmicCapital;
		UserManager userManager = luckPerms.getUserManager();
		if (!userManager.isLoaded(id)) {
			userManager.loadUser(id).thenRunAsync(() -> {
				User user = userManager.getUser(id);
				if (user == null) {
					return;
				}
				grant(userManager, user, suffix);
			});
		}
		User user = userManager.getUser(id);
		if (user == null) {
			return;
		}
		grant(userManager, user, suffix);
	}

	private void grant(@NotNull UserManager manager, @NotNull User user, @NotNull Suffix suffix) {
		Collection<Node> nodes = user.getNodes();
		nodes.removeIf(node -> {
			if (node instanceof SuffixNode suffixNode) {
				return suffixNode.getPriority() == 100;
			}
			return false;
		});
		nodes.add(SuffixNode.builder(suffix.legacyFormat(), 100).build());
		manager.saveUser(user);
	}

	private void clear(@NotNull UserManager userManager, @NotNull User user) {
		Collection<Node> nodes = user.getNodes();
		nodes.removeIf(node -> node instanceof SuffixNode);
		userManager.saveUser(user);
	}

	private void clear(UUID uuid) {
		UserManager userManager = LuckPermsProvider.get().getUserManager();
		if (!userManager.isLoaded(uuid)) {
			userManager.loadUser(uuid).thenRunAsync(() -> {
				User user = userManager.getUser(uuid);
				if (user == null) {
					return;
				}
				clear(userManager, user);
			});
		}
		User user = userManager.getUser(uuid);
		if (user == null) {
			return;
		}
		clear(userManager, user);
	}


	private final static class Suffix {
		private final String name;
		private final String permission;
		private final String category;
		private final int priority;
		private final Component format;
		private final Component description;
		private final Material material;

		@Contract(pure = true)
		public Suffix(String name, String category, int priority, Component format, Component description, Material material) {
			this.name = name;
			this.category = category;
			this.priority = priority;
			this.format = format;
			this.permission = "astraldupe.suffix." + name.toLowerCase();
			this.description = description;
			this.material = material;
		}

		public @NotNull String legacyFormat() {
			LegacyComponentSerializer legacyComponentSerializer = LegacyComponentSerializer.legacyAmpersand();
			return legacyComponentSerializer.serialize(format);
		}

	}

	private final static class SuffixMenu implements InventoryHolder {
		private final SuffixCommand suffixCommand;
		private final Inventory inventory;
		private final ItemStack background = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
		private final ItemStack footer = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		private final ItemStack reset = new ItemStack(Material.BARRIER);
		MiniMessage miniMessage = MiniMessage.miniMessage();

		{
			ItemMeta backgroundMeta = background.getItemMeta();
			backgroundMeta.displayName(
					miniMessage.deserialize("<red>"));
			background.setItemMeta(backgroundMeta);

			ItemMeta footerMeta = background.getItemMeta();
			footerMeta.displayName(
					miniMessage.deserialize("<red>"));
			background.setItemMeta(footerMeta);

			ItemMeta resetMeta = reset.getItemMeta();
			resetMeta.displayName(
					miniMessage.deserialize("<red<Reset Suffix")
							.decoration(TextDecoration.ITALIC, false));
			resetMeta.
					getPersistentDataContainer()
					.set(key_reset, PersistentDataType.BOOLEAN, true);
			reset.setItemMeta(resetMeta);
		}

		public SuffixMenu(SuffixCommand suffixCommand) {
			this.suffixCommand = suffixCommand;
			inventory = Bukkit.createInventory(this, 54, Component.text("Suffixes"));
		}

		public void open(Player player) {
			inventory.clear();
			for (int i = 0; i < inventory.getSize() - 9; i++) {
				inventory.setItem(i, background);
			}
			for (int i = inventory.getSize() - 9; i < inventory.getSize(); i++) {
				inventory.setItem(i, footer);
			}
			inventory.setItem(inventory.getSize() - 5, reset);

			int i = 0;
			List<Suffix> suffixList = new ArrayList<>(suffixCommand.suffixes.values().stream().toList());
			suffixList.sort(new SuffixComparator());
			for (Suffix suffix : suffixList) {
				ItemStack suffixItem = new ItemStack(suffix.material);
				ItemMeta itemMeta = suffixItem.getItemMeta();
				List<Component> lore = new ArrayList<>();
				Component non_italic = Component.empty().decoration(TextDecoration.ITALIC, false);
				lore.add(non_italic.appendSpace());
				lore.add(non_italic.append(miniMessage
						.deserialize("<dark_gray> | <gray>Name: <white>" + suffix.name)
				));
				lore.add(non_italic.append(miniMessage
						.deserialize("<dark_gray> | <gray>Category: <white>" + suffix.category)
				));
				lore.add(non_italic.append(miniMessage
						.deserialize("<dark_gray> | <gray>Suffix: <white>").append(suffix.format)
				));
				lore.add(non_italic.append(miniMessage
						.deserialize("<dark_gray> | <gray>Name: <white>" + suffix.name)
				));
				lore.add(non_italic.append(miniMessage
						.deserialize("<dark_gray> | <gray>Description: <white>").append(suffix.description)));


				if (player.hasPermission(suffix.permission)) {
					lore.add(
							non_italic.append(miniMessage.deserialize("<dark_gray> | <gray>Unlocked: <green>Yes"))
					);
				} else {
					lore.add(
							non_italic.append(miniMessage.deserialize("<dark_gray> | <gray>Unlocked: <green>Yes"))
					);
				}
				lore.add(non_italic.appendSpace());
				itemMeta.lore(lore);

				Component playerName = Utils.format((Utils.getPrefix(player)) + miniMessage.serialize(player.displayName()));


				Component itemName = non_italic
						.append(playerName)
						.append(ColorUtils.translateComp((Utils.getSuffix(player) != null) ? " " + Utils.convertColorCodesToAdventure(Utils.getSuffix(player)) : ""))
						.append(ColorUtils.translateComp(" <gray>» <white>"))
						.append(ColorUtils.translateComp("Hey!"));
				itemMeta.displayName(itemName);

				PersistentDataContainer container = itemMeta.getPersistentDataContainer();
				container.set(key_suffix, PersistentDataType.STRING, suffix.name);

				suffixItem.setItemMeta(itemMeta);
				inventory.setItem(i, suffixItem);
				i++;
			}

			player.openInventory(inventory);
		}


		@Override
		public @NotNull Inventory getInventory() {
			return inventory;
		}
	}

	private static class SuffixComparator implements java.util.Comparator<Suffix> {
		@Override
		public int compare(Suffix a, Suffix  b) {
			return a.priority - b.priority;
		}
	}

	private record MenuListener(SuffixCommand suffixCommand) implements Listener {
		private MenuListener(SuffixCommand suffixCommand) {
			this.suffixCommand = suffixCommand;
			suffixCommand.main.register(this);
		}

		@EventHandler
		@SuppressWarnings("unused")
		public void onInventoryClick(InventoryClickEvent event) {
			if (event.getInventory().getHolder() instanceof SuffixMenu) {
				event.setCancelled(true);
				event.setResult(Event.Result.DENY);
				ItemStack itemStack = event.getCurrentItem();
				if (event.getCurrentItem()==null){
					return;
				}
				assert itemStack != null;
				ItemMeta meta = itemStack.getItemMeta();
				if (meta.getPersistentDataContainer().has(key_suffix)) {
					Suffix suffix = suffixCommand.suffixes.get(
							meta.getPersistentDataContainer().get(key_suffix, PersistentDataType.STRING));
					if (event.getWhoClicked().hasPermission(suffix.permission)) {
						suffixCommand.setSuffix(event.getWhoClicked().getUniqueId(), suffix);
						MiniMessage mM = MiniMessage.miniMessage();
						suffixCommand.main.messageManager().message(event.getWhoClicked(), "suffix.set", "%suffix%=" + mM.serialize(suffix.format));
					} else {
						suffixCommand.main.messageManager().message(event.getWhoClicked(), "suffix.no-permission");
					}
				} else if (meta.getPersistentDataContainer().has(key_reset)) {
					suffixCommand.clear(event.getWhoClicked().getUniqueId());
				}
			}
		}

		@EventHandler
		@SuppressWarnings("unused")
		public void onInventoryDrag(InventoryDragEvent event) {
			if (event.getInventory().getHolder() instanceof SuffixMenu) {
				event.setCancelled(true);
				event.setResult(Event.Result.DENY);
			}
		}
	}
}