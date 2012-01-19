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
     
    private static String MDM_CONF = "mdm.conf"; //$NON-NLS-1$
    
    private static File file;
    
    private static Properties CONFIGURATION = null;
    
    private MDMConfiguration() {}
    
    public final static Properties getConfiguration(){
        return getConfiguration(false);
    }
    
    public final static Properties getConfiguration(boolean reload){
        if (reload) CONFIGURATION = null;
        if (CONFIGURATION != null) return CONFIGURATION;
        
        CONFIGURATION = new Properties();
        
        String currentDir = System.getProperty("user.dir"); //$NON-NLS-1$
        file = new File(currentDir, MDM_CONF);
        
        // try the current dir
        if (!file.exists()) {
            // if not found, try appending "bin"
            System.out.println("MDM Configuration: unable to find the configuration in '" + file.getAbsolutePath() + "'.");
            file = new File(currentDir, "bin/" + MDM_CONF);
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
        else
            System.err.println("MDM Configuration: unable to load the configuration in '"+file.getAbsolutePath() +". The default configurations will be used.");
        
        checkupPropertiesForXDBConf();
        
        return CONFIGURATION;
        
    }
    
    /**
     * check up xdb config properties to add default value if it is unavailable
     */
    @SuppressWarnings("nls")
    private static void checkupPropertiesForXDBConf()
    {
        if (CONFIGURATION == null) return;
        
        if (CONFIGURATION.getProperty("xmldb.server.name") == null)
            CONFIGURATION.setProperty("xmldb.server.name", "localhost");
        if (CONFIGURATION.getProperty("xmldb.server.port") == null)
            CONFIGURATION.setProperty("xmldb.server.port", "8180");
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
        if(file == null)
            throw new IllegalStateException();
        
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            CONFIGURATION.store(out, "MDM configuration file"); //$NON-NLS-1$
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e2) {
                }
            }
        }
    }
    
    public static EDBType getDBType(){
        Object dbtype=getConfiguration().get("xmldb.type");
        if(dbtype!=null && dbtype.toString().equals(EDBType.ORACLE.getName())){
            return EDBType.ORACLE;
        }
        if(dbtype!=null && dbtype.toString().equals(EDBType.BERKELEY.getName())){
            return EDBType.BERKELEY;
        }   
        if(dbtype!=null && dbtype.toString().equals(EDBType.QIZX.getName())){
            return EDBType.QIZX;
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
    
    public static String getHttpPort() {
        String port = getConfiguration().getProperty("http.server.port"); //$NON-NLS-1$
        port = port == null ? "8180" : port; //$NON-NLS-1$
        return port;
    }

    public static String getAdminPassword() {
        String passwd = getConfiguration().getProperty("admin.password"); //$NON-NLS-1$
        passwd = passwd == null ? "1a254116eb5e70714b0680dfd4d8f7d4" : passwd; //$NON-NLS-1$
        return passwd;
    }

    public static String getAdminUser() {
        String user = getConfiguration().getProperty("admin.user"); //$NON-NLS-1$
        user = user == null ? "admin" : user; //$NON-NLS-1$
        return user;
    }
}
