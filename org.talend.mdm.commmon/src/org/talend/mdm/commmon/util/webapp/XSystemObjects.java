package org.talend.mdm.commmon.util.webapp;

import java.util.HashMap;
import java.util.Map;

import org.talend.mdm.commmon.util.core.ICoreConstants;

public enum XSystemObjects {
//tom:datacluster/CONF;datacluster/Inbox;datacluster/Reporting;datacluster/UpdateReport;datacluster/SearchTemplate;datacluster/MDMItemsTrash
//tem:datacluster/PROVISIONING;datacluster/crossreferencing;datacluster/JCAAdapters
	// Data Clusters
	DC_JCAADAPTERS(XObjectType.DATA_CLUSTER, "JCAAdapters",true), 
	DC_INBOX(XObjectType.DATA_CLUSTER, "Inbox",false),
	//DC_BUG_TRACKING(XObjectType.DATA_CLUSTER, "Bug Tracking"), 
	DC_CONF(XObjectType.DATA_CLUSTER, "CONF",false), 
	//DC_MDMCONF(XObjectType.DATA_CLUSTER, "MDMCONF"), 
	DC_PROVISIONING(XObjectType.DATA_CLUSTER, "PROVISIONING",true), 
	DC_UPDATE_PREPORT(XObjectType.DATA_CLUSTER, "UpdateReport",false), 
	//DC_XTENTIS_COMMON_CONF(XObjectType.DATA_CLUSTER, "Xtentis Common Conf"), 
	DC_XTENTIS_COMMON_REPORTING(XObjectType.DATA_CLUSTER, "Reporting",false),
	DC_MDMITEMSTRASH(XObjectType.DATA_CLUSTER,"MDMItemsTrash",false),
	
	DC_SEARCHTEMPLATE(XObjectType.DATA_CLUSTER,"SearchTemplate",false),
	
	DC_CROSSREFERENCING(XObjectType.DATA_CLUSTER,ICoreConstants.CrossReferencing_datacluster,true),
	
	//Role
	
	//tom:role/Default_Admin;role/Default_User
	//tem:role/Default_Viewer
	ROLE_DEFAULT_ADMIN(XObjectType.ROLE,"Default_Admin",false),
	ROLE_DEFAULT_USER(XObjectType.ROLE,"Default_User",false),
	ROLE_DEFAULT_VIEWER(XObjectType.ROLE,"Default_Viewer",true), //readonly for everything, can't be modified
	// Data Models
	
	//tom:datamodel/CONF;datamodel/Reporting;datamodel/UpdateReport;datamodel/SearchTemplate;datamodel/XMLSCHEMA---
	//tem:datamodel/PROVISIONING;datamodel/crossreferencing
	//DM_BUG_TRACKING(XObjectType.DATA_MODEL, "Bug Tracking"), 
	DM_CONF(XObjectType.DATA_MODEL, "CONF",false), 
	DM_PROVISIONING(XObjectType.DATA_MODEL, "PROVISIONING",true), 
	//DM_REPORTING(XObjectType.DATA_MODEL, "REPORTING"), 
	DM_UPDATEREPORT(XObjectType.DATA_MODEL, "UpdateReport",false), 
	DM_XTENTIS_COMMON_CONF(XObjectType.DATA_MODEL, "XMLSCHEMA---",false), 
	DM_XTENTIS_COMMON_REPORTING(XObjectType.DATA_MODEL, "Reporting",false),
	
	DM_SEARCHTEMPLATE(XObjectType.DATA_MODEL,"SearchTemplate",false),
	
