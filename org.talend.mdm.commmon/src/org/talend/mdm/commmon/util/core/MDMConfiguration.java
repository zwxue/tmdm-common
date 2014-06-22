// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.commmon.util.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Handles the mdm.conf file
 * @author bgrieder
 *
 */
public final class MDMConfiguration {
    
    private static Logger logger = Logger.getLogger(MDMConfiguration.class);

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
            logger.info("MDM Configuration: unable to find the configuration in '" + file.getAbsolutePath() + "'.");
            file = new File(currentDir, "bin/" + MDM_CONF);
            logger.info("MDM Configuration: trying in '" + file.getAbsolutePath() + "'.");
        }
        
        if (file.exists()) {
            logger.info("MDM Configuration: found in '" + file.getAbsolutePath() + "'.");
            try {
                CONFIGURATION.load(new FileInputStream(file));
            }
            catch (Exception e) {
                logger.warn("MDM Configuration: unable to load the configuration in '"+file.getAbsolutePath()+"' :"+e.getMessage()+". The default configurations will be used.");
            }
        }
        else
            logger.warn("MDM Configuration: unable to load the configuration in '" + file.getAbsolutePath()
                    + ". The default configurations will be used.");
        
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
            logger.error(e.getMessage(), e);
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
        passwd = passwd == null ? "talend" : passwd; //$NON-NLS-1$
        return passwd;
    }

    public static String getAdminUser() {
        String user = getConfiguration().getProperty("admin.user"); //$NON-NLS-1$
        user = user == null ? "admin" : user; //$NON-NLS-1$
        return user;
    }
    
    public static int getAutoEntityFindThreshold() {
        String value = getConfiguration().getProperty("autoentityfind.item.max"); //$NON-NLS-1$
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }
    
    public static boolean isSqlDataBase() {
        String xmlServerClass = getConfiguration().getProperty("xmlserver.class"); //$NON-NLS-1$
        if ("com.amalto.core.storage.DispatchWrapper".equals(xmlServerClass)) { //$NON-NLS-1$
            return true;
        }
        return false;
    }
}
