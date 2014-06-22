/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package org.talend.mdm.commmon.metadata;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.talend.mdm.commmon.metadata.validation.ValidationRule;

public class ContainedComplexTypeMetadata implements ComplexTypeMetadata {

    private ComplexTypeMetadata containedType;

    private FieldMetadata container;

    private boolean isFrozen;

    private ContainedComplexTypeMetadata(ComplexTypeMetadata containedType, FieldMetadata container) {
        this.containedType = containedType;
        this.container = container;
        if (containedType.getName().startsWith(MetadataRepository.ANONYMOUS_PREFIX)) {
            this.containedType = (ComplexTypeMetadata) containedType.copy();
            for (FieldMetadata field : this.containedType.getFields()) {
                field.setContainingType(this);
            }
        } else {
            this.containedType.declareUsage(this);
        }
    }

    public static ComplexTypeMetadata contain(ComplexTypeMetadata type, FieldMetadata containingField) {
        return new ContainedComplexTypeMetadata(type, containingField);
    }

    void finalizeUsage() {
        containedType = (ComplexTypeMetadata) containedType.copy();
        for (FieldMetadata field : containedType.getFields()) {
            field.setContainingType(this);
        }
        List<ComplexTypeMetadata> subTypes = new LinkedList<ComplexTypeMetadata>();
        for (ComplexTypeMetadata subType : containedType.getSubTypes()) {
            ComplexTypeMetadata copy = (ComplexTypeMetadata) subType.copy();
            ComplexTypeMetadata containedCopy = ContainedComplexTypeMetadata.contain(copy, container);
            for (FieldMetadata subTypeField : copy.getFields()) {
                subTypeField.setContainingType(containedCopy);
            }
            subTypes.add(containedCopy);
        }
        containedType.setSubTypes(subTypes);
        containedType = (ComplexTypeMetadata) containedType.freeze();
    }

    @Override
    public ComplexTypeMetadata getEntity() {
        return container.getContainingType().getEntity();
    }

    @Override
    public FieldMetadata getContainer() {
        return container;
    }

    @Override
    public void setContainer(FieldMetadata field) {
        container = field;
    }

    @Override
    public Collection<FieldMetadata> getKeyFields() {
        return containedType.getKeyFields();
    }

    @Override
    public void registerKey(FieldMetadata keyField) {
        containedType.registerKey(keyField);
    }

    @Override
    public FieldMetadata getField(String fieldName) {
        return containedType.getField(fieldName);
    }

    @Override
    public Collection<FieldMetadata> getFields() {
        return containedType.getFields();
    }

    @Override
    public void addField(FieldMetadata fieldMetadata) {
        containedType.addField(fieldMetadata);
    }

    @Override
    public List<String> getWriteUsers() {
        return containedType.getWriteUsers();
    }

    @Override
    public List<String> getHideUsers() {
        return containedType.getHideUsers();
    }

    @Override
    public List<String> getDenyCreate() {
        return containedType.getDenyCreate();
    }

    @Override
    public List<String> getDenyDelete(DeleteType type) {
        return containedType.getDenyDelete(type);
    }

    @Override
    public List<String> getWorkflowAccessRights() {
        return containedType.getWorkflowAccessRights();
    }

    @Override
    public String getSchematron() {
        return containedType.getSchematron();
    }

    @Override
    public List<FieldMetadata> getPrimaryKeyInfo() {
        return containedType.getPrimaryKeyInfo();
    }

    @Override
    public boolean hasField(String fieldName) {
        return containedType.hasField(fieldName);
    }

    @Override
    public Collection<ComplexTypeMetadata> getSubTypes() {
        return containedType.getSubTypes();
    }

    @Override
    public void registerSubType(ComplexTypeMetadata type) {
        containedType.registerSubType(type);
    }

    @Override
    public List<FieldMetadata> getLookupFields() {
        return containedType.getLookupFields();
    }

    @Override
    public void declareUsage(ContainedComplexTypeMetadata usage) {
    }

    @Override
    public List<ContainedComplexTypeMetadata> getUsages() {
        return containedType.getUsages();
    }

    @Override
    public void freezeUsages() {
    }

    @Override
    public void setSubTypes(List<ComplexTypeMetadata> subTypes) {
        containedType.setSubTypes(subTypes);
    }

    @Override
    public Collection<TypeMetadata> getSuperTypes() {
        return containedType.getSuperTypes();
    }

    @Override
    public void addSuperType(TypeMetadata superType) {
        containedType.addSuperType(superType);
    }

    @Override
    public String getName() {
        return containedType.getName();
    }

    @Override
    public void setName(String name) {
        containedType.setName(name);
    }

    @Override
    public String getNamespace() {
        return containedType.getNamespace();
    }

    @Override
    public boolean isAssignableFrom(TypeMetadata type) {
        return containedType.isAssignableFrom(type);
    }

    @Override
    public TypeMetadata copy() {
        return containedType.copy();
    }

    @Override
    public TypeMetadata copyShallow() {
        return containedType.copyShallow();
    }

    @Override
    public TypeMetadata freeze() {
        if (!isFrozen) {
            isFrozen = true;
            containedType = (ComplexTypeMetadata) containedType.freeze();
            if (containedType != null) {
                containedType.declareUsage(this);
            }
        }
        return this;
    }

    @Override
    public boolean isInstantiable() {
        return containedType.isInstantiable();
    }

    @Override
    public void setInstantiable(boolean isInstantiable) {
        containedType.setInstantiable(isInstantiable);
    }

    @Override
    public boolean isFrozen() {
        return containedType.isFrozen();
    }

    @Override
    public void validate(ValidationHandler handler) {
        containedType.validate(handler);
    }

    @Override
    public ValidationRule createValidationRule() {
        return containedType.createValidationRule();
    }

    @Override
    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public void setData(String key, Object data) {
        containedType.setData(key, data);
    }

    @Override
    public <X> X getData(String key) {
        return containedType.getData(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ContainedComplexTypeMetadata)) {
            return false;
        }

        ContainedComplexTypeMetadata that = (ContainedComplexTypeMetadata) o;

        if (!container.getContainingType().equals(that.container.getContainingType())) {
            return false;
        }
        if (!container.getName().equals(that.container.getName())) {
            return false;
        }
        if (!containedType.getName().equals(that.containedType.getName())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = containedType.getName().hashCode();
        result = 31 * result + container.getContainingType().hashCode();
        result = 31 * result + container.getName().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Contained([" + containedType.getNamespace() + ':' + containedType.getName() + "], container="
                + container.getName() + ')';
    }
}
