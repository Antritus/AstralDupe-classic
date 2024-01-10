package me.antritus.astraldupe.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AstralPlayer {
	private boolean isStaffChatEnabled = false;
	private boolean isToggleEnabled = true;
	private long chatCooldown = 0;
	private boolean hasKeepInventory = false;
	private int duped;
	private int received;
//	private int receivedDropped;
//	private int receivedInv;



	public void addDuped(int amount){
		duped+=amount;
	}
	public void removeDuped(int amount){
		duped-=amount;
	}
	public void addReceived(int amount){
		received+=amount;
	}
	public void removeReceived(int amount){
		received-=amount;
	}



}
