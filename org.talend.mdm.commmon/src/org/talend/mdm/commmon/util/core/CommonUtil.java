package org.talend.mdm.commmon.util.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommonUtil {
	
	
	public static String getConceptRevisionID(LinkedHashMap<String, String> itemsRevisionIDs,String defaultRevisionID,String conceptName) {

		ArrayList<String> patterns = new ArrayList<String>(itemsRevisionIDs.keySet());
		for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext(); ) {
			String pattern = iterator.next();
			if (conceptName.matches(pattern)) return itemsRevisionIDs.get(pattern);
		}
		return defaultRevisionID;
	}

	public static String getErrMsgFromException(Throwable e){
		Pattern p=Pattern.compile("(.*?):(.*?)");
		Matcher m=p.matcher(e.getLocalizedMessage());
		String msg=e.getLocalizedMessage();
		if(m.matches()){
			msg=m.group(2);
		}
		return msg;
	}
}
