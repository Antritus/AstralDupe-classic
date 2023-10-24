package xyz.prorickey.classicdupe.events;

import io.papermc.paper.event.player.AsyncChatDecorateEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.antritus.astral.factions.api.FactionsAPI;
import me.antritus.astral.factions.api.FactionsAPIProvider;
import me.antritus.astral.factions.api.data.Faction;
import me.antritus.astral.factions.api.data.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Config;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.commands.moderator.StaffChatCMD;
import xyz.prorickey.classicdupe.database.PlayerData;
import xyz.prorickey.classicdupe.discord.ClassicDupeBot;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class Chat implements Listener {

    public static Boolean mutedChat = false;

    public static final Map<Player, Long> chatCooldown = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    public void onAsyncChatDecorate(AsyncChatDecorateEvent e){
        MiniMessage mm = MiniMessage.miniMessage();
        String serialized = mm.serialize(e.originalMessage());
        Player player = e.player();

        if (player==null){
            return;
        }
        if(!player.hasPermission("astraldupe.chat.minimessagetags"))
            serialized = mm.stripTags(serialized);
        //}
        PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(e.player().getUniqueId());
        
        // Checking all players in the message
        // Old for loop due to streaming needs final variables.
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(serialized.contains("@" + onlinePlayer.getName())) {
                if (Utils.isVanished(onlinePlayer)){
                    if (!player.hasPermission("astraldupe.chat.pingvanished")){
                        continue;
                    }
                }
                serialized = serialized.replace("@" + onlinePlayer.getName(), "<yellow>@"+onlinePlayer.getName()+"</yellow>");
                PlayerData playerData = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(onlinePlayer.getUniqueId());
                if (playerData==null){
                    continue;
                }
                if(!playerData.getMutePings())
                    onlinePlayer.playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1, 1));
            }
        }
        e.result(mm.deserialize(serialized));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAsyncChat(AsyncChatEvent e) {
        if(!ClassicDupe.getDatabase().getFilterDatabase().checkMessage(PlainTextComponentSerializer.plainText().serialize(e.message()).toLowerCase())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Utils.cmdMsg("<red>Your message has been blocked by the filter"));
            return;
        }
        if(mutedChat && !e.getPlayer().hasPermission("astraldupe.mutechat.bypass")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Utils.cmdMsg("<red>The chat is currently muted"));
            return;
        }
        if(chatCooldown.containsKey(e.getPlayer()) && chatCooldown.get(e.getPlayer()) > System.currentTimeMillis()) {
            e.setCancelled(true);
            long timeLeft = chatCooldown.get(e.getPlayer())-System.currentTimeMillis();
            e.getPlayer().sendMessage(Utils.cmdMsg("<red>You are currently on chat cooldown for " + Math.round(timeLeft/1000.0) + " second(s)"));
            return;
        }
        if(StaffChatCMD.staffChatPlayers.contains(e.getPlayer())) {
            e.setCancelled(true);
            StaffChatCMD.sendToStaffChat(
                    Utils.format("<aqua><b>STAFF</b> ")
                            .append(MiniMessage.miniMessage().deserialize(((Utils.getPrefix(e.getPlayer()) != null) ? Utils.getPrefix(e.getPlayer()) : "") + e.getPlayer().getName()))
                            .append(Utils.format(" <gray>\u00BB "))
                            .append(e.message().color(TextColor.color(0x10F60E))));
            ClassicDupeBot.getJDA().getChannelById(TextChannel.class, Config.getConfig().getLong("discord.staffchat"))
                    .sendMessage("**" + e.getPlayer().getName() + "** \u00BB " + PlainTextComponentSerializer.plainText().serialize(e.message())).queue();
            return;
        }


        String clanName = null;
        String clanColor = "<yellow>";
        try {
            Class.forName("me.antritus.astral.factions.api.data.Faction");
            FactionsAPI<?> factionsAPI = FactionsAPIProvider.get();
            User user = factionsAPI.getUserDatabase().getKnownNonNull(e.getPlayer().getUniqueId());
            Faction faction = user.getFactionInstance();
            if (faction != null){
                clanName = faction.getName();
                //clanName = faction.getColor();
            }
        } catch (ClassNotFoundException ex) {
        }

        String pgroup = ClassicDupe.getLuckPerms().getUserManager().getUser(e.getPlayer().getUniqueId()).getPrimaryGroup();
        if(pgroup.equalsIgnoreCase("default")) chatCooldown.put(e.getPlayer(), System.currentTimeMillis()+1000);
        else if(pgroup.equalsIgnoreCase("vip")) chatCooldown.put(e.getPlayer(), System.currentTimeMillis()+500);
        else if(pgroup.equalsIgnoreCase("mvp")) chatCooldown.put(e.getPlayer(), System.currentTimeMillis()+500);
        else if(pgroup.equalsIgnoreCase("legend")) chatCooldown.put(e.getPlayer(), System.currentTimeMillis()+500);

        MiniMessage mm = MiniMessage.miniMessage();
        Component name;
        PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(e.getPlayer().getUniqueId());
        if(data.nickname != null) {
            name = mm.deserialize(Utils.getPrefix(e.getPlayer()) + data.nickname)
                    .hoverEvent(HoverEvent.showText(Utils.format("<yellow>Real Name: " + e.getPlayer().getName())));
        }
        else name = Utils.format(Utils.getPrefix(e.getPlayer())+ e.getPlayer().getName());

        Component finalName = name;
        String finalClanName = clanName;
        e.renderer((player, sourceDisplayName, message, viewer) ->
                Utils.format((finalClanName != null ? "<dark_gray>[" + clanColor + finalClanName + "<dark_gray>] " : ""))
                        .append(finalName)
                        .append(Utils.format((Utils.getSuffix(player) != null) ? " " + Utils.convertColorCodesToAdventure(Utils.getSuffix(player))  : ""))
                        .append(Utils.format(" <gray>\u00BB <white>"))
                        .append(message)
        );
    }
}
