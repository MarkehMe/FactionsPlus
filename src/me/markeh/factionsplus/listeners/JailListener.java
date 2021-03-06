package me.markeh.factionsplus.listeners;

import me.markeh.factionsframework.faction.Factions;
import me.markeh.factionsframework.factionsmanager.FactionsManager;
import me.markeh.factionsplus.conf.FactionData;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class JailListener implements Listener {
	
	// ----------------------------------------
	//  Singleton
	// ----------------------------------------

	private static JailListener instance = null;
	public static JailListener get() {
		if (instance == null) instance = new JailListener();
		return instance;
	}
	
	protected JailListener() { }

	
	// ----------------------------------------
	//  Fields
	// ----------------------------------------
	
	private final Factions factions = FactionsManager.get().fetch();
	
	// ----------------------------------------
	//  Events
	// ----------------------------------------

	// Send a player to jail when they try to move out of the X Y Z coords, but allow looking around
	@EventHandler
	public final void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (player == null) return;
		
		// They can look around
		if (event.getFrom().getX() == event.getTo().getX() &&
			event.getFrom().getY() == event.getTo().getY() &&
			event.getFrom().getZ() == event.getTo().getZ()) return;
		
		this.jailCheck(player, false);
	}
	
	// Jail a player when they join, let them know they're in jail too with a little more information
	@EventHandler
	public final void onPlayerJoin(PlayerLoginEvent event) {
		this.jailCheck(event.getPlayer(), true);
	}
	
	// In case they move worlds, we'll check it extra early 
	@EventHandler
	public final void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
		this.jailCheck(event.getPlayer(), true);
	}
	
	// ----------------------------------------
	//  Methods
	// ----------------------------------------

	// Simple method which does two things; returns true if they're in jail, and teleports if they are 
	public final boolean jailCheck(Player player, Boolean notify) {
		if(player == null) return false;
		
		FactionData fdata = FactionData.get(factions.getFactionFor(player).getID());
		
		if ( ! fdata.jailedPlayers.contains(player.getUniqueId().toString())) return false;
		
		player.teleport(fdata.jailLoc.getBukkitLocation());
		
		if (notify) player.sendMessage(ChatColor.RED + "You are currently in jail. You can leave this faction if you're unhappy with it.");
		
		return true;
	}

}
