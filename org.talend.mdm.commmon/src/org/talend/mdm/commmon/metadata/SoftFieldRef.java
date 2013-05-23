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

package org.talend.mdm.commmon.metadata;

import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class SoftFieldRef implements FieldMetadata {

    private final MetadataRepository repository;

    private final SoftFieldRef containingField;

    private final TypeMetadata containingType;

    private final String fieldName;

    private final Map<String, Object> additionalData = new HashMap<String, Object>();

    private FieldMetadata frozenField;

    public SoftFieldRef(MetadataRepository metadataRepository, String fieldName, TypeMetadata containingType) {
        this.repository = metadataRepository;
        this.containingType = containingType;
        this.fieldName = fieldName;
        this.containingField = null;
    }

    public SoftFieldRef(MetadataRepository metadataRepository, String fieldName, SoftFieldRef containingField) {
        this.repository = metadataRepository;
        this.containingField = containingField;
        this.containingType = null;
        this.fieldName = fieldName;
    }

    private FieldMetadata getField() {
        if (frozenField == null) {
            DefaultValidationHandler handler = new DefaultValidationHandler();
            freeze(handler);
            handler.end();
        }
        return frozenField;
    }

    @Override
    public synchronized void setData(String key, Object data) {
        additionalData.put(key, data);
    }

    @Override
    public <X> X getData(String key) {
        return (X) additionalData.get(key);
    }

    @Override
    public String getName() {
        return fieldName;
    }

    @Override
    public boolean isKey() {
        return getField().isKey();
    }

    @Override
    public TypeMetadata getType() {
        return getField().getType();
    }

    @Override
    public ComplexTypeMetadata getContainingType() {
        return (ComplexTypeMetadata) containingType;
    }

    @Override
    public void setContainingType(ComplexTypeMetadata typeMetadata) {
        getField().setContainingType(typeMetadata);
    }

    @Override
    public FieldMetadata freeze(ValidationHandler handler) {
        if (frozenField != null) {
            return frozenField;
        }
        ComplexTypeMetadata type;
        if (containingType != null) {
            type = repository.getComplexType(containingType.getName());
        } else {
            type = (ComplexTypeMetadata) containingField.getType();
        }
        if (type == null) {
            Integer line = this.getData(MetadataRepository.XSD_LINE_NUMBER);
            Integer column = this.getData(MetadataRepository.XSD_COLUMN_NUMBER);
            Element xmlElement = this.getData(MetadataRepository.XSD_DOM_ELEMENT);
            handler.error(this, this.getContainingType().getName() + "/" + this.getName() + " is a nonexistent target.", xmlElement, line, column, ValidationError.TYPE_DOES_NOT_EXIST);  //$NON-NLS-1$//$NON-NLS-2$
            return null;
        }
        frozenField = type.getField(fieldName);
        
        Set<Map.Entry<String,Object>> data = additionalData.entrySet();
        for (Map.Entry<String, Object> currentData : data) {
            frozenField.setData(currentData.getKey(), currentData.getValue());
        }
        return frozenField;
    }

    @Override
    public void promoteToKey(ValidationHandler handler) {
        getField().promoteToKey(handler);
    }

    @Override
    public void validate(ValidationHandler handler) {
        // Get line and column numbers
        Integer lineNumberObject = (Integer) additionalData.get(MetadataRepository.XSD_LINE_NUMBER);
        Integer columnNumberObject = (Integer) additionalData.get(MetadataRepository.XSD_COLUMN_NUMBER);
        Element xmlElement = (Element) additionalData.get(MetadataRepository.XSD_DOM_ELEMENT);

        TypeMetadata validationType;
        if (containingType != null) {
            validationType = containingType;
        } else {
            int errorCount = handler.getErrorCount();
            containingField.validate(new LocationOverride(containingField, handler, xmlElement, lineNumberObject, columnNumberObject));
            if (handler.getErrorCount() > errorCount) {
                return;
            }
            validationType = containingField.getType();
        }
        if (lineNumberObject == null) {
            lineNumberObject = validationType.<Integer>getData(MetadataRepository.XSD_LINE_NUMBER);
        }
        if (columnNumberObject == null) {
            columnNumberObject = validationType.<Integer>getData(MetadataRepository.XSD_COLUMN_NUMBER);
        }
        if (lineNumberObject == null) {
            lineNumberObject = -1;
        }
        if (columnNumberObject == null) {
            columnNumberObject = -1;
        }
        TypeMetadata type = repository.getComplexType(validationType.getName());
        if (type == null) {
            type = repository.getNonInstantiableType(validationType.getNamespace(), validationType.getName());
        }
        if (type == null) {
            handler.error(this,
                    "Type '" + validationType.getName() + "' does not exist.",
                    xmlElement,
                    lineNumberObject,
                    columnNumberObject,
                    ValidationError.TYPE_DOES_NOT_EXIST);
            return;
        }
        if (fieldName != null) {
            ComplexTypeMetadata complexTypeMetadata = (ComplexTypeMetadata) validationType;
            if (!complexTypeMetadata.hasField(fieldName)) {
                handler.error(this,
                        "Type '" + validationType.getName() + "' does not own field '" + fieldName + "'.",
                        xmlElement,
                        lineNumberObject,
                        columnNumberObject,
                        ValidationError.TYPE_DOES_NOT_OWN_FIELD);
            }
        }
    }

    @Override
    public TypeMetadata getDeclaringType() {
        return getField().getDeclaringType();
    }

    @Override
    public void adopt(ComplexTypeMetadata metadata, MetadataRepository repository) {
        FieldMetadata copy = getField().copy(this.repository);
        copy.setContainingType(metadata);
        metadata.addField(copy);
    }

    @Override
    public FieldMetadata copy(MetadataRepository repository) {
        if (containingType == null) {
            return new SoftFieldRef(repository, fieldName, containingField);
        } else {
            return new SoftFieldRef(repository, fieldName, containingType.copy(repository));
        }
    }

    @Override
    public List<String> getHideUsers() {
        return getField().getHideUsers();
    }

    @Override
    public List<String> getWriteUsers() {
        return getField().getWriteUsers();
    }

    @Override
    public boolean isMany() {
        return getField().isMany();
    }

    @Override
    public boolean isMandatory() {
        return getField().isMandatory();
    }

    @Override
    public <T> T accept(MetadataVisitor<T> visitor) {
        return getField().accept(visitor);
    }

    @Override
    public String toString() {
        if (containingType != null) {
            return containingType.toString() + "/" + fieldName; //$NON-NLS-1$
        } else {
            return containingField.toString() + "/" + fieldName; //$NON-NLS-1$
        }
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof FieldMetadata && getField().equals(o);
    }
}
