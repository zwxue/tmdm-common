// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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
 *
 * @author bgrieder
 */
public final class MDMConfiguration {

    private static final Logger logger = Logger.getLogger(MDMConfiguration.class);

    private static final String MDM_CONF = "mdm.conf"; //$NON-NLS-1$

    private static File file;

    private static Properties CONFIGURATION = null;

    private MDMConfiguration() {
    }

    public static Properties getConfiguration() {
        return getConfiguration(false);
    }

    public static Properties getConfiguration(boolean reload) {
        if (reload) {
            CONFIGURATION = null;
        }
        if (CONFIGURATION != null) {
            return CONFIGURATION;
        }
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
            } catch (Exception e) {
                logger.warn("MDM Configuration: unable to load the configuration in '" + file.getAbsolutePath() + "' :" + e.getMessage() + ". The default configurations will be used.");
            }
        } else {
            logger.warn("MDM Configuration: unable to load the configuration in '" + file.getAbsolutePath()
                    + ". The default configurations will be used.");
        }
        checkupPropertiesForXDBConf();
        return CONFIGURATION;
    }

    /**
     * check up xdb config properties to add default value if it is unavailable
     */
    private static void checkupPropertiesForXDBConf() {
        if (CONFIGURATION == null) {
            return;
        }
        if (CONFIGURATION.getProperty("xmldb.server.name") == null) { //$NON-NLS-1$
            CONFIGURATION.setProperty("xmldb.server.name", "localhost"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (CONFIGURATION.getProperty("xmldb.server.port") == null) { //$NON-NLS-1$
            CONFIGURATION.setProperty("xmldb.server.port", "8180"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (CONFIGURATION.getProperty("xmldb.administrator.username") == null) { //$NON-NLS-1$
            CONFIGURATION.setProperty("xmldb.administrator.username", "admin"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (CONFIGURATION.getProperty("xmldb.administrator.password") == null) { //$NON-NLS-1$
            CONFIGURATION.setProperty("xmldb.administrator.password", "1bc29b36f623ba82aaf6724fd3b16718"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (CONFIGURATION.getProperty("xmldb.driver") == null) { //$NON-NLS-1$
            CONFIGURATION.setProperty("xmldb.driver", "org.exist.xmldb.DatabaseImpl"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (CONFIGURATION.getProperty("xmldb.dbid") == null) { //$NON-NLS-1$
            CONFIGURATION.setProperty("xmldb.dbid", "exist"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (CONFIGURATION.getProperty("xmldb.dburl") == null) { //$NON-NLS-1$
            CONFIGURATION.setProperty("xmldb.dburl", "exist/xmlrpc/db"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (CONFIGURATION.getProperty("xmldb.isupurl") == null) { //$NON-NLS-1$
            CONFIGURATION.setProperty("xmldb.isupurl", "exist/"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * save configure file
     */
    public static void save() {
        if (file == null) {
            throw new IllegalStateException("No MDM configuration was previously loaded.");
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            CONFIGURATION.store(out, "MDM configuration file"); //$NON-NLS-1$
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e2) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Error occurred during close() operation during configuration save.", e2);
                    }
                }
            }
        }
    }

    public static EDBType getDBType() {
        Object dbType = getConfiguration().get("xmldb.type"); //$NON-NLS-1$
        if (dbType != null && dbType.toString().equals(EDBType.ORACLE.getName())) {
            return EDBType.ORACLE;
        }
        if (dbType != null && dbType.toString().equals(EDBType.BERKELEY.getName())) {
            return EDBType.BERKELEY;
        }
        if (dbType != null && dbType.toString().equals(EDBType.QIZX.getName())) {
            return EDBType.QIZX;
        }
        return EDBType.EXIST;
    }

    public static boolean isExistDb() {
        Object dbType = getConfiguration().get("xmldb.type"); //$NON-NLS-1$
        return !(dbType != null && !dbType.toString().equals(EDBType.EXIST.getName()));
    }

    public static String getHttpPort() {
        String port = getConfiguration().getProperty("http.server.port"); //$NON-NLS-1$
        port = port == null ? "8180" : port; //$NON-NLS-1$
        return port;
    }

    public static String getAdminPassword() {
        String password = getConfiguration().getProperty("admin.password"); //$NON-NLS-1$
        password = password == null ? "talend" : password; //$NON-NLS-1$
        return password;
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

    /**
     * It can be only called when MDM applies on user data containers.
     * <li>DispatchWrapper is Hybrid configuration,</li>
     * <li>SQLWrapper is Full SQL</li>
     */
    public static boolean isSqlDataBase() {
        String xmlServerClass = getConfiguration().getProperty("xmlserver.class"); //$NON-NLS-1$
        return "com.amalto.core.storage.DispatchWrapper".equals(xmlServerClass)  //$NON-NLS-1$
                || "com.amalto.core.storage.SQLWrapper".equals(xmlServerClass); //$NON-NLS-1$
    }
}
