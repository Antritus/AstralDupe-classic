package xyz.prorickey.classicdupe.discord.events;

import me.antritus.astraldupe.AstralDupe;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.classicdupe.Config;

import static me.antritus.astraldupe.MessageManager.placeholder;

public class StaffChat extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(Config.getConfig().getLong("discord.staffchat") != event.getChannel().getIdLong() || event.getAuthor().isBot()) return;
        AstralDupe astralDupe = AstralDupe.getInstance();
        String[] placeholders = new String[]{
                placeholder("who", event.getAuthor().getName()),
                placeholder("message", event.getMessage().getContentRaw())};
        Bukkit.getOnlinePlayers().stream().filter(p->p.hasPermission("astraldupe.staff.chat")).forEach(player->{
            astralDupe.messageManager().message(player, "staff-chat.format", placeholders);
        });
    }
}
