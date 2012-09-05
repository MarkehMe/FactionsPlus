package markehme.factionsplus.listeners;

import markehme.factionsplus.Utilities;
import markehme.factionsplus.config.Config;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.massivecraft.factions.event.FPlayerJoinEvent;

public class PeacefulListener implements Listener{
	@EventHandler
	public void onFPlayerJoinEvent(FPlayerJoinEvent event) {
		if(event.isCancelled()) {
			return;
		}
		int boostValue = Config._peaceful.powerBoostIfPeaceful._ ;
		if(boostValue> 0) {
			if(Utilities.isPeaceful( event.getFaction() )) { // done: Prepare for 1.7.x and the removal of isPeaceful()
				Utilities.addPower(event.getFPlayer(),boostValue);
			}
		}
	}

}
