package markehme.factionsplus.Cmds.req;

import markehme.factionsplus.MCore.FPUConf;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.massivecore.cmd.MassiveCommand;
import com.massivecraft.massivecore.cmd.req.ReqAbstract;

public class ReqAnnouncementsEnabled extends ReqAbstract {
	private static final long serialVersionUID = 1L;

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static ReqAnnouncementsEnabled i = new ReqAnnouncementsEnabled();
	public static ReqAnnouncementsEnabled get() { return i; }

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public boolean apply(CommandSender sender, MassiveCommand command) {
		return FPUConf.get(UPlayer.get(sender).getUniverse()).announcementsEnabled;
	}

	@Override
	public String createErrorMessage(CommandSender sender, MassiveCommand command) {
		return "Announcements are not enabled.";
	}
}