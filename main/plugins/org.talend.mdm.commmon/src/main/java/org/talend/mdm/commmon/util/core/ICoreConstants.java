package org.talend.mdm.commmon.util.core;

import java.util.HashMap;

public interface ICoreConstants {

    String X_Schematron = "X_Schematron"; //$NON-NLS-1$

    String X_Workflow = "X_Workflow"; //$NON-NLS-1$

    String[] WORKFLOW_ACCESSES = { "Read-only", "Hidden", "Writable" }; //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * the default svn name in VersionSystem
     */
    String DEFAULT_SVN = "DEFAULT_SVN"; //$NON-NLS-1$

    String DEFAULT_CATEGORY_ROOT = "category"; //$NON-NLS-1$

    /**
     * cross referencing init datacluster & datamodel
     */
    String CrossReferencing_datacluster = "crossreferencing"; //$NON-NLS-1$

    String CrossReferencing_datamodel = "crossreferencing"; //$NON-NLS-1$

    String SYSTEM_INTERACTIVE_ROLE = "System_Interactive"; //$NON-NLS-1$

    String SYSTEM_WEB_ROLE = "System_Web"; //$NON-NLS-1$

    String SYSTEM_VIEW_ROLE = "System_View"; //$NON-NLS-1$

    String ADMIN_PERMISSION = "administration"; //$NON-NLS-1$

    String SYSTEM_ADMIN_ROLE = "System_Admin"; //$NON-NLS-1$

    String TALEND_NAMESPACE = "http://www.talend.com/mdm"; //$NON-NLS-1$

    public static class rolesConvert {

        public static final HashMap<String, String> oldRoleToNewRoleMap = new HashMap<String, String>();

        static {
            oldRoleToNewRoleMap.put("Default_Admin", ICoreConstants.SYSTEM_ADMIN_ROLE);
            oldRoleToNewRoleMap.put("Default_User", ICoreConstants.SYSTEM_INTERACTIVE_ROLE);
            oldRoleToNewRoleMap.put("Default_Viewer", ICoreConstants.SYSTEM_VIEW_ROLE);
        }
    }

}
