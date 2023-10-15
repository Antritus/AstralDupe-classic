package xyz.prorickey.classicdupe.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Config;

public class ExecuteDCMD {

    public static void execute(SlashCommandInteractionEvent inter) {
        if(!inter.getMember().getRoles().contains(inter.getGuild().getRoleById(Config.getConfig().getLong("discord.role.system")))) {
            inter.reply("You cannot execute this command").setEphemeral(true).queue();
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                ClassicDupe.executeConsoleCommand(inter.getOption("command", OptionMapping::getAsString));
            }
        }.runTask(ClassicDupe.getInstance());
        inter.reply("Executed the command `" + inter.getOption("command", OptionMapping::getAsString) + "`").setEphemeral(true).queue();
    }

}
