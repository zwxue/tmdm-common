// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

/**
 * created by hwzhu on Sep 29, 2018 Detailled comment
 *
 */
public final class MDMXMLConstants {

    /**
     * <p>
     * Private constructor to prevent instantiation.
     * </p>
     */
    private MDMXMLConstants() {
    }

    public static final String FEATURE_DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";

    public static final String FEATURE_LOAD_EXTERNAL = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

    public static final String FEATURE_EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";

    public static final String FEATURE_EXTERNAL_PARAM_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";

    public static final String FEATURE_DEFER_NODE_EXP = "http://apache.org/xml/features/dom/defer-node-expansion";

    public static final String PROPERTY_IS_SUPPORT_EXT_ENTITY = "javax.xml.stream.isSupportingExternalEntities";

    public static final String PROPERTY_SCHEMAL_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    public static final String PROPERTY_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

    public static final String PROPERTY_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
}
