package org.talend.mdm.commmon.util.core;

/**
 * @author achen
 * all uuid type
 */
import java.util.HashSet;
import java.util.Set;

public enum EUUIDCustomType {
	//two custom simple type (only used for concept id)
	UUID("UUID"),
	PICTURE("PICTURE"),
	AUTO_INCREMENT("AUTO_INCREMENT");
	String name;
	EUUIDCustomType(String name){
		this.name=name;
	}
	
	public String getName() {
		return name;
	}


	public static Set<String> allTypes(){
		Set<String> list=new HashSet<String>();
		for(EUUIDCustomType type:values()){
			list.add(type.name);
		}
		return list;
	}
}
