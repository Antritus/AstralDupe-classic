package xyz.prorickey.classicdupe.discord;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GroupsLink {
	public static boolean isPlayerInGroup(Player player, String group) {
		return player.hasPermission("group." + group);
	}



	static public void grantTemporaryGroup(UUID playerUUID, String group, long durationInSeconds) {
		LuckPerms luckPerms = LuckPermsProvider.get();

		User user = luckPerms.getUserManager().getUser(playerUUID);
		if (user == null) {
			return;
		}

		long currentTime = System.currentTimeMillis();
		long expirationTime = currentTime + durationInSeconds;
		InheritanceNode node = InheritanceNode.builder(group)
				.group(group).expiry(expirationTime, TimeUnit.MILLISECONDS).build();
		user.data().add(node);
		luckPerms.getUserManager().saveUser(user);
	}

	static public void grantGroup(UUID playerUUID, String group) {
		LuckPerms luckPerms = LuckPermsProvider.get();

		User user = luckPerms.getUserManager().getUser(playerUUID);
		if (user == null) {
			return;
		}

		InheritanceNode node = InheritanceNode.builder(group)
				.group(group).build();
		user.data().add(node);
		luckPerms.getUserManager().saveUser(user);
	}

	static public void removeGroupFromPlayer(UUID playerUUID, String group) {
		LuckPerms luckPerms = LuckPermsProvider.get();
		User user = luckPerms.getUserManager().getUser(playerUUID);
		if (user == null) {
			return;
		}

		DataMutateResult groupNode = user.data().remove(Node.builder("group."+group).build());
		if (groupNode.wasSuccessful()) {
			luckPerms.getUserManager().saveUser(user);
		}
	}
}
