package xyz.prorickey.classicdupe.commands.admin;

import com.github.antritus.astral.AdvancedPlugin;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.commands.AstralCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;

import java.util.Collections;
import java.util.List;

public class ConfigReload extends AstralCommand {
    public ConfigReload(AstralDupe main) {
        super(main, "astraldupe-reload");
        setPermission("astraldupe.admin.reload");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        Config.reloadConfig();
        sender.sendMessage(Utils.cmdMsg("<green>The config has been reloaded"));
        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
