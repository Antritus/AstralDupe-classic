package me.antritus.astraldupe;


import com.github.antritus.astral.messages.MessageManager;
import me.antritus.astral.cosmiccapital.api.CosmicCapitalAPI;
import me.antritus.astral.cosmiccapital.api.providers.EconomyProvider;
import me.antritus.astral.factions.api.FactionsAPI;
import me.antritus.astral.factions.api.FactionsAPIProvider;
import me.antritus.astral.fluffycombat.FluffyCombat;
import me.antritus.astraldupe.commands.DupeCommand;
import me.antritus.astraldupe.commands.StoreAnnouncementCommand;
import me.antritus.astraldupe.commands.SuffixCommand;
import me.antritus.astraldupe.discord_loggers.ChatLogger;
import me.antritus.astraldupe.discord_loggers.MessageLogger;
import me.antritus.astraldupe.listeners.BountyClaimListener;
import me.antritus.astraldupe.listeners.ChatItemListener;
import me.antritus.astraldupe.listeners.GiveKillMoneyListener;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.ClassicDupe;

import java.util.ArrayList;
import java.util.List;

public class AstralDupe extends ClassicDupe {
	public static String season = "beta";
	public static String seasonFormat = "<dark_aqua>B<aqua>E<dark_aqua>T<aqua>A<reset>";
	public static AstralDupeEconomy economy;
	public static MessageLogger messageLogger;
	public static ChatLogger chatLogger;
	private static AstralDupe instance;
	public static FactionsAPI<?> factionsAPI;
	public static CosmicCapitalAPI cosmicCapital;
	public static FluffyCombat fluffyCombat;
	public static List<Material> illegalDupes = new ArrayList<>();
	private MessageManager messageManager;

	@Override
	public void onEnable() {
		super.onEnable();
		instance = this;
		messageManager = new MessageManager(this);
		try {
			Class.forName("me.antritus.astral.factions.api.FactionsAPI");
			factionsAPI = FactionsAPIProvider.get();
		} catch (ClassNotFoundException ignored){
		}

		if (getServer().getPluginManager().getPlugin("CosmicCapital") != null) {
			cosmicCapital = EconomyProvider.getAPI();
			economy = new AstralDupeEconomy(this);
			register(new BountyClaimListener());
			register(new GiveKillMoneyListener());
		}

		if (getServer().getPluginManager().getPlugin("Fluffy") != null)
			fluffyCombat = FluffyCombat.getPlugin(FluffyCombat.class);
		if (fluffyCombat == null){
			this.getServer().getPluginManager().disablePlugin(this);
			getLogger().severe("Couldn't find Fluffy(Combat) in the plugins! Disabling plugin!");
			return;
		}

		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
			PlaceholderAPIPlugin.getInstance().getLocalExpansionManager().register(AstralDupeExpansion.class);


		List<String> forbiddenDupes = getConfig().getStringList("forbiddenDupes");
		forbiddenDupes.forEach(item->{
			try {
				Material material = Material.valueOf(item);
				illegalDupes.add(material);
			} catch (IllegalArgumentException ignore){}
		});

		messageLogger = new MessageLogger("message");
		chatLogger = new ChatLogger("chat");
		register(chatLogger);

		register(new ChatItemListener());

		CommandMap commandMap = getServer().getCommandMap();
		commandMap.register("astraldupe", new DupeCommand(this));
		commandMap.register("astraldupe", new SuffixCommand(this));
		commandMap.register("astraldupe", new StoreAnnouncementCommand(this));
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


	public void register(Listener listener){
		getServer().getPluginManager().registerEvents(listener, this);
	}


	public static AstralDupe getInstance(){
		return instance;
	}

}
