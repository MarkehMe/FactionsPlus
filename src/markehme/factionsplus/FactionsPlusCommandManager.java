package markehme.factionsplus;

import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.*;

import markehme.factionsplus.Cmds.*;
import markehme.factionsplus.extras.*;



/*
	/f warp [name]	Warps to the Faction warp!	factionsplus.warp
	/f createwarp [name]	Creates a Faction warp, these can only be created in faction land	factionsplus.createwarp
	/f deletewarp [name]	Removes a Faction warp, these can only be created in faction land	factionsplus.deletewarp
	/f removewarp [name]	Alias of deletewarp	factionsplus.deletewarp
	/f setjail	Sets the jail location, this can only be set in faction land	factionsplus.setjail
	/f jail [name]	Sends a player to jail!	factionsplus.jail
	/f unjail [name]	Removes a player from jail!	factionsplus.jail

 */

public class FactionsPlusCommandManager {
	FactionsPlusCommandManager FactionsPlusCommandManager;
	
	public static void setup() {
		// Warp Commands 
		addSC(new CmdAddWarp()); 
		addSC(new CmdRemoveWarp());
		addSC(new CmdWarp());
		addSC(new CmdListWarps());
		
		// Jail Commands
		addSC(new CmdSetJail());
		addSC(new CmdUnsetJail());
		addSC(new CmdJail());
		addSC(new CmdUnJail());
		
		// General Commands
		addSC(new CmdAnnounce());
		addSC(new CmdBan());
		addSC(new CmdToggleState());
		addSC(new CmdFC());
		addSC(new CmdGC());
		
		// Region based Commands
		//addSC(new CmdPlot());
		
		// New Admin commands 
		addSC(new CmdFactionHome());
		
		P.p.cmdBase.cmdMoney.addSubCommand(new CmdMoneyTop());//FIXME: add this too on 1.6 help
		
		addSC(new CmdDebug());
		
		
		Bridge.factions.finalizeHelp(); 
	}

	private static final void addSC(FCommand subCommand) {
		Bridge.factions.addSubCommand(subCommand);
	}
	
}
