package org.talend.mdm.commmon.util.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;


/**
 * Handles the mdm.conf file
 * @author bgrieder
 *
 */
public final class MDMConfiguration {
	
	static String MDM_CONF = "mdm.conf";
	
	static File file = new File(MDM_CONF);
	
	private static Properties CONFIGURATION = null;
	
	protected MDMConfiguration() {}
	
	public final static Properties getConfiguration(){
		return getConfiguration(false);
	}
	public final static Properties getConfiguration(boolean reload){
		if (reload) CONFIGURATION = null;
		if (CONFIGURATION != null) return CONFIGURATION;
		
		CONFIGURATION = new Properties();
		
		// try the current dir
		if (!file.exists()) {
			// if not found, try appending "bin"
			System.out.println("MDM Configuration: unable to find the configuration in '" + file.getAbsolutePath() + "'.");
			file = new File("bin/" + MDM_CONF);
			System.out.println("MDM Configuration: trying in '" + file.getAbsolutePath() + "'.");
		}
		
		if (file.exists()) {
			System.out.println("MDM Configuration: found in '" + file.getAbsolutePath() + "'.");
			try {
				CONFIGURATION.load(new FileInputStream(file));
			}
			catch (Exception e) {
				System.err.println("MDM Configuration: unable to load the configuration in '"+file.getAbsolutePath()+"' :"+e.getMessage()+". The default configurations will be used."); 
			}
		} 
		
		checkupPropertiesForXDBConf();
		
		return CONFIGURATION;
		
	}
	
	/**
	 * check up xdb config properties to add default value if it is unavailable
	 */
	private static void checkupPropertiesForXDBConf()
	{
		if (CONFIGURATION == null) return;
		
		if (CONFIGURATION.getProperty("xmldb.server.name") == null)
			CONFIGURATION.setProperty("xmldb.server.name", "localhost");
		if (CONFIGURATION.getProperty("xmldb.server.port") == null)
			CONFIGURATION.setProperty("xmldb.server.port", "8080");
		if (CONFIGURATION.getProperty("xmldb.administrator.username") == null)
			CONFIGURATION.setProperty("xmldb.administrator.username", "admin");
		if (CONFIGURATION.getProperty("xmldb.administrator.password") == null)
			CONFIGURATION.setProperty("xmldb.administrator.password", "1bc29b36f623ba82aaf6724fd3b16718");
		if (CONFIGURATION.getProperty("xmldb.driver") == null)
			CONFIGURATION.setProperty("xmldb.driver", "org.exist.xmldb.DatabaseImpl");
		if (CONFIGURATION.getProperty("xmldb.dbid") == null)
			CONFIGURATION.setProperty("xmldb.dbid", "exist");
		if (CONFIGURATION.getProperty("xmldb.dburl") == null)
			CONFIGURATION.setProperty("xmldb.dburl", "exist/xmlrpc/db");
		if (CONFIGURATION.getProperty("xmldb.isupurl") == null)
			CONFIGURATION.setProperty("xmldb.isupurl", "exist/");
	}
	/**
	 * save configure file
	 */
	public static void save(){
		FileOutputStream out;
		try {
			out = new FileOutputStream(file);
			CONFIGURATION.store(out, "mdm configure file");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static EDBType getDBType(){
    	Object dbtype=getConfiguration().get("xmldb.type");
    	if(dbtype!=null && dbtype.toString().equals(EDBType.ORACLE.getName())){
    		return EDBType.ORACLE;
    	}		
    	return EDBType.EXIST;
	}
	public static boolean isExistDb() {
		Object dbtype=getConfiguration().get("xmldb.type");
		if(dbtype!=null && !dbtype.toString().equals(EDBType.EXIST.getName())) {
			return false;
		}
		return true;
	}	
}
