/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package org.talend.mdm.commmon.metadata;

import org.talend.mdm.commmon.metadata.validation.ValidationFactory;
import org.talend.mdm.commmon.metadata.validation.ValidationRule;

import javax.xml.XMLConstants;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnresolvedFieldMetadata implements FieldMetadata {

    private final String fieldName;

    private boolean isKey;

    private final TypeMetadata declaringType;

    private final Map<String,Object> additionalData = new HashMap<String, Object>();

    private ComplexTypeMetadata containingType;

    public UnresolvedFieldMetadata(String fieldName, boolean key, ComplexTypeMetadata containingType) {
        this.fieldName = fieldName;
        this.isKey = key;
        this.containingType = containingType;
        this.declaringType = containingType;
    }

    @Override
    public String getName() {
        return fieldName;
    }

    @Override
    public boolean isKey() {
        return isKey;
    }

    @Override
    public TypeMetadata getType() {
        return new SimpleTypeMetadata(XMLConstants.W3C_XML_SCHEMA_NS_URI, Types.ANY_TYPE);
    }

    @Override
    public ComplexTypeMetadata getContainingType() {
        return containingType;
    }

    @Override
    public TypeMetadata getDeclaringType() {
        return declaringType;
    }

    @Override
    public List<String> getHideUsers() {
        return Collections.emptyList();
    }

    @Override
    public List<String> getWriteUsers() {
        return null;
    }

    @Override
    public boolean isMany() {
        return false;
    }

    @Override
    public boolean isMandatory() {
        return false;
    }

    @Override
    public void adopt(ComplexTypeMetadata metadata, MetadataRepository repository) {
    }

    @Override
    public FieldMetadata copy(MetadataRepository repository) {
        return this;
    }

    @Override
    public void setContainingType(ComplexTypeMetadata typeMetadata) {
        this.containingType = typeMetadata;
    }

    @Override
    public FieldMetadata freeze() {
        return this;
    }

    @Override
    public void promoteToKey() {
        isKey = true;
    }

    @Override
    public void validate(ValidationHandler handler) {
        ValidationFactory.getRule(this).perform(handler);
    }

    @Override
    public ValidationRule createValidationRule() {
        return ValidationFactory.getRule(this);
    }

    @Override
    public void setData(String key, Object data) {
        additionalData.put(key, data);
    }

    @Override
    public <X> X getData(String key) {
        return (X) additionalData.get(key);
    }

    @Override
    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
