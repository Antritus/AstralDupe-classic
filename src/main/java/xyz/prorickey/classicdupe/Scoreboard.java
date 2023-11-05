package xyz.prorickey.classicdupe;

import me.antritus.astral.cosmiccapital.api.CosmicCapitalAPI;
import me.antritus.astral.cosmiccapital.api.managers.IAccountManager;
import me.antritus.astral.cosmiccapital.api.types.IAccount;
import me.antritus.astral.factions.api.FactionsAPI;
import me.antritus.astral.factions.api.data.Faction;
import me.antritus.astral.factions.api.data.User;
import me.antritus.astral.factions.api.database.UserDatabase;
import me.antritus.astral.fluffycombat.FluffyCombat;
import me.antritus.astral.fluffycombat.api.CombatTag;
import me.antritus.astral.fluffycombat.api.CombatUser;
import me.antritus.astral.fluffycombat.api.events.CombatEnterEvent;
import me.antritus.astral.fluffycombat.api.events.CombatFullEndEvent;
import me.antritus.astral.fluffycombat.manager.CombatManager;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.utils.*;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;
import xyz.prorickey.classicdupe.database.BountyDatabase;
import xyz.prorickey.classicdupe.database.PlayerData;
import xyz.prorickey.classicdupe.metrics.Metrics;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class Scoreboard implements Listener {

    private static final Map<Player, org.bukkit.scoreboard.Scoreboard> scoreboards = new HashMap<>();
    private static org.bukkit.scoreboard.Scoreboard scoreboard;

    public static class ScoreboardTask extends BukkitRunnable {
        @Override
        public void run() {
            Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                if(!scoreboards.containsKey(player)) {
                    org.bukkit.scoreboard.Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
                    scoreboards.put(player, board);
                    player.setScoreboard(board);
                }
                scoreboard(player, scoreboards.get(player));
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCombatExit(CombatFullEndEvent event){
        Objective objAttacker = scoreboard.getObjective(event.getPlayer().getUniqueId().toString()) == null ?
                scoreboard.registerNewObjective(event.getPlayer().getUniqueId().toString(), Criteria.DUMMY, Component.text("dummy")) :
                scoreboard.getObjective(event.getPlayer().getUniqueId().toString());
        objAttacker.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCombatEnter(CombatEnterEvent event){
        CombatTag tag = event.getCombatTag();
        Player attacker = Bukkit.getPlayer(tag.getAttacker().getUniqueId());
        Player victim = Bukkit.getPlayer(tag.getVictim().getUniqueId());
        Objective objAttacker = scoreboard.getObjective(attacker.getUniqueId().toString()) == null ?
                scoreboard.registerNewObjective(attacker.getUniqueId().toString(), Criteria.DUMMY, Component.text("dummy")) :
                scoreboard.getObjective(attacker.getUniqueId().toString());
        Objective objVictim = scoreboard.getObjective(victim.getUniqueId().toString()) == null ?
                scoreboard.registerNewObjective(victim.getUniqueId().toString(), Criteria.DUMMY, Component.text("dummy")) :
                scoreboard.getObjective(victim.getUniqueId().toString());
        objAttacker.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        objVictim.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
    }

    private static void scoreboard(Player player, org.bukkit.scoreboard.Scoreboard board) {
        Scoreboard.scoreboard = board;
        new BukkitRunnable() {
            /**
             * When an object implementing interface {@code Runnable} is used
             * to create a thread, starting the thread causes the object's
             * {@code run} method to be called in that separately executing
             * thread.
             * <p>
             * The general contract of the method {@code run} is that it may
             * take any action whatsoever.
             *
             * @see Thread#run()
             */
            @Override
            public void run() {

                Objective obj = board.getObjective(player.getUniqueId().toString()) == null ?
                        board.registerNewObjective(player.getUniqueId().toString(), Criteria.DUMMY, Component.text("dummy")) :
                        board.getObjective(player.getUniqueId().toString());
                String displayName = "<color:#8128f7><bold>AstralDupe";
                if(Utils.isVanished(player)) displayName = AstralDupe.astralDupe+" <red><b>(V)";
                if(obj.getDisplaySlot() != DisplaySlot.SIDEBAR) obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                if(!obj.displayName().equals(Utils.format(displayName))) obj.displayName(Utils.format(displayName));

                PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(player.getUniqueId());
                if(data == null) return;

                if (AstralDupe.restartInProgress)
                    updateTeamScore(obj, board, 14, Utils.format("<dark_red><b>SERVER REBOOTING"));
                updateTeamScore(obj, board, 14, ColorUtils.translateComp(""));
                updateTeamScore(obj, board, 13, Utils.format("<gray> | <white>Rank: <gold>"+Utils.getPrefix(player)));
                if (AstralDupe.factionsAPI!= null){
                    FactionsAPI<?> factionsAPI = AstralDupe.factionsAPI;
                    UserDatabase userDatabase = factionsAPI.getUserDatabase();
                    User user = userDatabase.get(player.getUniqueId());
                    Faction faction = user.getFactionInstance();
                    if (faction==null||!faction.exists()) {
                        updateTeamScore(obj, board, 12, Utils.format("<gray> | <white>Clan: <dark_red>/clan"));
                    } else {
                        updateTeamScore(obj, board, 12, Utils.format("<gray> | <white>Clan: <yellow>"+faction.getName()));
                    }
                } else {
                    updateTeamScore(obj, board, 12, Utils.format("<gray> | <white>Clan: <dark_red><b>SOON"));
                }
                String formatPing = PingUtils.getPingFormatMs(player);
                updateTeamScore(obj, board, 11, Utils.format("<gray> | <white>Ping: <green>"+formatPing));

                // Checking if cosmic capital is found.
                // This is set, so we know if it is a beta season and cosmic capital
                // is still in development.
                try {
                    Class.forName("me.antritus.astral.cosmiccapital.CosmicCapital");
                    CosmicCapitalAPI cosmicCapital = AstralDupe.cosmicCapital;
                    IAccountManager accountManager = cosmicCapital.playerManager();
                    IAccount account = accountManager.getKnownNonNull(player.getUniqueId());
                   double balance = account.balance();
                    String  balanceFormat = format(balance);
                    updateTeamScore(obj, board, 10, Utils.format("<gray> | Coins: <aqua>"+balanceFormat));
                } catch (ClassNotFoundException ignore) {
                }

                updateTeamScore(obj, board, 9, ColorUtils.translateComp(" "));

                FluffyCombat fluffyCombat = AstralDupe.fluffyCombat;
                CombatManager combatManager = fluffyCombat.getCombatManager();
                boolean disabledCombat = true;
                if (combatManager.hasTags(player) && !disabledCombat){
                    CombatTag combatTag = combatManager.getLatest(player);
                    assert combatTag != null;
                    CombatUser combatUser = combatTag.getAttacker();
                    if (combatUser.getUniqueId().equals(player.getUniqueId())) {
                        combatUser = combatTag.getVictim();
                    }
                    OfflinePlayer opponent = combatUser.getPlayer();
                    String name = opponent.getName();

                    double health = -1;
                    if (!opponent.isOnline()){
                    } else if (opponent instanceof Player opponentPlayer){
                        health = HealthUtils.getFormattedHealth(opponentPlayer);
                    }
                    String faction = "<dark_red><b>SOON";
                    if (AstralDupe.factionsAPI != null){
                        FactionsAPI<?> factionsAPI = AstralDupe.factionsAPI;
                        UserDatabase userDatabase = factionsAPI.getUserDatabase();
                        User user = userDatabase.get(opponent.getUniqueId());
                        if (user != null){
                            Faction factionInstance = user.getFactionInstance();
                            if (factionInstance==null|| !factionInstance.exists()){
                                faction = "<red_red>NONE";
                            }
                        } else {
                            faction = "<dark_red>INTERNAL ERROR";
                        }
                    }
                    int ticksRemaining = combatTag.getTicksLeft();
                    double seconds = (double) ticksRemaining /20;
                    DecimalFormat decimalFormat = new DecimalFormat(".0");

                    String timeRemaining = decimalFormat.format(seconds);
                    if (seconds<0){
                        timeRemaining = "~0";
                    }

                    updateTeamScore(obj, board, 8, Utils.format("<gray> | <red>Opponent: <dark_red>"+name));
                    updateTeamScore(obj, board, 7, Utils.format("<gray> | <red>Health: <red>"+health));
                    updateTeamScore(obj, board, 6, Utils.format("<gray> | <red>Clan: <yellow>"+faction));
                    updateTeamScore(obj, board, 5, Utils.format("<gray> | <red>Remaining: <green>"+timeRemaining+"s"));
                    BountyDatabase bountyDatabase = ClassicDupe.getDatabase().getBountyDatabase();
                    // Why are bounties not doubles??
                    Integer bounty = bountyDatabase.getBounty(opponent.getUniqueId());
                    if (bounty!=null) {
                        updateTeamScore(obj, board, 4, Utils.format("<gray> | <red>Bounty: <white>"+bounty));
                    }
                } else {
                    double tps = TPSUtils.getTPS();
                    String tpsFormat = TPSUtils.getFormattedTPS(tps);
                    double tpsPercent = TPSUtils.getTPSPercent(tps);
                    int tpsPercent2 = (int) tpsPercent * 100;
                    updateTeamScore(obj, board, 8, Utils.format("<gray> | <white>TPS: <white>" + tpsFormat + " <gray>(" + (tpsPercent2) + "%)"));
                    updateTeamScore(obj, board, 7, Utils.format("<gray> | <white>Online: <aqua>" + PlayerUtils.getVisiblePlayers(player).size()));
                    if (tps < 7) {
                        updateTeamScore(obj, board, 6, Utils.format("<dark_red><b>CONTACT ADMIN!"));
                        updateTeamScore(obj, board, 5, Utils.format("<dark_red><b>TPS IS LOW!"));
                    }
                    updateTeamScore(obj, board, 6, Utils.format("<gray> | <white>Uptime: <yellow>" +
                            Metrics.getServerMetrics().getServerUptimeFormatted()));
                    updateTeamScore(obj, board, 5, Utils.format("<gray> | <white>Beta:<green> true"));
//                    updateTeamScore(obj, board, 5, Utils.format("<gray> | <white>Joins: <white>" + AstralDupe.getInstance().joins()));
                }
                updateTeamScore(obj, board, 2, Utils.format(" "));
                if (AstralDupe.restartInProgress)
                    updateTeamScore(obj, board, 2, Utils.format("<dark_red><b>SERVER REBOOTING"));
                updateTeamScore(obj, board, 1, Utils.format("      <gray>Astral.bet"));
            }
        }.runTaskAsynchronously(AstralDupe.getInstance());
    }

    public static String format(double n) {
        String data = "Se,52|Qi,48|Qd,45|Tq,42|Du,39|Un,36|De,33|No,30|Oc,27|SP,24|SX,21|QT,18|Q,15|T,12|B,9|M,6|k,3";
        String[] dataParts = data.split("\\|");

        for (String part : dataParts) {
            String[] sParts = part.split(",");
            double threshold = Math.pow(10.0, Double.parseDouble(sParts[1]));
            if (n >= threshold) {
                String suffix = sParts[0];
                double formattedNumber = n / Math.pow(10.0, Double.parseDouble(sParts[1]));
                return String.format("%.2f%s", formattedNumber, suffix);
            }
        }

        return String.format("%.2f", n);
    }

    private static void updateTeamScore(Objective obj, org.bukkit.scoreboard.Scoreboard board, int score, Component value) {
        String color = colors.get(score);
        Team line = board.getTeam(color) == null ?
                board.registerNewTeam(color) :
                board.getTeam(color);
        line.addEntry(color);
        line.prefix(value);
        obj.getScore(color).setScore(score);
    }

    private static void updateScore(Objective obj, int score, String name) {
        for (String s : obj.getScoreboard().getEntries().stream().toList()) {
            if(obj.getScore(s).getScore() == score && s.equals(name)) return;
            if(obj.getScore(s).getScore() == score) {
                obj.getScore(s).resetScore();
                obj.getScore(name).setScore(score);
            }
        }
        obj.getScore(name).setScore(score);
    }

    private static final Map<Integer, String> colors = new HashMap<>(){{
        put(16, "\u00A70");
        put(15, "\u00A71");
        put(14, "\u00A72");
        put(13, "\u00A73");
        put(12, "\u00A74");
        put(11, "\u00A75");
        put(10, "\u00A76");
        put(9, "\u00A77");
        put(8, "\u00A78");
        put(7, "\u00A79");
        put(6, "\u00A7a");
        put(5, "\u00A7b");
        put(4, "\u00A7c");
        put(3, "\u00A7d");
        put(2, "\u00A7e");
        put(1, "\u00A7f");
        put(0, "\u00A7r");
    }};
}
