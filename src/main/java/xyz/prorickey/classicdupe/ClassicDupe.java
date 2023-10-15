package xyz.prorickey.classicdupe;

import com.github.antritus.astral.AdvancedPlugin;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.ForRemoval;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.clans.ClanDatabase;
import xyz.prorickey.classicdupe.commands.admin.*;
import xyz.prorickey.classicdupe.commands.default1.*;
import xyz.prorickey.classicdupe.commands.moderator.*;
import xyz.prorickey.classicdupe.commands.perk.*;
import xyz.prorickey.classicdupe.database.Database;
import xyz.prorickey.classicdupe.database.PlayerVaultDatabase;
import xyz.prorickey.classicdupe.discord.BoosterService;
import xyz.prorickey.classicdupe.discord.ClassicDupeBot;
import xyz.prorickey.classicdupe.events.*;
import xyz.prorickey.classicdupe.metrics.Metrics;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.OverrideOnly
public class ClassicDupe extends AdvancedPlugin {

    public static ClassicDupe plugin;
    public static ClassicDupeBot bot;
    public static Economy economy;
    public static LuckPerms lpapi;
    public static Database database;
    @ForRemoval(reason = "Removing clans fully.")
    @Deprecated(forRemoval = true)
    public static ClanDatabase clanDatabase;
    public static PlayerVaultDatabase pvdatabase;

    public static final List<ItemStack> randomItems = new ArrayList<>();

    public static ClassicDupe getInstance() {
       return plugin;
    }

    @Override
    public void enable() {
        plugin = this;

        try {
            Class.forName("org.h2.Driver");
            Class.forName ("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            plugin.getServer().getPluginManager().disablePlugin(this);
            throw new RuntimeException(e);
        }

        Metrics.init(this);
        RegisteredServiceProvider<LuckPerms> lppro = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if(lppro != null) { lpapi = lppro.getProvider(); }
        //if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) new ClassicDupeExpansion(this).register();
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp != null) { economy = rsp.getProvider(); }


        Config.init(this);
        database = new Database();
        pvdatabase = new PlayerVaultDatabase(this);


        bot = new ClassicDupeBot(this);

        new JoinEvent.JoinEventTasks().runTaskTimer(this, 0, 20);
        new LeaderBoardTask().runTaskTimer(this, 0, 20*60);
        new LinkCMD.LinkCodeTask().runTaskTimer(this, 0, 20);
        new Scoreboard.ScoreboardTask().runTaskTimer(this, 0, 10);

        //new Clans(this);

        for (Material value : Material.values()) randomItems.add(new ItemStack(value));
        for (Enchantment value : Enchantment.values()) {
            for(int i = 1; i < value.getMaxLevel(); i++) {
                ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
                meta.addStoredEnchant(value, i, false);
                book.setItemMeta(meta);
                randomItems.add(book);
            }
        }

        List<Command> commands = new ArrayList<>();
        commands.add(new ConfigReload((AstralDupe) this));
        commands.add(new FilterCMD((AstralDupe) this));
        commands.add(new GamemodeCMD((AstralDupe) this));
        commands.add(new GmaCMD((AstralDupe) this));
        commands.add(new GmcCMD((AstralDupe) this));
        commands.add(new GmspCMD((AstralDupe) this));
        commands.add(new GmsCMD((AstralDupe) this));
        commands.add(new HeadCMD((AstralDupe) this));
        commands.add(new InvseeCMD((AstralDupe) this));
        commands.add(new PvAddCMD((AstralDupe) this));
        commands.add(new PvSeeCMD((AstralDupe) this));
        commands.add(new ScheduleRestartCMD((AstralDupe) this));
        commands.add(new SetSpawnCMD((AstralDupe) this));
        commands.add(new SudoCMD((AstralDupe) this));
        commands.add(new DeathMessagesCMD((AstralDupe) this));
        commands.add(new DelHomeCMD((AstralDupe) this));
        commands.add(new DiscordCMD((AstralDupe) this));
        commands.add(new HelpCMD((AstralDupe) this));
        commands.add(new HomeCMD((AstralDupe) this));
        commands.add(new LinkCMD((AstralDupe) this));
        commands.add(new MutePingsCMD((AstralDupe) this));
        commands.add(new NakedOffCMD((AstralDupe) this));
        commands.add(new NightVisionCMD((AstralDupe) this));
        commands.add(new PrivateMessageCMD((AstralDupe) this));
        commands.add(new PrivateMessageReplyCMD((AstralDupe) this));
        commands.add(new RandomCMD((AstralDupe) this));
        commands.add(new ReportCMD((AstralDupe) this));
        commands.add(new RulesCMD((AstralDupe) this));
        commands.add(new SetHomeCMD((AstralDupe) this));
        commands.add(new SpawnCMD((AstralDupe) this));
        commands.add(new StatsCMD((AstralDupe) this));
        commands.add(new TrashCMD((AstralDupe) this));
        commands.add(new UnlinkCMD((AstralDupe) this));
        commands.add(new WorldsizeCMD((AstralDupe) this));
        commands.add(new BroadcastCMD((AstralDupe) this));
        commands.add(new ClearChatCMD((AstralDupe) this));
        commands.add(new CspyCMD((AstralDupe) this));
        commands.add(new MutechatCMD((AstralDupe) this));
        commands.add(new SpecCMD((AstralDupe) this));
        commands.add(new StaffChatCMD((AstralDupe) this));
        commands.add(new StaffTeleportCMD((AstralDupe) this));
        commands.add(new ChatColorCMD((AstralDupe) this));
        commands.add(new ChatGradientCMD((AstralDupe) this));
        commands.add(new CraftCMD((AstralDupe) this));
        commands.add(new EnderChestCMD((AstralDupe) this));
        commands.add(new FeedCMD((AstralDupe) this));
        commands.add(new HatCMD((AstralDupe) this));
        commands.add(new KillEffectsCMD((AstralDupe) this));
        commands.add(new NicknameCMD((AstralDupe) this));
        commands.add(new PlayerVaultCMD((AstralDupe) this));
        commands.add(new RenameCMD((AstralDupe) this));
        commands.add(new RepairCMD((AstralDupe) this));
        commands.add(new SuffixCMD((AstralDupe) this));

        CommandMap commandMap = getServer().getCommandMap();
        commandMap.registerAll("astraldupe", commands);

        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        getServer().getPluginManager().registerEvents(new QuitEvent(), this);
        getServer().getPluginManager().registerEvents(new Chat(), this);
        getServer().getPluginManager().registerEvents(new BlockPlace(), this);
        getServer().getPluginManager().registerEvents(new VoidTeleport(), this);
        getServer().getPluginManager().registerEvents(new DeathEvent(), this);
        getServer().getPluginManager().registerEvents(new EntitySpawnEvent(), this);
        getServer().getPluginManager().registerEvents(new FlowEvent(), this);
        getServer().getPluginManager().registerEvents(new GoldenAppleCooldown(), this);
        getServer().getPluginManager().registerEvents(new PearlCooldown(), this);
        getServer().getPluginManager().registerEvents(new CSPY(), this);
        getServer().getPluginManager().registerEvents(new BoosterService(), this);

        // Checking if cosmic capital is found.
        // This is set, so we know if it is a beta season and cosmic capital
        // is still in development.
        try {
            Class.forName("me.antritus.astral.cosmiccapital.CosmicCapital");
            ShopCMD.reloadShop();
            commands.add(new ShopCMD((AstralDupe) this));
            commands.add(new BountyCMD((AstralDupe) this));
        } catch (ClassNotFoundException ignore) {
        }


        //ticker

    }
    @Override
    public void updateConfig(@Nullable String s, String s1) {

    }

