package me.antritus.astraldupe;


import me.antritus.astral.cosmiccapital.api.CosmicCapitalAPI;
import me.antritus.astral.cosmiccapital.api.providers.EconomyProvider;
import me.antritus.astral.factions.api.FactionsAPI;
import me.antritus.astral.factions.api.FactionsAPIProvider;
import me.antritus.astral.fluffycombat.FluffyCombat;
import me.antritus.astraldupe.anticheat.FlagStackSize;
import me.antritus.astraldupe.commands.DupeCommand;
import me.antritus.astraldupe.commands.StoreAnnouncementCommand;
import me.antritus.astraldupe.commands.SuffixCommand;
import me.antritus.astraldupe.commands.ToggleCommand;
import me.antritus.astraldupe.commands.staff.low.StaffChatCommand;
import me.antritus.astraldupe.entity.AstralPlayer;
import me.antritus.astraldupe.listeners.*;
import me.antritus.astraldupe.loggers.ChatLogger;
import me.antritus.astraldupe.loggers.MessageLogger;
import me.antritus.astraldupe.loggers.StaffChatLogger;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.Registry;
import org.bukkit.block.DecoratedPot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.ClassicDupe;

import java.util.*;

public class AstralDupe extends ClassicDupe {
	private static final String discord = "discord.gg/uh6h7faR9p";
	public static final String astralColor = "<color:#7f29f0>";
	public static final String astralDupe = "<click:open_url:'"+"https://"+discord+"'><hover:show_text:'<red>AstralDoop\n<gray>Report Bugs: <white>"+discord+"'><b>"+astralColor+"Astral<RESET>";

	public final static boolean dev = false;
	public final static int season = 0;
	public final static String seasonFormat = "<dark_aqua>B<aqua>E<dark_aqua>T<aqua>A<reset>";
	public static AstralDupeEconomy economy;
	public static MessageLogger messageLogger;
	public static ChatLogger chatLogger;
	public StaffChatLogger staffChatLogger;
	private static AstralDupe instance;
	public static FactionsAPI<?> factionsAPI;
	public static CosmicCapitalAPI cosmicCapital;
	public static FluffyCombat fluffyCombat;
	public static List<Material> illegalDupes = new ArrayList<>();
	private MessageManager messageManager;
	private JoinListener joinListener;
	private Map<UUID, AstralPlayer> players = new LinkedHashMap<>();

	@Override
	public void onEnable() {
		super.onEnable();
		instance = this;
		messageManager = new MessageManager(this);
		try {
			Class.forName("me.antritus.astral.factions.api.FactionsAPI");
			factionsAPI = FactionsAPIProvider.get();
		} catch (ClassNotFoundException ignored) {
		}

		if (getServer().getPluginManager().getPlugin("CosmicCapital") != null) {
			cosmicCapital = EconomyProvider.getAPI();
			economy = new AstralDupeEconomy(this);
			register(new BountyClaimListener());
			register(new GiveKillMoneyListener());
		}

		if (getServer().getPluginManager().getPlugin("Fluffy") != null)
			fluffyCombat = FluffyCombat.getPlugin(FluffyCombat.class);
		if (fluffyCombat == null) {
			this.getServer().getPluginManager().disablePlugin(this);
			getLogger().severe("Couldn't find Fluffy(Combat) in the plugins! Disabling plugin!");
			return;
		}

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
			PlaceholderAPIPlugin.getInstance().getLocalExpansionManager().register(AstralDupeExpansion.class);


		List<String> forbiddenDupes = getConfig().getStringList("forbiddenDupes");
		forbiddenDupes.forEach(item -> {
			try {
				Material material = Material.valueOf(item);
				illegalDupes.add(material);
			} catch (IllegalArgumentException ignore) {
			}
		});

		messageLogger = new MessageLogger("message");
		chatLogger = new ChatLogger(this, "chat");
		staffChatLogger = new StaffChatLogger(this, "staff-chat");
		register(chatLogger);
		register(staffChatLogger);

		register(new ChatItemListener());
		register(new PortalFixerListener());
		register(new ToggleListener(this));


		CommandMap commandMap = getServer().getCommandMap();
		List<Command> commands = new LinkedList<>();
		commands.add(new DupeCommand(this));
		commands.add(new SuffixCommand(this));
		commands.add(new StoreAnnouncementCommand(this));
		commands.add(new StaffChatCommand(this));
		commands.add(new ToggleCommand(this));

		commandMap.registerAll("astraldupe", commands);
		SuffixCommand.suffixCommand.loadSuffixes();

		joinListener = new JoinListener(this);
		register(joinListener);

		new FlagStackSize();

		randomItems();
	}

	private void randomItems(){
		for (MusicInstrument instrument : Registry.INSTRUMENT){
			ItemStack horn = new ItemStack(Material.GOAT_HORN);
			MusicInstrumentMeta musicInstrumentMeta = (MusicInstrumentMeta) horn.getItemMeta();
			musicInstrumentMeta.setInstrument(instrument);
			horn.setItemMeta(musicInstrumentMeta);
			randomItems.add(horn);
		}
		for (int i = 0; i < 3; i++){
			ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);
			FireworkMeta fireworkMeta = (FireworkMeta) firework.getItemMeta();
			fireworkMeta.setPower(i);
			firework.setItemMeta(fireworkMeta);
			randomItems.add(firework);
		}
		for (Material sherd : Arrays.stream(Material.values()).filter(material->material.name().endsWith("POTTERY_SHERD")).toList()){
			ItemStack itemStack = new ItemStack(Material.DECORATED_POT);
			BlockStateMeta itemMeta = (BlockStateMeta) itemStack.getItemMeta();
			DecoratedPot pot = (DecoratedPot) itemMeta.getBlockState();
			pot.setSherd(DecoratedPot.Side.FRONT, sherd);
			pot.setSherd(DecoratedPot.Side.LEFT, sherd);
			pot.setSherd(DecoratedPot.Side.RIGHT, sherd);
			pot.setSherd(DecoratedPot.Side.BACK, sherd);
			itemMeta.setBlockState(pot);
			itemStack.setItemMeta(itemMeta);
			randomItems.add(itemStack);
		}
	}

	@Override
	public void onDisable() {
		super.onDisable();
	}


	@NotNull
	public MessageManager messageManager() {
		return messageManager;
	}

	@NotNull
	@Deprecated(forRemoval = true)
	public MessageManager getMessageManager() {
		return messageManager;
	}


	public void register(Listener listener) {
		getServer().getPluginManager().registerEvents(listener, this);
	}


	public static AstralDupe getInstance() {
		return instance;
	}


	public AstralPlayer astralPlayer(Player player){
		players.putIfAbsent(player.getUniqueId(), new AstralPlayer());
		return players.get(player.getUniqueId());
	}
	public AstralPlayer astralPlayer(UUID playerId){
		players.putIfAbsent(playerId, new AstralPlayer());
		return players.get(playerId);
	}
}