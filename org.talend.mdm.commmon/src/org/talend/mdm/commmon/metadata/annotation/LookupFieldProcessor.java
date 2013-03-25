/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package org.talend.mdm.commmon.metadata.annotation;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.util.XSDParser;
import org.talend.mdm.commmon.metadata.*;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LookupFieldProcessor implements XmlSchemaAnnotationProcessor {

    @Override
    public void process(MetadataRepository repository, ComplexTypeMetadata type, XSDAnnotation annotation, XmlSchemaAnnotationProcessorState state) {
        if (annotation != null) {
            EList<Element> annotations = annotation.getApplicationInformation();
            List<FieldMetadata> lookupFields = new LinkedList<FieldMetadata>();
            for (Element appInfo : annotations) {
                if ("X_Lookup_Field".equals(appInfo.getAttribute("source"))) { //$NON-NLS-1$
                    lookupFields.add(handleLookupField(repository, state, appInfo));
                }
            }
            state.setLookupFields(lookupFields);
        }
    }

    private FieldMetadata handleLookupField(MetadataRepository repository, XmlSchemaAnnotationProcessorState state, Element appInfo) {
        state.markAsReference();
        String path = appInfo.getTextContent();
        String[] typeAndFields = path.split("/"); //$NON-NLS-1$
        String userNamespace = repository.getUserNamespace();
        state.setFieldType(new SoftTypeRef(repository, userNamespace, typeAndFields[0].trim(), true));
        state.setReferencedType(new SoftTypeRef(repository, userNamespace, typeAndFields[0].trim(), true)); // Only reference instantiable types.
        List<String> fieldList = Arrays.asList(typeAndFields);
        FieldMetadata fieldMetadata = createFieldReference(repository, state.getFieldType(), fieldList, appInfo);
        if (fieldMetadata == null) {
            throw new IllegalArgumentException("Path '" + path + "' is not supported.");
        }
        return fieldMetadata;
    }

    private static FieldMetadata createFieldReference(MetadataRepository repository, TypeMetadata rootTypeName, List<String> path, Element appInfo) {
        Queue<String> processQueue = new LinkedList<String>(path);
        processQueue.poll(); // Remove first (first is type name and required additional processing for '.')
        SoftTypeRef currentType = new SoftTypeRef(repository, rootTypeName.getNamespace(), rootTypeName.getName(), true);
        FieldMetadata fieldMetadata;
        if (processQueue.isEmpty()) {
            // handle case where referenced type "Type" only and not "Type/Id" (way to reference composite id).
            fieldMetadata = new SoftIdFieldRef(repository, rootTypeName.getName());
        } else {
            SoftFieldRef previous = null;
            SoftFieldRef newField = null;
            while (!processQueue.isEmpty()) {
                // In case of "PersonneMorale/IdPersonneMorale[PersonneMorale/ListeRole/Role/IdRole=DEP]", skip xpath condition.
                String currentField = processQueue.poll().trim();
                if (currentField.indexOf('[') > 0) {
                    currentField = StringUtils.substringBefore(currentField, "["); //$NON-NLS-1$
                    processQueue.clear(); // don't process any further the xpath expression.
                }
                if (previous == null) {
                    newField = new SoftFieldRef(repository, currentField, currentType);
                } else {
                    newField = new SoftFieldRef(repository, currentField, previous);
                }
                previous = newField;
            }
            fieldMetadata = newField;
        }
        if (fieldMetadata == null) {
            throw new IllegalStateException();
        }
        fieldMetadata.setData(MetadataRepository.XSD_LINE_NUMBER, XSDParser.getStartLine(appInfo));
        fieldMetadata.setData(MetadataRepository.XSD_COLUMN_NUMBER, XSDParser.getStartColumn(appInfo));
        fieldMetadata.setData(MetadataRepository.XSD_DOM_ELEMENT, appInfo);
        return fieldMetadata;
    }
}
