// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.commmon.util.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.jxpath.JXPathContext;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.util.core.ICoreConstants;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.commmon.util.core.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * It is the integration point between MDM & Bonita, We will pass in the workflow a single MDM Context object. It will
 * include the following some infos: <br>
 * <li>Context info (host, port etc) <li>Update Report info <li>The full entity xml record <li>The XSD Schema info
 */
public class MDMContext implements Serializable {

    private static final long serialVersionUID = -115796295649644256L;

    // Context info
    private String host;

    private String port;

    private String universe;

    private String dataContainer;

    private String dataModel;

    private String username;

    private String password;

    private String concept;

    private HashSet<String> roles;

    private String processId;

    private String processVersion;

    // Update Report info
    private String updateReport;

    // Entity record (Document object)
    private transient Document entityDocument;

    // Entity xml
    private String entityXml;

    // MetadataRepository
    private transient MetadataRepository repository;

    // DataModel schema
    private String xsdSchema;

    /**
     * Getter for host.
     * 
     * @return the host
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Sets the host.
     * 
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Getter for port.
     * 
     * @return the port
     */
    public String getPort() {
        return this.port;
    }

    /**
     * Sets the port.
     * 
     * @param port the port to set
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * Getter for universe.
     * 
     * @return the universe
     */
    public String getUniverse() {
        return this.universe;
    }

    /**
     * Sets the universe.
     * 
     * @param universe the universe to set
     */
    public void setUniverse(String universe) {
        this.universe = universe;
    }

    /**
     * Getter for dataContainer.
     * 
     * @return the dataContainer
     */
    public String getDataContainer() {
        return this.dataContainer;
    }

    /**
     * Sets the dataContainer.
     * 
     * @param dataContainer the dataContainer to set
     */
    public void setDataContainer(String dataContainer) {
        this.dataContainer = dataContainer;
    }

    /**
     * Getter for dataModel.
     * 
     * @return the dataModel
     */
    public String getDataModel() {
        return this.dataModel;
    }

    /**
     * Sets the dataModel.
     * 
     * @param dataModel the dataModel to set
     */
    public void setDataModel(String dataModel) {
        this.dataModel = dataModel;
    }

    /**
     * Getter for username.
     * 
     * @return the username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Sets the username.
     * 
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Getter for password.
     * 
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets the password.
     * 
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the concept.
     * 
     * @param concept the concept to set
     */
    public void setConcept(String concept) {
        this.concept = concept;
    }

    /**
     * Getter for roles.
     * 
     * @return the roles
     */
    public HashSet<String> getRoles() {
        return this.roles;
    }

    /**
     * Sets the roles.
     * 
     * @param roles the roles to set
     */
    public void setRoles(HashSet<String> roles) {
        this.roles = roles;
    }

    /**
     * Getter for processId.
     * 
     * @return the processId
     */
    public String getProcessId() {
        return this.processId;
    }

    /**
     * Sets the processId.
     * 
     * @param processId the processId to set
     */
    public void setProcessId(String processId) {
        this.processId = processId;
    }

    /**
     * Getter for processVersion.
     * 
     * @return the processVersion
     */
    public String getProcessVersion() {
        return this.processVersion;
    }

    /**
     * Sets the processVersion.
     * 
     * @param processVersion the processVersion to set
     */
    public void setProcessVersion(String processVersion) {
        this.processVersion = processVersion;
    }

    /**
     * Getter for updateReport.
     * 
     * @return the updateReport
     */
    public String getUpdateReport() {
        return this.updateReport;
    }

    /**
     * Sets the updateReport.
     * 
     * @param updateReport the updateReport to set
     */
    public void setUpdateReport(String updateReport) {
        this.updateReport = updateReport;
    }

    /**
     * Getter for entityDocument.
     * 
     * @return the entityDocument
     */
    public Document getEntityDocument() {
        return this.entityDocument;
    }

    /**
     * Sets the entityDocument.
     * 
     * @param entityDocument the entityDocument to set
     */
    public void setEntityDocument(Document entityDocument) {
        this.entityDocument = entityDocument;
    }

