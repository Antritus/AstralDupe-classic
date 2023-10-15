package xyz.prorickey.classicdupe.commands.moderator;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.ForRemoval;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

@ForRemoval(reason = "Staff commands should go to their own plugin.")
@Deprecated(forRemoval = true)
public class BroadcastCMD extends AstralCommand {

    public BroadcastCMD(AstralDupe astralDupe) {
        super(astralDupe, "broadcast");
        setPermission("astraldupe.staff.broadcast");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        StringBuilder msg = new StringBuilder();
        for (String arg : args) msg.append(arg).append(" ");
        ClassicDupe.getInstance().getServer().getOnlinePlayers().forEach(p -> {
            p.sendMessage(Utils.format("<green>-----------------------------------------------------"));
            p.sendMessage(Utils.format("<yellow>" + Utils.centerText("Announcement")));
            p.sendMessage(Utils.format(msg.toString()));
            p.sendMessage(Utils.format("<green>-----------------------------------------------------"));
        });
        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
