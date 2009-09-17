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
		
		return CONFIGURATION;
		
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
