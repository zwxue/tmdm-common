package org.talend.mdm.commmon.util.core;

public interface ICoreConstants {
	//schematron_rule
	//static final String SCHEMATRON_RULE="schematron_rule";
	//static final String SCHEMATRON_TAG="<schema xmlns=\"http://www.ascc.net/xml/schematron\" ns=\"http://xml.apache.cocoon/xmlform\">";
	static final String X_Schematron="X_Schematron";
	static final String X_Workflow="X_Workflow";
	/**
	 * workflow access
	 */
	static final String[] WORKFLOW_ACCESSES={"Read-only","Hidden","Writable"};
	/**
	 * the default svn name in VersionSystem
	 */
	static final String DEFAULT_SVN="DEFAULT_SVN";
	
	static final String DEFAULT_CATEGORY_ROOT = "category";
	/**
	 * cross referencing init datacluster & datamodel
	 */
	public static final String CrossReferencing_datacluster="crossreferencing";
	public static final String CrossReferencing_datamodel="crossreferencing";	
	
	/**
	 * enum user access type of accessing MDM
	 */
	public static final String ACCESS_BYSTUDIO = "5A";
	public static final String ACCESS_BYWEBUI = "5B";
	public static final String ACCESS_BYCODE = "";
	
	/**
	 * enum user roles
	 */
	public static final String SYSTEM_ADMIN_ROLE = "System_Admin";
	public static final String SYSTEM_INTERACTIVE_ROLE = "System_Interactive";
	public static final String SYSTEM_WEB_ROLE = "System_Web";
	public static final String SYSTEM_VIEW_ROLE = "System_View";
}
