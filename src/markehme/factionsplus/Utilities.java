package markehme.factionsplus;

import java.io.*;

import markehme.factionsplus.FactionsBridge.*;
import markehme.factionsplus.config.*;
import markehme.factionsplus.util.*;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.permissions.*;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;


public class Utilities {
	/* ********** FILE RELATED ********** */
	
	public static String readFileAsString(File filePath) {
		try {
			FileInputStream fstream = new FileInputStream(filePath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			String fullThing = "";

			while ((strLine = br.readLine()) != null)   {
				fullThing = fullThing + strLine + "\r\n";
			}

			in.close();

			return fullThing;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}


	public static void writeToFile(String fileN, String T) {
		BufferedWriter bw =null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(fileN), true));
			bw.write(T);
			bw.newLine();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (null != bw) {
				try {
					bw.close();
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}
		}
	}

	/* ********** JAIL RELATED ********** */

	public static boolean isJailed(Player thePlayer) {
		FPlayer fplayer = FPlayers.i.get(thePlayer.getName());
		
		if(fplayer == null) return false;
		
		File jailDataFile = new File(Config.folderJails,"jaildata." + fplayer.getFactionId() + "." + thePlayer.getName());

		if(!jailDataFile.exists()) {
			return false;
		}

		String JailData = Utilities.readFileAsString(jailDataFile);

		if(JailData == "0") {
			return false;
		} else {
			return true;
		}
	}
	
	/* ********** FACTIONS RELATED ********** */

	public static boolean isOfficer(FPlayer fplayer) {
		return Bridge.factions.getRole( fplayer).equals( FactionsAny.Relation.OFFICER );
	}

	public static boolean isLeader(FPlayer fplayer) {
		return Bridge.factions.getRole( fplayer).equals( FactionsAny.Relation.LEADER );
	}

//	public static boolean checkGroupPerm(World world, String group, String permission) {
//		if(Config.config.getBoolean("enablePermissionGroups")) {
//			return(FactionsPlus.permission.groupHas(world, group, permission));
//		} else {
//			return true;
//		}
//	}

	public static void addPower(Player player, double amount) {
		FPlayer fplayer = FPlayers.i.get(player);
		fplayer.setPowerBoost(fplayer.getPowerBoost() + amount);
	}

	public static void addPower(FPlayer player, double amount) {
		player.setPowerBoost(player.getPowerBoost() + amount);
	}

	public static void addPower(String player, double amount) {
		FPlayer fplayer = FPlayers.i.get(player);
		fplayer.setPowerBoost(fplayer.getPowerBoost() + amount);
	}

	public static void removePower(Player player, double amount) {
		FPlayer fplayer = FPlayers.i.get(player);
		fplayer.setPowerBoost(fplayer.getPowerBoost() - amount);
	}

	public static void removePower(FPlayer player, double amount) {
		player.setPowerBoost(player.getPowerBoost() - amount);
	}

	public static void removePower(String player, double amount) {
		FPlayer fplayer = FPlayers.i.get(player);
		fplayer.setPowerBoost(fplayer.getPowerBoost() - amount);
	}

