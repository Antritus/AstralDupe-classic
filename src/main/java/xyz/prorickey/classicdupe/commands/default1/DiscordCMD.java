package xyz.prorickey.classicdupe.commands.default1;

import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.commands.AstralCommand;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;

import java.util.ArrayList;
import java.util.List;

public class DiscordCMD extends AstralCommand {

    public DiscordCMD(AstralDupe astralDupe) {
        super(astralDupe, "discord");
    }

    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        sender.sendMessage(Utils.format("<green>You can join our discord at <aqua>"+ Config.getConfig().getString("discord.link", "discord.com"))
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, Config.getConfig().getString("discord.link", "discord.com"))));
        return true;
    }

    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