    /**
     * Getter for repository.
     * 
     * @return the repository
     */
    public MetadataRepository getRepository() {
        return this.repository;
    }

    /**
     * Sets the repository.
     * 
     * @param repository the repository to set
     */
    public void setRepository(MetadataRepository repository) {
        this.repository = repository;
    }

    /**
     * Getter for xsdSchema.
     * 
     * @return the xsdSchema
     */
    public String getXsdSchema() {
        return this.xsdSchema;
    }

    /**
     * Sets the xsdSchema.
     * 
     * @param xsdSchema the xsdSchema to set
     */
    public void setXsdSchema(String xsdSchema) {
        this.xsdSchema = xsdSchema;
    }

    /**
     * Getter for entityXml.
     * 
     * @return the entityXml
     */
    public String getEntityXml() {
        return this.entityXml;
    }

    /**
     * Sets the entityXml.
     * 
     * @param entityXml the entityXml to set
     */
    public void setEntityXml(String entityXml) {
        this.entityXml = entityXml;
    }

    private static JXPathContext getXPathContext(Node node) {
        JXPathContext jxpContext = JXPathContext.newContext(node);
        jxpContext.setLenient(true);
        jxpContext.registerNamespace("xsi", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI); //$NON-NLS-1$
        jxpContext.registerNamespace("tmdm", ICoreConstants.TALEND_NAMESPACE); //$NON-NLS-1$
        return jxpContext;
    }