	public static int getCountOfWarps(Faction faction) {
		File currentWarpFile = new File(Config.folderWarps, faction.getId());

		int c = 0;
		if (currentWarpFile.exists()) {	
			FileInputStream fstream=null;
			DataInputStream in=null;
			BufferedReader br =null;
			try {
				fstream = new FileInputStream(currentWarpFile);
				in = new DataInputStream(fstream);
				br= new BufferedReader(new InputStreamReader(in));
				String strLine;

				while ((strLine = br.readLine()) != null) {
					if(strLine.contains(":")) {
						c++;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();			
			} finally {
				if ( null != br ) {
					try {
						br.close();
					} catch ( IOException e ) {
						e.printStackTrace();
					}
				}
				if ( null != in ) {
					try {
						in.close();
					} catch ( IOException e ) {
						e.printStackTrace();
					}
				}
				if ( null != fstream ) {
					try {
						fstream.close();
					} catch ( IOException e ) {
						e.printStackTrace();
					}
				}
			}
		}
		return c;
	}
	
	
	// ---------------- other simple utils
	
	public static File getCurrentFolder() {
		return new File(".");
	}
	
	/**
	 * the object is checked by reference (ie. == as opposed to .equals ) to see if it's contained in the array
	 * @param objRef
	 * @param array
	 * @return
	 */
	public static boolean isReferenceInArray( Object objRef, Object[] array ) {
		for ( int i = 0; i < array.length; i++ ) {
			if (objRef == array[i]) { //not .equals(), we just want to know if that reference is in the array
				return true;
			}
		}
		return false;
	}
	
	/**
	 * uses .equals()
	 * @param objRef
	 * @param array
	 * @return
	 */
	public static int getIndexOfObjectInArray( Object objRef, Object[] array ) {
		for ( int i = 0; i < array.length; i++ ) {
			if (objRef.equals(array[i])) { //not .equals(), we just want to know if that reference is in the array
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * checks if player has permission<br />
	 * if he's OP then it auto has perm
	 * @param p
	 * @param perm
	 * @return
	 */
	public static boolean hasPermissionOrIsOp(Player p, Permission perm) {
		if (null != p) {
			return p.isOp() || p.hasPermission( perm );
		}
		return false;
	}
	
	public static boolean isOp(FPlayer fplayer) {
		return fplayer.getPlayer().isOp();
	}
	
	public static boolean isWarZone(Faction faction)
	{
		return faction.getId().equals("-2");
	}

	public static boolean isSafeZone(Faction faction)
	{
		return faction.getId().equals("-1");
	}

	public static boolean isWilderness(Faction faction)
	{
		return faction.getId().equals("0");
	}

	/**
	 * aka non-safezone and non-warzone and not wilderness
	 * @param faction
	 * @return true is so
	 */
	public static boolean isNormalFaction(Faction faction) {
		return !isWarZone( faction ) && !isSafeZone(faction) && !isWilderness( faction );
	}
	
	private static final int margin=10;//ie. 12.345 => 123 if margin is 10 or 1234 if margin is 100 ie. multiply by margin & truncate .*
	public static boolean isJustLookingAround(Location from, Location to) {
		assert Q.nn( from );
		assert Q.nn( to );

	        if (from.getWorld() != to.getWorld() && (!from.getWorld().equals(to.getWorld()))) {
	            return false;
	        }
//	        System.out.println(getIntegerPartMultipzliedBy(from.getX(), 10)+" vs "+from.getX());
	        if (getIntegerPartMultipzliedBy(from.getX(), margin) != getIntegerPartMultipzliedBy(to.getX(), margin)) {
	            return false;
	        }
	        if (getIntegerPartMultipzliedBy(from.getY(), margin) != getIntegerPartMultipzliedBy(to.getY(), margin)) {
	            return false;
	        }
	        if (getIntegerPartMultipzliedBy(from.getZ(), margin) != getIntegerPartMultipzliedBy(to.getZ(), margin)) {
	            return false;
	        }
	        return true;
	}
	
	public static final int getIntegerPartMultipzliedBy(double d, int multipliedByThis) {
		assert multipliedByThis>0;
		String asString = Double.toString( d*multipliedByThis );
		int dotAt = asString.indexOf( Config.DOT );
		if (dotAt<0) {//assumed it's fully integer
			dotAt=asString.length(); 
		}
//		multipliedByThis=Math.min( Math.getExponent( multipliedByThis )+dotAt, dotAt);
		return Integer.parseInt( asString.substring( 0, dotAt) );
	}
	
	/**
	 * just like: from=to; except the pitch/yal is not reset; ie. keep the pitch/yaw of "from"
	 * @param from
	 * @param to
	 * @return from  (unneeded but hey)
	 */
	public static final Location setLocationExceptEye(Location from, Location to) {
		assert Q.nn( from );
		assert Q.nn(to);
		from.setWorld( to.getWorld() );
		from.setX( to.getX() );
		from.setY( to.getY() );
		from.setZ( to.getZ() );
		return from;
	}
	//***********************************World Checking/Common Condition Checking for Listeners to also respect factions settings***********************//
	/**
	 * Checks if world is an ignored PvP world in faction config
	 * @param world
	 * @return boolean (true if it is an ignored world)
	 */
	public static final boolean noMonitorPvPWorld(World world) {
		String name = world.getName();
		if(FactionsPlus.ignoredPvPWorlds.contains(name)) {
			return true;
		} else {
			return false;
		}
		
	}
	/**
	 * Checks if claiming in world is disabled in config
	 * @param world
	 * @return boolean (true if it is a disabled claiming world)
	 */
	public static final boolean noClaimWorld(World world) {
		String name = world.getName();
		if(FactionsPlus.noClaimingWorlds.contains(name)) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * Checks if powerloss in world is disabled in config
	 * @param world
	 * @return boolean (true if it is a powerloss disabled world)
	 */
	public static final boolean noPowerLossWorld(World world) {
		String name = world.getName();
		if(FactionsPlus.noPowerLossWorlds.contains(name)) {
			return true;
		} else {
			return false;
		}
	}
}
