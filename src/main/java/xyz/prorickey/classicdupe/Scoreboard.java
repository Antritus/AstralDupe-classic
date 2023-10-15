package xyz.prorickey.classicdupe;

import com.github.antritus.astral.utils.ColorUtils;
import me.antritus.astral.cosmiccapital.CosmicCapital;
import me.antritus.astral.cosmiccapital.api.PlayerAccount;
import me.antritus.astral.cosmiccapital.database.PlayerAccountDatabase;
import me.antritus.astral.factions.api.FactionsAPI;
import me.antritus.astral.factions.api.data.Faction;
import me.antritus.astral.factions.api.data.User;
import me.antritus.astral.factions.api.database.UserDatabase;
import me.antritus.astral.fluffycombat.FluffyCombat;
import me.antritus.astral.fluffycombat.api.CombatTag;
import me.antritus.astral.fluffycombat.api.CombatUser;
import me.antritus.astral.fluffycombat.manager.CombatManager;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.HealthUtils;
import me.antritus.astraldupe.utils.PingUtils;
import me.antritus.astraldupe.utils.PlayerUtils;
import me.antritus.astraldupe.utils.TPSUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
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

public class Scoreboard {

    private static final Map<Player, org.bukkit.scoreboard.Scoreboard> scoreboards = new HashMap<>();

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

    private static void scoreboard(Player player, org.bukkit.scoreboard.Scoreboard board) {
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
                if(Utils.isVanished(player)) displayName = "<color:751ac9><bold>AstralDupe <red><b>(V)";
                if(obj.getDisplaySlot() != DisplaySlot.SIDEBAR) obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                if(!obj.displayName().equals(Utils.format(displayName))) obj.displayName(Utils.format(displayName));

                PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(player.getUniqueId());
                if(data == null) return;

                if (AstralDupe.restartInProgress)
                    updateTeamScore(obj, board, 15, Utils.format("<dark_red><b>SERVER REBOOTING"));
                updateTeamScore(obj, board, 14, ColorUtils.translateComp("<color:#7300de><b>Stats <italic>("+player.getName()+")"));
                updateTeamScore(obj, board, 13, Utils.format("<color:#751ac9>  Rank: <white>"+Utils.getPrefix(player)));
                if (AstralDupe.factionsAPI!= null){
                    FactionsAPI<?> factionsAPI = AstralDupe.factionsAPI;
                    UserDatabase userDatabase = factionsAPI.getUserDatabase();
                    User user = userDatabase.get(player.getUniqueId());
                    Faction faction = user.getFactionInstance();
                    if (faction==null||!faction.exists()) {
                        updateTeamScore(obj, board, 12, Utils.format("<color:#751ac9>  Clan: <dark_red>/clan"));
                    } else {
                        updateTeamScore(obj, board, 12, Utils.format("<color:#751ac9>  Clan: <white>"+faction.getName()));
                    }
                } else {
                    updateTeamScore(obj, board, 12, Utils.format("<color:#751ac9>  Clan: <dark_red><b>TODO"));
                }
                String formatPing = PingUtils.getPingFormatMs(player);
                updateTeamScore(obj, board, 11, Utils.format("<color:#751ac9>  Ping: <white>"+formatPing));

                // Checking if cosmic capital is found.
                // This is set, so we know if it is a beta season and cosmic capital
                // is still in development.
                try {
                    Class.forName("me.antritus.astral.cosmiccapital.CosmicCapital");

                    CosmicCapital cosmicCapital = AstralDupe.cosmicCapital;
                    PlayerAccountDatabase accountDatabase = cosmicCapital.getPlayerDatabase();
                    PlayerAccount account = accountDatabase.get(player.getUniqueId());
                    double balance = account.getBalance();
                    String  balanceFormat = format(balance);
                    updateTeamScore(obj, board, 10, Utils.format("<color:#751ac9>  Coins: <white>"+balanceFormat));
                } catch (ClassNotFoundException ignore) {
                }


                FluffyCombat fluffyCombat = AstralDupe.fluffyCombat;
                CombatManager combatManager = fluffyCombat.getCombatManager();
                if (combatManager.hasTags(player)){
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
                    String faction = "<dark_red><b>TODO";
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

                    updateTeamScore(obj, board, 9, ColorUtils.translateComp("<color:#ed2828>><b>Combat"));
                    updateTeamScore(obj, board, 8, Utils.format("<color:#ff3636>  Opponent: <white>"+name));
                    updateTeamScore(obj, board, 7, Utils.format("<color:#ff3636>  Health: <white>"+health));
                    updateTeamScore(obj, board, 6, Utils.format("<color:#ff3636>  Clan: <white>"+faction));
                    updateTeamScore(obj, board, 5, Utils.format("<color:#ff3636>  Remaining: <white>"+timeRemaining+"s"));
                    BountyDatabase bountyDatabase = ClassicDupe.getDatabase().getBountyDatabase();
                    // Why are bounties not doubles??
                    Integer bounty = bountyDatabase.getBounty(opponent.getUniqueId());
                    if (bounty!=null) {
                        updateTeamScore(obj, board, 4, Utils.format("<color:#55eded>  Bounty: <white>"+bounty));
                    }
                } else {
                    updateTeamScore(obj, board, 9, ColorUtils.translateComp("<color:#7300de><b>Server"));
                    double tps = TPSUtils.getTPS();
                    String tpsFormat = TPSUtils.getFormattedTPS(tps);
                    double tpsPercent = TPSUtils.getTPSPercent(tps);
                    int tpsPercent2 = (int) tpsPercent*100;
                    updateTeamScore(obj, board, 8, Utils.format("<color:#751ac9>  TPS: <white>"+tpsFormat +" <gray>("+(tpsPercent2)+"%)"));
                    updateTeamScore(obj, board, 7, Utils.format("<color:#751ac9>  Online: <white>"+ PlayerUtils.getVisiblePlayers(player).size()));
                    updateTeamScore(obj, board, 6, Utils.format("<color:#751ac9>  Season Joins: <white>"+0));
                    if (tps<7){
                        updateTeamScore(obj, board, 7, Utils.format("<dark_red><b>CONTACT ADMIN!"));
                        updateTeamScore(obj, board, 6, Utils.format("<dark_red><b>TPS IS LOW!"));
                    }
                    updateTeamScore(obj, board, 3, Utils.format("<color:#751ac9>  Uptime: <white>"+
                            Metrics.getServerMetrics().getServerUptimeFormatted()));
                    updateTeamScore(obj, board, 4, Utils.format("<color:#751ac9>  Total Joins: <white>"+0));
                }
                if (AstralDupe.restartInProgress)
                    updateTeamScore(obj, board, 2, Utils.format("<dark_red><b>SERVER REBOOTING"));
                updateTeamScore(obj, board, 1, Utils.format("   <gray>AstralDupe.minehut.gg"));
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
