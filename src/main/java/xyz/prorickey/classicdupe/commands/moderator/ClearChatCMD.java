package xyz.prorickey.classicdupe.commands.moderator;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.ForRemoval;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;

@ForRemoval(reason = "Staff commands should go to their own plugin.")
@Deprecated(forRemoval = true)
public class ClearChatCMD extends AstralCommand {

    public ClearChatCMD(AstralDupe astralDupe) {
        super(astralDupe, "clearchat");
        setPermission("astraldupe.staff.clearchat");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        ClassicDupe.getInstance().getServer().getOnlinePlayers().forEach(p -> {
            if (!p.hasPermission("astraldupe.staff.clearchat.bypass")) {
                for (int i = 0; i < 300; i++) p.sendMessage(" ");
            }
            p.sendMessage(Utils.cmdMsg("<red><b>The chat has been cleared"));
        });
        return true;
    }
}
