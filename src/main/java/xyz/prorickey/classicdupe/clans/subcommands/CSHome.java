package xyz.prorickey.classicdupe.clans.subcommands;

import me.antritus.astral.fluffycombat.FluffyCombat;
import me.antritus.astral.fluffycombat.manager.CombatManager;
import me.antritus.astraldupe.AstralDupe;
import me.antritus.astraldupe.ForRemoval;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.prorickey.classicdupe.ClassicDupe;
import xyz.prorickey.classicdupe.Utils;
import xyz.prorickey.classicdupe.clans.builders.Clan;
import xyz.prorickey.classicdupe.clans.builders.ClanMember;
import xyz.prorickey.classicdupe.clans.builders.ClanSub;
import xyz.prorickey.classicdupe.clans.builders.Warp;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@ForRemoval(reason = "Clans will be removed fully from the classic dupe plugin.")
@Deprecated(forRemoval = true)
public class CSHome extends ClanSub {
    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Utils.format("<red>You cannot execute this command from console"));
            return;
        }
        FluffyCombat fluffyCombat = AstralDupe.fluffyCombat;
        CombatManager combatManager = fluffyCombat.getCombatManager();
        if (combatManager.hasTags(player)){
            player.sendMessage(Utils.cmdMsg("<red>You cannot execute this command in combat"));
            return;
        }
        ClanMember cmem = ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId());
        if(cmem.getClanID() == null) {
            player.sendMessage(Utils.cmdMsg("<red>You can't teleport to any homes if you are not in a clan"));
            return;
        }
        Clan clan = ClassicDupe.getClanDatabase().getClan(cmem.getClanID());
        if(args.length == 0) {
            if(!clan.getWarpNames().contains("default")) {
                player.sendMessage(Utils.cmdMsg("<red>That home does not exist"));
                return;
            }
            Warp warp = clan.getWarp("default");
            player.teleport(warp.location);
            player.sendMessage(Utils.cmdMsg("<yellowYou have been teleported to your clans default home"));
        } else {
            if(!clan.getWarpNames().contains(args[0].toLowerCase())) {
                player.sendMessage(Utils.cmdMsg("<red>That home does not exist"));
                return;
            }
            Warp warp = clan.getWarp(args[0].toLowerCase());
            player.teleport(warp.location);
            player.sendMessage(Utils.cmdMsg("<yellowYou have been teleported to <gold>" + args[0].toLowerCase()));
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player) || ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId()).getClanID() == null) return new ArrayList<>();
        ClanMember cmem = ClassicDupe.getClanDatabase().getClanMember(player.getUniqueId());
        if(args.length == 1) return (ClassicDupe.getClanDatabase().getClan(cmem.getClanID()).getWarpNames());
        return new ArrayList<>();
    }
}
