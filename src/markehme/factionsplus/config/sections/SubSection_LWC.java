package markehme.factionsplus.config.sections;

import markehme.factionsplus.config.*;


public final class SubSection_LWC {
	
//	public static final static final String	str_removeLWCLocksOnClaim	= "removeLWCLocksOnClaim";

	@Option(oldAliases_alwaysDotted={
		"extras.lwc.removeLWCLocksOnClaim",
		"removeLWCLocksOnClaim" //the first one in this line is the latest ID for this option, and will be used when saving the config.yml
		//all below lines are used to import the from the obsolete names for this same config option, the higher ones override the lower ones
		,"extras.lwc.useLWCIntegrationFix"
//		,"removeLWCLocksOnClaim"
		,"useLWCIntegrationFix"
	}, realAlias_inNonDottedFormat = "removeAllLocksOnClaim" )
	public  final _boolean removeAllLocksOnClaim=new _boolean(false);
	
	@Option(oldAliases_alwaysDotted={
		"extras.lwc.blockCPublicAccessOnNonOwnFactionTerritory"
	},
			realAlias_inNonDottedFormat = "blockCPublicAccessOnNonOwnFactionTerritory" )
	public  final _boolean blockCPublicAccessOnNonOwnFactionTerritory=new _boolean(false);
}
