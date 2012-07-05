package markehme.factionsplus.config.sections;

import markehme.factionsplus.config.*;



public class SubSection_TeleportsIntoAllyDeny {
	
	@Option(
			oldAliases_alwaysDotted = {}, realAlias_inNonDottedFormat = "ViaHomeCommand" )
	public final _boolean	viaHome		= new _boolean( false );	
																	
	@Option(
			oldAliases_alwaysDotted = {}, realAlias_inNonDottedFormat = "ViaEnderPeals" )
	public final _boolean	viaPearls	= new _boolean( false );	

	public boolean isAnySet() {
		return viaHome._ || viaPearls._;
	}

	public boolean shouldPreventHomeTelepors() {
		return viaHome._;
	}

	public boolean shouldPreventEnderPearlsTeleports() {
		return viaPearls._;
	}
}