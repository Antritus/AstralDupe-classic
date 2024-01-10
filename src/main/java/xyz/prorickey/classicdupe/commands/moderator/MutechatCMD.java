package xyz.prorickey.classicdupe.commands.moderator;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.ForRemoval;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.events.Chat;

import java.util.ArrayList;
import java.util.List;

@ForRemoval(reason = "Staff commands should go to their own plugin.")
@Deprecated(forRemoval = true)
public class MutechatCMD extends AstralCommand {

    public MutechatCMD(AstralDupe astralDupe) {
        super(astralDupe, "mutechat");
        setAliases(List.of("mchat"));
        setPermission("astraldupe.staff.mutechat");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (Chat.mutedChat) {
            Chat.mutedChat = false;
            ClassicDupe.rawBroadcast("<red>The chat has been unmuted");
        } else {
            Chat.mutedChat = true;
            ClassicDupe.rawBroadcast("<red>The chat has been muted");
        }
        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
