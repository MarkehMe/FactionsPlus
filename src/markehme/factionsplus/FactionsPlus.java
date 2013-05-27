package markehme.factionsplus;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import markehme.factionsplus.config.Config;
import markehme.factionsplus.extras.LWCBase;
import markehme.factionsplus.extras.LWCFunctions;
import markehme.factionsplus.extras.Metrics;
import markehme.factionsplus.listeners.CoreListener;
import markehme.factionsplus.listeners.FPConfigLoadedListener;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitWorker;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.MConf;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiversePortals.MultiversePortals;
import com.onarandombox.MultiversePortals.utils.PortalManager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class FactionsPlus extends FactionsPlusPlugin {

	public static FactionsPlus instance;
	
	public static Logger log 									= 	Logger.getLogger("Minecraft");
	
	Factions factions;
	
    public static Permission permission 						= 	null;
    
	public static boolean isWorldEditEnabled 					= 	false;
	public static boolean isWorldGuardEnabled 					= 	false;
	public static boolean isMultiversePortalsEnabled 			= 	false;
	
	public final CoreListener corelistener 						=	new CoreListener();
	
	public static WorldEditPlugin worldEditPlugin 				= 	null;
	public static WorldGuardPlugin worldGuardPlugin 			= 	null;
	
	public static MultiversePortals multiversePortalsPlugin 	= 	null;
	
	public static String version;
	public static String FactionsVersion;
	
	private static Metrics metrics 								= 	null;
	
	public static Set<String> ignoredPvPWorlds 					= 	MConf.get().worldsIgnorePvP;
	public static Set<String> noClaimingWorlds 					= 	MConf.get().worldsNoClaiming;
	public static Set<String> noPowerLossWorlds 				= 	MConf.get().worldsNoPowerLoss;
	
	public FactionsPlus() {
		super();
		
		if ( null != instance ) {
			throw bailOut("This was not expected, getting new-ed again without getting unloaded first.\n"
				+"Safest way to reload is to stop and start the server!");
		}
		
		instance = this;
	}
	
	
	@Override
	public void onDisable() {
		Throwable failed = null;// TODO: find a way to chain all thrown exception rather than overwrite all older
		try {
			try {
				EssentialsIntegration.onDisable();
			} catch ( Throwable t ) {
				failed = t;
			}
			
			try {
				Config.deInit();
			} catch ( Throwable t ) {
				failed = t;
			}
			
			// TODO: unhook Factions registered commands on disabling self else they'll still call our code and possibly NPE
			// since we deinited some of our parts; can add an if for each command and check if we're enabled and make it in a base class
			
			try {
				if ( LWCBase.isLWCPluginPresent() ) {
					LWCFunctions.unhookLWC();
				}
			} catch ( Throwable t ) {
				failed = t;
			}
			
			try {
				FactionsPlusUpdate.ensureNotRunning();
			} catch ( Throwable t ) {
				failed = t;
			}
			
			try {
				getServer().getServicesManager().unregisterAll( this );// not really needed at this point, only for when using
																		// .register(..)
			} catch ( Throwable t ) {
				failed = t;
			}
			
			try {
				HandlerList.unregisterAll( FactionsPlus.instance );
			} catch ( Throwable t ) {
				failed = t;
			}
			
			try {
				//this will deInit metrics, but it will be enabled again onEnable
				getServer().getScheduler().cancelTasks( this );
			} catch ( Throwable t ) {
				failed = t;
			}
			
			//TODO: investigate why nag author happens ... even though we seem to be shuttind down task correctly
			//some tasks still remain from both FP and Vault at this point if doing a server `reload` as soon as you see "[FactionsPlus] Ready." 
//			List<BukkitWorker> workers = Bukkit.getScheduler().getActiveWorkers();
//			info("Active Workers: "+workers.size());
//			
//			for ( BukkitWorker bukkitWorker : workers ) {
//				info("  workerOwner: "+bukkitWorker.getOwner()+" taskId="+bukkitWorker.getTaskId()
//					+", "+bukkitWorker.getThread().getName());			
//			}
			
			if ( null == failed ) {
				FactionsPlusPlugin.info( "Disabled successfuly." );
			}
			
		} catch ( Throwable t ) {
			failed = t;
		} finally {
			if ( null != failed ) {
				FactionsPlusPlugin.info( "Did not disable successfuly." );
				FactionsPlus.severe( failed, "This is the last seen exception:" );
			}
		}
	}
	
	
	@Override
	public void onEnable() {
		try {
			super.onEnable(); 
			
			this.ignoredPvPWorlds			= 	MConf.get().worldsIgnorePvP;
			this.noClaimingWorlds 			= 	MConf.get().worldsNoClaiming;
			this.noPowerLossWorlds 			= 	MConf.get().worldsNoPowerLoss;
			
			Config.init();
			
			PluginManager pm = this.getServer().getPluginManager();
			FactionsVersion = (pm.getPlugin("Factions").getDescription().getVersion());
			
			FactionsPlusPlugin.info("GitHub: https://github.com/MarkehMe/FactionsPlus");
			FactionsPlusPlugin.info("Factions version " + FactionsVersion ); 
			
			pm.registerEvents(new FPConfigLoadedListener(),this);
			
			Config.reload(); 
			
			pm.registerEvents(this.corelistener, this);
			
			FactionsPlusJail.server = getServer();
			CoreListener.fp = FactionsPlusJail.server;
			
			FactionsPlusCommandManager.setup();
			
	        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
	        if (permissionProvider != null) {
	            permission = permissionProvider.getProvider();
	        }
	        
	        if(1<2) {        //Temporary Always True Until a Config Option is Created 
	        	if(pm.isPluginEnabled("WorldEdit")) {
	        		worldEditPlugin = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
	        		FactionsPlusPlugin.info("Hooked into WorldEdit " + pm.getPlugin("WorldEdit").getDescription().getVersion());
	        		isWorldEditEnabled = true;
	        		
	        	}
	            if(pm.isPluginEnabled("WorldGuard")) {
	            	worldGuardPlugin = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");
	            	FactionsPlusPlugin.info("Hooked into WorldGuard " + pm.getPlugin("WorldGuard").getDescription().getVersion());
	            	isWorldGuardEnabled = true;
	            	
	            }
	        }
	        
	        if(pm.isPluginEnabled("Multiverse-Portals")) { 
	        	Plugin MVc = getServer().getPluginManager().getPlugin("Multiverse-Portals");
	            
	            if (MVc instanceof MultiversePortals) {
	            	this.multiversePortalsPlugin = (MultiversePortals) MVc;
	            }
	            
	        	isMultiversePortalsEnabled = true;
	        	FactionsPlusPlugin.info("Hooked into Multiverse-Portals " + pm.getPlugin("Multiverse-Portals").getDescription().getVersion());
	        }
	        version = getDescription().getVersion();
	        
			FactionsPlusPlugin.info("Ready. ");
			
			try {
				metrics = new Metrics( this );

				// Version of Factions
			    /*metrics.addCustomData(new Metrics.Plotter("Factions 1.x") {

			        @Override
			        public int getValue() {
			        	int FVC = 0;
			            return FVC;
			        }

			    });*/
			    
				metrics.start();
			} catch ( IOException e ) {
				FactionsPlusPlugin.info("Metrics could not start up: "+e.getMessage() );
			}
						
		} catch (Throwable t) {
			FactionsPlus.severe( t);
			if (isEnabled()) {
				disableSelf();
			}
		} //try
	} //onEnable
	
	
}
