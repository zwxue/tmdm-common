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
	static File file = new File("mdm.conf");
	static{

		
	}
	private static Properties CONFIGURATION = null;
	
	protected MDMConfiguration() {}
	
	public final static Properties getConfiguration(){
		return getConfiguration(false);
	}
	public final static Properties getConfiguration(boolean reload){
		if (reload) CONFIGURATION = null;
		if (CONFIGURATION != null) return CONFIGURATION;
		
		CONFIGURATION = new Properties();
		
		//first try Current path
		
		if (file.exists()) {
			try {
				CONFIGURATION.load(new FileInputStream(file));
			} catch (Exception e) {
				String err = "MDM Configuration: unable to load the configuration in '"+file.getAbsolutePath()+"' :"+e.getMessage()+". The default configurations will be used."; 
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
		if (CONFIGURATION.getProperty("mldb.administrator.password") == null)
			CONFIGURATION.setProperty("mldb.administrator.password", "1bc29b36f623ba82aaf6724fd3b16718");
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
}
