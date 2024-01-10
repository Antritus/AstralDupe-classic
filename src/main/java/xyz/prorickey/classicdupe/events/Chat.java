package xyz.prorickey.classicdupe.events;

import bet.astral.messagemanager.placeholder.LegacyPlaceholder;
import io.papermc.paper.event.player.AsyncChatDecorateEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.entity.AstralPlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.database.PlayerData;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressWarnings("UnstableApiUsage")
public class Chat implements Listener {
    public static Boolean mutedChat = false;
    private static final DecimalFormat decimalFormat = new DecimalFormat(".0");
    private static final PlainTextComponentSerializer plainTextSerializer = PlainTextComponentSerializer.plainText();

    @EventHandler(priority = EventPriority.LOW)
    private void onAsyncChatDecorate(AsyncChatDecorateEvent e){
        MiniMessage mm = MiniMessage.miniMessage();
        String serialized = mm.serialize(e.originalMessage());
        Player player = e.player();

        if (player==null){
            return;
        }
        if(!player.hasPermission("astraldupe.chat.minimessagetags"))
            serialized = mm.stripTags(serialized);
        //}


        // Fuck the old method. New one is better and easier to use in both events
        Component message = mm.deserialize(serialized);
        for (Player pingable : pings(player, message)){
            Pattern pattern = pingPattern(pingable);
            message = message.replaceText(builder->{
                builder.match(pattern).replacement("<yellow>@"+pingable.getName()+"</yellow>");
            });
        }
        e.result(message);
    }
    public static Pattern pingPattern(Player pingable){
        return Pattern.compile("((?i)@"+pingable.getName()+")");
    }
    public static List<Player> pings(Player player, Component message){
        List<Player> pings = new LinkedList<>();
        String serialized = plainTextSerializer.serialize(message);
        for (Player pingable : Bukkit.getOnlinePlayers()) {
            Matcher matcher = pingPattern(pingable).matcher(serialized);
            if (matcher.find()){
                if (Utils.isVanished(player, pingable) && player.hasPermission("astraldupe.chat.pingvanished")) {
                    pings.add(pingable);
                    continue;
                } else if (Utils.isVanished(player, pingable)){
                    continue;
                }
                pings.add(pingable);
            }
        }
        return pings;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onAsyncChat(AsyncChatEvent e) {
        if(!ClassicDupe.getDatabase().getFilterDatabase().checkMessage(PlainTextComponentSerializer.plainText().serialize(e.message()).toLowerCase())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Utils.cmdMsg("<red>Your message has been blocked by the filter"));
            return;
        }
        if(mutedChat && !e.getPlayer().hasPermission("astraldupe.staff.mutechat.bypass")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Utils.cmdMsg("<red>The chat is currently muted"));
            return;
        }

        long now = System.currentTimeMillis();
        AstralDupe astralDupe = AstralDupe.getInstance();
        AstralPlayer astralPlayer = astralDupe.astralPlayer(e.getPlayer());
        if (now<astralPlayer.getChatCooldown()){
            e.setCancelled(true);
            long left = astralPlayer.getChatCooldown()-now;
            double leftD = (double) left /1000;
            String format = decimalFormat.format(leftD);
            astralDupe.messageManager().message(e.getPlayer(), "chat.cooldown",
                    new LegacyPlaceholder("cooldown", format));

            return;
        }

        {
            LuckPerms luckPerms = ClassicDupe.getLuckPerms();
            UserManager userManager = luckPerms.getUserManager();
            net.luckperms.api.model.user.User luckPermsUser = userManager.getUser(e.getPlayer().getUniqueId());
            assert luckPermsUser != null;
            String cooldownString = luckPermsUser.getCachedData().getMetaData().getMetaValue("chat-cooldown");
            int cooldown = 0;
            try {
                cooldown = Integer.parseInt(cooldownString != null ? cooldownString : "");
            } catch (IllegalArgumentException ignore) {
            }
            astralPlayer.setChatCooldown(System.currentTimeMillis() + cooldown);
        }

        MiniMessage mm = MiniMessage.miniMessage();
        Component name;
        PlayerData data = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(e.getPlayer().getUniqueId());
        if(data.nickname != null) {
            name = mm.deserialize(Utils.getPrefix(e.getPlayer()) + data.nickname)
                    .hoverEvent(HoverEvent.showText(Utils.format("<yellow>Real Name: " + e.getPlayer().getName())));
        }
        else name = Utils.format(Utils.getPrefix(e.getPlayer())+ e.getPlayer().getName());


        for (Player player : pings(e.getPlayer(), e.message())){
            PlayerData playerData = ClassicDupe.getDatabase().getPlayerDatabase().getPlayerData(player.getUniqueId());
            if (playerData==null){
                continue;
            }
            if(!playerData.getMutePings())
                player.playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1, 1));
        }

        final Component finalName = name;
        e.renderer((player, sourceDisplayName, message, viewer) ->
                    finalName
                        .append(Utils.format((Utils.getSuffix(player) != null) ? " " + Utils.convertColorCodesToAdventure(Utils.getSuffix(player))  : ""))
                        .append(Utils.format(" <gray>\u00BB <white>"))
                        .append(message)
        );
    }
}
