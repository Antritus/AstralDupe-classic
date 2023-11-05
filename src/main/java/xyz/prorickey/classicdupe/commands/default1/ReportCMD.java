package xyz.prorickey.classicdupe.commands.default1;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.utils.ColorUtils;
import me.antritus.astraldupe.utils.PlayerUtils;
import me.antritus.astraldupe.commands.AstralCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.discord.ClassicDupeBot;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class ReportCMD extends AstralCommand {

    private static List<String> reasons = List.of("cheating", "chat", "other");

    public ReportCMD(AstralDupe astralDupe) {
        super(astralDupe, "report");
        setAliases(List.of("helpop", "apuaapuaolenpulassa!"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(Utils.cmdMsg("<red>You must enter the name of a player who you would like to report"));
            return true;
        }
        Player rep = Bukkit.getPlayer(args[0]);
        if (rep == null) {
            player.sendMessage(Utils.cmdMsg("<red>That player has not joined the server before"));
            return true;
        }
        if (args.length == 1) {
            player.sendMessage(Utils.cmdMsg("<red>You must enter a reason for reporting <yellow>" + rep.getName()));
            return true;
        }
        if (!reasons.contains(args[1].toLowerCase())) args[1] = "other";
        String reason;
        if (args.length > 2) {
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                sb.append(args[i]).append(" ");
            }
            reason = sb.toString();
        } else {
            reason = "No reason provided";
        }

        player.sendMessage(Utils.cmdMsg("<green>Thank you for submitting a report, staff will look into it now"));

        Bukkit.getOnlinePlayers().stream().filter(p->p.hasPermission("astraldupe.staff.chat")).forEach(p->p.sendMessage(ColorUtils.translateComp("<dark_gray>[<red>REPORT<dark_gray>] <yellow>" +
                        player.getName() +
                        " <green>reported <yellow>" +
                        rep.getName() +
                        "<green> for <yellow>" +
                        args[1])
                .appendNewline()
                .append(Utils.format("<green>Reason: <yellow>" + reason))));

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Report Submitted!");
        eb.addField("Reported By", player.getName(), true);
        eb.addField("Reported", rep.getName(), true);
        switch (args[1]) {
            case "cheating" -> {
                eb.setColor(0xE81A1A);
                eb.addField("Reason", "Cheating - " + reason, true);
            }
            case "chat" -> {
                eb.setColor(0xE8AE1A);
                eb.addField("Reason", "Chat - " + reason, true);
            }
            case "other" -> {
                eb.setColor(0x1ABFE8);
                eb.addField("Reason", "Other - " + reason, true);
            }
        }
        eb.setTimestamp(Instant.now());

        ClassicDupeBot.getJDA().getTextChannelById(Config.getConfig().getLong("discord.reports"))
                .sendMessageEmbeds(eb.build())
                .queue();

        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return PlayerUtils.getVisiblePlayerNames(sender);
        if (args.length == 2) return reasons;
        return Collections.emptyList();
    }
}