    /**
     * Check Workflow Access Rights for a element (Workflow Access rights: Writable, Read-only, Hidden)
     * 
     * @param xpath the element's xpath
     * @param isCheckWritable Check element writable access rights when it is 'true', otherwise check the
     * readable(Read-only or Writable) access rights.
     * @return
     */
    public boolean checkWorkflowAccessRights(String xpath, boolean isCheckWritable) {
        // 1. super Admin
        if (roles != null && roles.contains(ICoreConstants.ADMIN_PERMISSION)) {
            return true;
        }
        if (MDMConfiguration.getAdminUser().equals(username)) {
            return true;
        }
        // 2. get FieldMetadata by xpath
        FieldMetadata fieldMetadata = getFieldMetadataByXpath(xpath);
        // 3. check FieldMetadata workflow access rights by current user roles and processId_processVersion
        List<String> workflowAccessRights = fieldMetadata.getWorkflowAccessRights();
        if (workflowAccessRights != null) {
            if (!isCheckWritable) {
                // check readable (Own Read-only or Writable access rights)
                for (String role : roles) {
                    // First check hidden
                    String currentUserWorkflowRole = role + processId + "_" + processVersion; //$NON-NLS-1$
                    if (workflowAccessRights.contains(currentUserWorkflowRole + "#Hidden")) { //$NON-NLS-1$
                        continue;
                    }
                    // Second check read-only
                    if (workflowAccessRights.contains(currentUserWorkflowRole + "#Read-only")) { //$NON-NLS-1$
                        return true;
                    }
                    // Last check writable
                    if (workflowAccessRights.contains(currentUserWorkflowRole + "#Writable")) { //$NON-NLS-1$
                        return true;
                    }
                }
            } else {
                // check writable
                for (String role : roles) {
                    // First check hidden
                    String currentUserWorkflowRole = role + processId + "_" + processVersion; //$NON-NLS-1$
                    if (workflowAccessRights.contains(currentUserWorkflowRole + "#Hidden")) { //$NON-NLS-1$
                        continue;
                    }
                    // Second check read-only
                    if (workflowAccessRights.contains(currentUserWorkflowRole + "#Read-only")) { //$NON-NLS-1$
                        continue;
                    }
                    // Last check writable
                    if (workflowAccessRights.contains(currentUserWorkflowRole + "#Writable")) { //$NON-NLS-1$
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private FieldMetadata getFieldMetadataByXpath(String xpath) {
        String elementXpath = xpath;
        // 1. clear prefix
        if (xpath.startsWith("//")) { //$NON-NLS-1$
            elementXpath = xpath.substring(2);
        } else if (xpath.startsWith("/")) { //$NON-NLS-1$
            elementXpath = xpath.substring(1);
        }
        // 2. get element metadata by xpath
        ComplexTypeMetadata complexType = repository.getComplexType(concept);
        // it need to change ComplexTypeMetadataImpl see TMDM-5089: unable to parse the resuable type xpath (e.g:
        // Employee/Address[@xsi:type="CNAddressType"]/Province)
        return complexType.getField(elementXpath);
    }

    /**
     * getValue by element's xpath
     * 
     * @param xpath the element's xpath
     * @return
     */
    public Object getValue(String xpath) {
        // 1. Check entityXml
        if (entityXml == null) {
            throw new RuntimeException("The entityXml can not be null, MDMContext should include the full entity record"); //$NON-NLS-1$
        }
        initEntityInfo();
        // 2. Check Workflow access rights
        boolean hasAuthorization = checkWorkflowAccessRights(xpath, false);
        if (!hasAuthorization) {
            throw new RuntimeException("Unable to read the element for current user = '" + username + "'"); //$NON-NLS-1$ //$NON-NLS-2$;
        }

        JXPathContext jxpContext = getXPathContext(entityDocument);
        // TODO in order to populate bonita form widget,it need to think about element's dataType and objectType.
        // TODO getFieldMetaByXpath, it can know the element's dataType, so we can know the follow Field type.
        // TODO and then build a bonita form widget objectType by field type and value
        // 1. FK
        // 2. ComplexType (reusableType), it need remove original node, and then add it by index
        // 3. Enumeration
        // 4. Multilingual
        // 5. Date DateTime
        // 6. Number
        // 7. Url
        // 8. Picture
        // 9. UUID AUTO_INCREMENT
        // 10.Others (String)
        return jxpContext.getValue(xpath);
    }

    /**
     * setElementValue according to the element's xpath and value
     * 
     * @param xpath the element's xpath
     * @param value the element's value
     */
    public void setValue(String xpath, Object value) {
        if (entityXml == null) {
            throw new RuntimeException("entityXml can not be null, MDMContext should include the full entity record"); //$NON-NLS-1$
        }
        initEntityInfo();
        boolean hasAuthorization = checkWorkflowAccessRights(xpath, true);
        if (!hasAuthorization) {
            throw new RuntimeException("Unable to modify the element for current user = '" + username + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        JXPathContext jxpContext = getXPathContext(entityDocument);
        // TODO according to bonita form widget's return value, it need to think about the value conversion by dataType
        jxpContext.setValue(xpath, value);
    }

    /**
     * TODO only display the process info, maybe it need to display all of context info.
     */
    @Override
    public String toString() {
        return this.processId + "_" + this.processVersion; //$NON-NLS-1$
    }

    /**
     * Send the MDMContext object to workflow engine, it should be a serailizable String but not POJO. so the method
     * serialize MDMContext object to a String object. (@see RuntimeAPI.instantiateProcess(ProcessDefinitionUUID
     * processUUID, Map<String, Object> variables))
     * 
     * @return a String object
     * @throws IOException
     */
    public String serialize() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream ois = new ObjectOutputStream(bos);
        ois.writeObject(this);
        return new BASE64Encoder().encode(bos.toByteArray());
    }

    /**
     * Deserialize a String to MDMContext
     * 
     * @param base64String a bese64 encoded string
     * @return MDMContext object
     * @throws Exception
     */
    public static MDMContext deserialize(String base64String) throws Exception {
        byte[] bytes = new BASE64Decoder().decodeBuffer(base64String);
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        MDMContext mdmContext = (MDMContext) ois.readObject();
        mdmContext.initEntityInfo();
        return mdmContext;
    }

    /**
     * Init entityDocument and repository
     * 
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    private void initEntityInfo() {
        try {
            if (entityDocument == null) {
                this.setEntityDocument(XMLUtils.parse(this.getEntityXml()));
                MetadataRepository _repository = new MetadataRepository();
                ByteArrayInputStream is = new ByteArrayInputStream(this.getXsdSchema().getBytes("UTF-8")); //$NON-NLS-1$
                _repository.load(is);
                this.setRepository(_repository);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize the entityDocument and repository."); //$NON-NLS-1$
        }

    }

}