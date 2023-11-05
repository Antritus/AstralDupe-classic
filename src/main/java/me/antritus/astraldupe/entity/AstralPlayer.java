package me.antritus.astraldupe.entity;

import lombok.Getter;
import lombok.Setter;

public class AstralPlayer {
	@Getter
	@Setter
	private boolean isStaffChatEnabled = false;
	@Getter
	@Setter
	private boolean isToggleEnabled = true;

	@Setter
	@Getter
	private long chatCooldown = 0;
}
