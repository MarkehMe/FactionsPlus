package markehme.factionsplus.Cmds;

import org.bukkit.*;
import org.bukkit.command.*;

import markehme.factionsplus.config.*;

import com.massivecraft.factions.cmd.*;
import com.massivecraft.factions.struct.*;



public class CmdReloadFP extends FCommand {
	
	public CmdReloadFP() {
		super();
		this.aliases.add( "reloadfp" );
		
		this.optionalArgs.put( "all|conf|templates", "all");
		
		this.permission = Permission.RELOAD.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		this.errorOnToManyArgs = true;
		
		this.setHelpShort( "Reloads FactionPlus config" );
		// TODO: maybe add optional params to also reload jails or what now, just in case they were edited manually
	}
	
	
	@SuppressWarnings( "boxing" )
	@Override
	public void perform() {
		long startTime = System.nanoTime();
		String what = this.argAsString( 0, "all" ).toLowerCase();
		String fileWhat = null;
		boolean ret = false;
		try {
			if ( what.startsWith( "conf" ) ) {
				fileWhat=Config.fileConfig.getName();
				ret = Config.reloadConfig();
			} else
				if ( what.startsWith( "templ" ) ) {
					fileWhat=Config.templatesFile.getName();
					ret = Config.reloadTemplates();
				} else
					if ( what.equals( "all" ) ) {
						fileWhat=what;
						Config.reload();
						ret = true;//else it would just throw
					} else {
						msg( "<b>Invalid file specified. <i>Valid files: all, conf, templates" );
						return;
					}
		} catch ( Throwable t ) {
			t.printStackTrace();
			ret = false;
		} finally {
			long endTime = System.nanoTime() - startTime;
			if ( ret ) {
				msg( "<i>Reloaded FactionPlus <h>%s <i>from disk, took <h>%,8dns<i>.", fileWhat, endTime );
			} else {
				msg( ChatColor.RED+"Errors occurred while loading %s. See console for details.", fileWhat);
			}
		}
		
	}
	
}
