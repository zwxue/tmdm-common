package org.talend.mdm.commmon.util.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;


public class CommonUtil {
	
	
	public static String getConceptRevisionID(LinkedHashMap<String, String> itemsRevisionIDs,String defaultRevisionID,String conceptName) {

		ArrayList<String> patterns = new ArrayList<String>(itemsRevisionIDs.keySet());
		for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext(); ) {
			String pattern = iterator.next();
			if (conceptName.matches(pattern)) return itemsRevisionIDs.get(pattern);
		}
		return defaultRevisionID;
	}

}
