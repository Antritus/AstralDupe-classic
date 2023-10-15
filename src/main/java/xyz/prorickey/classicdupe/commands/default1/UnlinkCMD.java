package xyz.prorickey.classicdupe.commands.default1;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.ForRemoval;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.database.LinkingDatabase;
import xyz.prorickey.classicdupe.discord.ClassicDupeBot;

import java.util.ArrayList;
import java.util.List;

@ForRemoval(reason = "This will be hopefully built inside of v2Discord")
@Deprecated
public class UnlinkCMD extends AstralCommand {

    public UnlinkCMD(AstralDupe astralDupe) {
        super(astralDupe, "unlink");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Utils.cmdMsg("<red>You cannot execute this command from console"));
            return true;
        }

        LinkingDatabase.Link link = ClassicDupe.getDatabase().getLinkingDatabase().getLinkFromUUID(player.getUniqueId().toString());
        if (link == null) {
            player.sendMessage(Utils.cmdMsg("<red>Your account is not linked"));
            return true;
        }
        ClassicDupe.getDatabase().getLinkingDatabase().unlinkByUUID(player.getUniqueId().toString());
        Bukkit.getScheduler().runTaskAsynchronously(ClassicDupe.getInstance(), () -> {
            ClassicDupeBot.getJDA().getGuildById(Config.getConfig().getLong("discord.id")).removeRoleFromMember(
                    ClassicDupeBot.getJDA().retrieveUserById(link.id).complete(),
                    ClassicDupeBot.getJDA().getRoleById(Config.getConfig().getLong("discord.role.link"))
            ).queue();
        });
        player.sendMessage(Utils.cmdMsg("<green>Unlinked your account from " + ClassicDupeBot.getJDA().getUserById(link.id).getName()));
        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}