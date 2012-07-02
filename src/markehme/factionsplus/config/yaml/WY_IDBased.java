package markehme.factionsplus.config.yaml;

import markehme.factionsplus.config.*;
import markehme.factionsplus.extras.*;
import markehme.factionsplus.util.*;



public abstract class WY_IDBased<METADATA_TYPE> extends WYItem<METADATA_TYPE> {
	
	private String	id;
	
	
	public WY_IDBased( int lineNumber, String identifier){//, WYSection _parent, WYItem _prev ) {
		super( lineNumber);//, _parent, _prev );
		id = identifier;
		assert Q.nn( id );
	}
	
	
	public String getId() {
		return id;
	}
	
	
	public void setId( String string ) {
		id = string;
	}
	
	
	@Deprecated
	public String getInAbsoluteDottedForm() {
		return getID_InAbsoluteDottedForm( null );
	}
	
	
	/**
	 * @param upToAndExcluding
	 *            can be null
	 * @return ie. extras.lwc.disableSomething
	 */
	@Deprecated
	public String getID_InAbsoluteDottedForm( WYSection upToAndExcluding ) {
		String df = "";
		WYSection p = getParent();
		if ( ( null != p ) && ( !p.equals( upToAndExcluding ) ) ) {
			df = p.getID_InAbsoluteDottedForm( upToAndExcluding ) + Config.DOT;
		}
		return df + this.getId();
	}
	
	
	@Override
	public String toString() {
		return getId() + WannabeYaml.IDVALUE_SEPARATOR;
	}
}
