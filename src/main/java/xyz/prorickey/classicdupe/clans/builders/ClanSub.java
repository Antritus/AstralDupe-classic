package xyz.prorickey.classicdupe.clans.builders;

import me.antritus.astraldupe.ForRemoval;
import org.bukkit.command.CommandSender;

import java.util.List;

@ForRemoval(reason = "Clans will be removed fully from the classic dupe plugin.")
@Deprecated(forRemoval = true)
public abstract class ClanSub {

    public ClanSub() {
    }

    public abstract void execute(CommandSender sender, String[] args);
    public abstract List<String> tabComplete(CommandSender sender, String[] args);

}