	Dm_CROSSREFERENCING(XObjectType.DATA_MODEL,ICoreConstants.CrossReferencing_datamodel,true),
	// Menus
	//tom:menu/Browse+items;menu/Browse+views;menu/ItemsTrash;menu/logging;menu/Reporting;menu/smtp;menu/UpdateReport;menu/Grouping+Hierarchy
	//tem:menu/Cross+Referencing;menu/Derived+Hierarchy;menu/Manage+users;menu/Service+Schedule;menu/SynchronizationAction;
	  //menu/SynchronizationItem;menu/Universe+Manager;menu/WorkflowTasks;menu/License;menu/Datastewardship
	M_BROWSE_ITEMS(XObjectType.MENU, "Browse items",false), 
	M_BROWSE_VIEWS(XObjectType.MENU, "Browse views",false), 
	M_MANAGER_USERS(XObjectType.MENU, "Manage users",true), 
	M_LICENSE(XObjectType.MENU, "License",true),
	M_REPORTING(XObjectType.MENU,"Reporting",false), 
	M_SYNCHRONIZATIONACTION(XObjectType.MENU,"SynchronizationAction",true), 
	M_SYNCHRONIZATIONPLAN(XObjectType.MENU,"SynchronizationItem",true),
	//M_DATA_CHANGES(XObjectType.MENU,"Data changes"),
	M_UPDATE_REPORT(XObjectType.MENU,"UpdateReport",false),
	M_SERVICE_SCHEDULE(XObjectType.MENU,"Service Schedule",true),
	M_LOGGING(XObjectType.MENU,"logging",false),
	M_ITEMSTRASH(XObjectType.MENU,"ItemsTrash",false),
	M_SMTP(XObjectType.MENU,"smtp",false),
	//M_HIERARCHICAL_VIEW(XObjectType.MENU,"Hierarchical View"),
	M_HIERARCHICAL_VIEW_GROUPING(XObjectType.MENU,"Grouping Hierarchy",false),
	M_HIERARCHICAL_VIEW_DERIVED(XObjectType.MENU,"Derived Hierarchy",true),
	M_UNIVERSEMANAGER(XObjectType.MENU,"Universe Manager",true),
	M_CROSSREFERENCING(XObjectType.MENU,"Cross Referencing",true),
	M_WORKFLOWTASKS(XObjectType.MENU,"WorkflowTasks",true),
	M_DATASTEWARDSHIP(XObjectType.MENU,"Datastewardship",true);
	
	XSystemObjects(int type, String name,boolean tem) {
		this.name = name;
		this.type = type;
		this.tem = tem;
	}

	XSystemObjects() {
	};

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	private String name;

	private int type;
	
	private boolean tem;

	public boolean isTem() {
		return tem;
	}

	public void setTem(boolean tem) {
		this.tem = tem;
	}

	// key is the type
	/**
	 * get all the elements
	 * 
	 * @return map
	 */
	public static Map<String, XSystemObjects> getXSystemObjects() {

		Map<String, XSystemObjects> map = new HashMap<String, XSystemObjects>();
		for (int i = 0; i < values().length; i++) {
			map.put(values()[i].getType()+"_"+String.valueOf(values()[i].getName()), values()[i]);
		}
		return map;
	}

	/**
	 * get the "type" elements
	 * 
	 * @param type
	 * @return map
	 */
	public static Map<String, XSystemObjects> getXSystemObjects(int type) {

		Map<String, XSystemObjects> map = new HashMap<String, XSystemObjects>();
		for (int i = 0; i < values().length; i++) {
			if (type == values()[i].getType())
				map.put(String.valueOf(values()[i].getName()), values()[i]);
		}
		return map;
	}
	
	/**
	 * @param type
	 * @param objectPK
	 * @return
	 */
	public static boolean isXSystemObject(int type,String objectPK) {

		Map<String, XSystemObjects> map = getXSystemObjects(type);
		
		return isXSystemObject(map,type,objectPK);
	}
	
	/**
	 * @param map
	 * @param type
	 * @param objectPK
	 * @return
	 */
	public static boolean isXSystemObject(Map<String, XSystemObjects> map,int type,String objectPK) {

		if(map.get(objectPK)!=null){
			return true;
		}
		return false;
	}

	/**
	 * chenk if the element is exist
	 * 
	 * @param type
	 * @param name
	 * @return boolean
	 */
	public static boolean isExist(int type, String name) {
		boolean is = false;
		for (int i = 0; i < values().length; i++) {
			if (type == values()[i].getType() && name.equals(values()[i].name)) {
				is = true;
				break;
			}
		}
		return is;
	}
	/**
	 * chenk if the element is exist
	 * 
	 * @param name
	 * @return boolean
	 */
	public static boolean isExist(String name) {
		boolean is = false;
		for (int i = 0; i < values().length; i++) {
			if (name.equals(values()[i].name)) {
				is = true;
				break;
			}
		}
		return is;
	}
	
	public static Map<String, XSystemObjects> getXSystemObjectsTOM(int type) {

		Map<String, XSystemObjects> map = new HashMap<String, XSystemObjects>();
		for (int i = 0; i < values().length; i++) {
			if (type == values()[i].getType()&& !values()[i].isTem())
				map.put(String.valueOf(values()[i].getName()), values()[i]);
		}
		return map;
	}
	
	
}
