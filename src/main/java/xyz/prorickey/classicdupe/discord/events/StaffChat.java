package xyz.prorickey.classicdupe.discord.events;

import bet.astral.messagemanager.placeholder.LegacyPlaceholder;
import bet.astral.messagemanager.placeholder.Placeholder;
import me.antritus.astraldupe.AstralDupe;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Config;


public class StaffChat extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(Config.getConfig().getLong("discord.staffchat") != event.getChannel().getIdLong() || event.getAuthor().isBot()) return;
        AstralDupe astralDupe = AstralDupe.getInstance();
        Placeholder[] placeholders = new Placeholder[]{
                new LegacyPlaceholder("who", event.getAuthor().getName()),
                new LegacyPlaceholder("message", event.getMessage().getContentRaw())};
        Bukkit.getOnlinePlayers().stream().filter(p->p.hasPermission("astraldupe.staff.chat")).forEach(player->{
            astralDupe.messageManager().message(player, "staff-chat.format", placeholders);
        });
    }
}