    @Override
    public void startDisable() {
        ClassicDupeBot.jda.shutdown();
    }

    @Override
    public void disable() {
    }

    public static void executeConsoleCommand(String cmd) {
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
    }

    private enum LastBroadcast {
        DISCORD,
        STORE
    }

    private static LastBroadcast lastBroadcast;

    private static class LeaderBoardTask extends BukkitRunnable {
        @Override
        public void run() {
            database.getPlayerDatabase().reloadLeaderboards();
        }
    }

    public static LuckPerms getLuckPerms() { return lpapi; }
    public static Database getDatabase() { return database; }
    public static PlayerVaultDatabase getPVDatabase() { return pvdatabase; }
    public static ClanDatabase getClanDatabase() { return clanDatabase; }

    public static List<String> getOnlinePlayerUsernames() {
        List<String> list = new ArrayList<>();
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            if(!Utils.isVanished(player)) list.add(player.getName());
        });
        return list;
    }

    public static List<String> getOnlinePlayerUsernames(Player player) {
        List<String> list = new ArrayList<>();
        if(player.hasPermission("astraldupe.vanish.see")) plugin.getServer().getOnlinePlayers().forEach(playe -> {
            list.add(playe.getName());
        });
        else plugin.getServer().getOnlinePlayers().forEach(playe -> {
            if(!Utils.isVanished(playe)) list.add(playe.getName());
        });
        return list;
    }

    public static void rawBroadcast(String text) {
        plugin.getServer().getOnlinePlayers().forEach(player -> player.sendMessage(Utils.format(text)));
    }

    public static Boolean scheduledRestartCanceled = false;
    public static Boolean restartInProgress = false;

    public static void scheduleRestart() {
        scheduledRestartCanceled = false;
        restartInProgress = true;
        rawBroadcast("<red><b>The server will restart in 60 seconds.");
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (scheduledRestartCanceled) return;
            rawBroadcast("<red><b>The server will restart in 30 seconds.");
        }, 20L * 30L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (scheduledRestartCanceled) return;
            rawBroadcast("<red><b>The server will restart in 10 seconds.");
        }, 20L * 50L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (scheduledRestartCanceled) return;
            rawBroadcast("<red><b>The server will restart in 5 seconds.");
        }, 20L * 55L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (scheduledRestartCanceled) return;
            restartInProgress = false;
            plugin.getServer().shutdown();
        }, 20L * 60L);
    }

}
