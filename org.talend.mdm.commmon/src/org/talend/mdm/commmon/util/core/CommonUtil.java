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

    /**
     * get the DB repository root path
     */
    public static String getDBRootPath(){
    	EDBType dbtype=MDMConfiguration.getDBType();
    	if(dbtype.getName().equals(EDBType.EXIST.getName())){
    		return EDBType.EXIST.getRoot();
    	}
    	if(dbtype.getName().equals(EDBType.ORACLE.getName())){
    		return EDBType.ORACLE.getRoot();
    	}
    	return "";
    }
    /**
     * 
     * @param revisionID
     * @param clusterName
     * @return
     */
    public static String getPath(String revisionID, String clusterName){
    	if(revisionID!=null) revisionID=revisionID.replaceAll("\\[HEAD\\]|HEAD", "");
		String rootpath=getDBRootPath();
		String collectionPath =
       		(revisionID == null || "".equals(revisionID) ? rootpath+"/" : rootpath+"/R-"+revisionID+"/")
       		+(clusterName == null ? "" : clusterName); 
		EDBType dbtype=MDMConfiguration.getDBType();
		if(dbtype.getName().equals(EDBType.BERKELEY.getName())){
			collectionPath=collectionPath.startsWith("/")?collectionPath.substring(1):collectionPath;
			return collectionPath.replace("/", ".");
		}
		return collectionPath;
    }
}
