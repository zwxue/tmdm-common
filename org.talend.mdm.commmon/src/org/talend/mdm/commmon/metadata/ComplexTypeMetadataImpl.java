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

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.metadata.validation.ValidationFactory;
import org.talend.mdm.commmon.metadata.validation.ValidationRule;

import java.util.*;

/**
 * Default implementation for a MDM entity type (i.e. "complex" type).
 */
public class ComplexTypeMetadataImpl extends MetadataExtensions implements ComplexTypeMetadata {

    private final String nameSpace;

    private final List<String> allowWrite;

    private final Map<String, FieldMetadata> fieldMetadata = new LinkedHashMap<String, FieldMetadata>();

    private final Map<String, FieldMetadata> fieldPathCache = new HashMap<String, FieldMetadata>();

    private final Map<String, FieldMetadata> keyFields = new LinkedHashMap<String, FieldMetadata>();

    private final Collection<TypeMetadata> superTypes = new HashSet<TypeMetadata>();

    private final List<String> denyCreate;

    private final List<String> hideUsers;

    private final List<String> logicalDelete;

    private final String schematron;

    private final List<String> physicalDelete;

    private final Collection<ComplexTypeMetadata> subTypes = new HashSet<ComplexTypeMetadata>();

    private List<FieldMetadata> lookupFields;

    private boolean isInstantiable;

    private List<FieldMetadata> primaryKeyInfo;

    private String name;

    private boolean isFrozen;
    
    private final List<String> workflowAccessRights;

    private FieldMetadata containingField;

    private List<ContainedComplexTypeMetadata> usages = new ArrayList<ContainedComplexTypeMetadata>();

    public ComplexTypeMetadataImpl(String nameSpace, String name, boolean instantiable) {
        this(nameSpace,
                name,
                Collections.<String>emptyList(),
                Collections.<String>emptyList(),
                Collections.<String>emptyList(),
                Collections.<String>emptyList(),
                Collections.<String>emptyList(),
                StringUtils.EMPTY,
                Collections.<FieldMetadata>emptyList(),
                Collections.<FieldMetadata>emptyList(),
                instantiable,
                Collections.<String>emptyList());
    }

    public ComplexTypeMetadataImpl(String nameSpace,
                                   String name,
                                   List<String> allowWrite,
                                   List<String> denyCreate,
                                   List<String> hideUsers,
                                   List<String> physicalDelete,
                                   List<String> logicalDelete,
                                   String schematron,
                                   List<FieldMetadata> primaryKeyInfo,
                                   List<FieldMetadata> lookupFields,
                                   boolean instantiable,
                                   List<String> workflowAccessRights) {
        this.name = name;
        this.nameSpace = nameSpace;
        this.allowWrite = allowWrite;
        this.denyCreate = denyCreate;
        this.hideUsers = hideUsers;
        this.physicalDelete = physicalDelete;
        this.logicalDelete = logicalDelete;
        this.schematron = schematron;
        this.primaryKeyInfo = primaryKeyInfo;
        this.lookupFields = lookupFields;
        this.isInstantiable = instantiable;
        this.workflowAccessRights = workflowAccessRights;
    }

    public void addSuperType(TypeMetadata superType) {
        if (isFrozen) {
            throw new IllegalStateException("Type '" + name + "' is frozen and can not be modified.");
        }
        superTypes.add(superType);
    }

    public Collection<TypeMetadata> getSuperTypes() {
        return Collections.unmodifiableCollection(superTypes);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (isFrozen) {
            throw new IllegalStateException("Cannot change name after type was frozen.");
        }
        this.name = name;
    }

    public String getNamespace() {
        return nameSpace;
    }

    public FieldMetadata getField(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            throw new IllegalArgumentException("Field name can not be null nor empty.");
        }
        if (fieldName.indexOf('/') < 0) {
            return fieldMetadata.get(fieldName); // Shortcut for direct look up for a field (no hierarchy involved).
        } else {
            if (fieldPathCache.containsKey(fieldName)) {
                return fieldPathCache.get(fieldName);
            }
            StringTokenizer tokenizer = new StringTokenizer(fieldName, "/"); //$NON-NLS-1$
            String firstFieldName = tokenizer.nextToken();
            FieldMetadata currentField = fieldMetadata.get(firstFieldName);
            // think about reusable type, e.g: Employee/Address[@xsi:type="CNAddressType"]/Province
            if (firstFieldName.contains("xsi:type")) { //$NON-NLS-1$
                currentField = fieldMetadata.get(StringUtils.substringBefore(firstFieldName, "[@xsi:type")); //$NON-NLS-1$
            }
            if (currentField == null) { // Look in super types if it wasn't found in current type.
                for (TypeMetadata superType : superTypes) {
                    if (superType instanceof ComplexTypeMetadata) {
                        if (((ComplexTypeMetadata) superType).hasField(firstFieldName)) {
                            currentField = ((ComplexTypeMetadata) superType).getField(firstFieldName);
                            break;
                        }
                    } else {
                        throw new IllegalStateException("No support for look up of fields in simple types.");
                    }
                }
            }
            if (currentField == null) {
                throw new IllegalArgumentException("Type '" + getName() + "' does not own field '" + firstFieldName + "'");
            }
            if (tokenizer.hasMoreTokens()) {
                ComplexTypeMetadata currentType = (ComplexTypeMetadata) currentField.getType();
                if (firstFieldName.contains("xsi:type")) { //$NON-NLS-1$
                    String reusableTypeName = StringUtils.substringAfter(firstFieldName, "@xsi:type").replace('=', ' ').replace(']', ' ').trim(); //$NON-NLS-1$
                    if (reusableTypeName.isEmpty()) {
                        throw new IllegalArgumentException("Reusable type could not be null for fieldName '" + fieldName + "'"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    if (!currentType.getName().equals(reusableTypeName)) { // Look real type in sub types
                        boolean foundRealType = false;
                        for (TypeMetadata subType : currentType.getSubTypes()) {
                            if (subType instanceof ComplexTypeMetadata && subType.getName().equals(reusableTypeName)) {
                                currentType = (ComplexTypeMetadata) subType;
                                foundRealType = true;
                                break;
                            }
                        }
                        if (!foundRealType) {
                            throw new IllegalArgumentException("Type '" + reusableTypeName + "' could not be found."); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    }
                }
                List<String> pathElements = new ArrayList<String>();
                while (tokenizer.hasMoreTokens()) {
                    pathElements.add(tokenizer.nextToken());
                }
                currentField = findField(currentType, pathElements, 0);
                if (currentField == null) {
                    throw new IllegalArgumentException("Type '" + name + "' does not contain field '" + fieldName + "'.");
                }
            }
            fieldPathCache.put(fieldName, currentField);
            return currentField;
        }
    }

    private static FieldMetadata findField(ComplexTypeMetadata type, List<String> pathElements, int level) {
        FieldMetadata field = type.getField(pathElements.get(level));
        if (field == null) {
            for (ComplexTypeMetadata subType : type.getSubTypes()) {
                field = findField(subType, pathElements, level);
                if (field != null) {
                    return field;
                }
            }
            return null;
        } else {
            if (level == pathElements.size() - 1) {
                return field;
            } else {
                return findField((ComplexTypeMetadata) field.getType(), pathElements, level + 1);
            }
        }
    }

    public boolean isInstantiable() {
        return isInstantiable;
    }

    @Override
    public boolean isFrozen() {
        return isFrozen;
    }

    @Override
    public void validate(ValidationHandler handler) {
        ValidationFactory.getRule(this).perform(handler);
        // Validate all fields.
        for (FieldMetadata value : fieldMetadata.values()) {
            ValidationFactory.getRule(value).perform(handler);
        }
    }

    @Override
    public ValidationRule createValidationRule() {
        return ValidationFactory.getRule(this);
    }

    public void setInstantiable(boolean isInstantiable) {
        if (isFrozen) {
            throw new IllegalStateException("Type '" + name + "' is frozen and can not be modified.");
        }
        this.isInstantiable = isInstantiable;
    }

    @Override
    public ComplexTypeMetadata getEntity() {
        if (getContainer() == null) {
            return this;
        } else {
            return getContainer().getContainingType().getEntity();
        }
    }

    @Override
    public FieldMetadata getContainer() {
        return containingField;
    }

    @Override
    public void setContainer(FieldMetadata field) {
        this.containingField = field;
    }

    public Collection<FieldMetadata> getKeyFields() {
        return Collections.unmodifiableCollection(keyFields.values());
    }

    public Collection<FieldMetadata> getFields() {
        return Collections.unmodifiableCollection(fieldMetadata.values());
    }

    public boolean isAssignableFrom(TypeMetadata type) {
        // Check one level of inheritance
        Collection<TypeMetadata> superTypes = getSuperTypes();
        for (TypeMetadata superType : superTypes) {
            if (type.getName().equals(superType.getName())) {
                return true;
            }
        }
        // Checks in type inheritance hierarchy.
        for (TypeMetadata superType : superTypes) {
            if (superType.isAssignableFrom(type)) {
                return true;
            }
        }
        return getName().equals(type.getName());
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return '[' + nameSpace + ':' + name + ']';
    }

    public void addField(FieldMetadata fieldMetadata) {
        if (isFrozen) {
            throw new IllegalStateException("Type '" + name + "' is frozen and can not be modified.");
        }
        if (fieldMetadata == null) {
            throw new IllegalArgumentException("Field can not be null.");
        }
        this.fieldMetadata.put(fieldMetadata.getName(), fieldMetadata);
        if (fieldMetadata.isKey()) {
            registerKey(fieldMetadata);
        }
    }

    public void registerKey(FieldMetadata keyField) {
        if (keyField == null) {
            throw new IllegalArgumentException("Key field can not be null.");
        }
        keyFields.put(keyField.getName(), keyField);
    }

    public ComplexTypeMetadata copy() {
        ComplexTypeMetadataImpl copy = new ComplexTypeMetadataImpl(getNamespace(),
                getName(),
                allowWrite,
                denyCreate,
                hideUsers,
                physicalDelete,
                logicalDelete,
                schematron,
                primaryKeyInfo,
                Collections.<FieldMetadata>emptyList(),
                isInstantiable,
                workflowAccessRights);
        Collection<FieldMetadata> fields = getFields();
        for (FieldMetadata field : fields) {
            copy.addField(field.copy());
        }
        for (TypeMetadata superType : superTypes) {
            copy.addSuperType(superType);
            if (superType instanceof ComplexTypeMetadata) {
                ((ComplexTypeMetadata) superType).registerSubType(copy);
            }
        }
        for (ComplexTypeMetadata subType : subTypes) {
            copy.subTypes.add(subType);
        }
        Collection<FieldMetadata> typeKeyFields = getKeyFields();
        for (FieldMetadata typeKeyField : typeKeyFields) {
            copy.registerKey(typeKeyField.copy());
        }
        copy.isFrozen = false;
        return copy;
    }

    public TypeMetadata copyShallow() {
        return new ComplexTypeMetadataImpl(getNamespace(),
                getName(),
                allowWrite,
                denyCreate,
                hideUsers,
                physicalDelete,
                logicalDelete,
                schematron,
                primaryKeyInfo,
                Collections.<FieldMetadata>emptyList(), isInstantiable, workflowAccessRights);
    }

    public List<String> getWriteUsers() {
        return Collections.unmodifiableList(allowWrite);
    }

    public List<String> getDenyCreate() {
        return Collections.unmodifiableList(denyCreate);
    }

    public List<String> getHideUsers() {
        return Collections.unmodifiableList(hideUsers);
    }
    
    public List<String> getWorkflowAccessRights() {
        return Collections.unmodifiableList(workflowAccessRights);
    }

    public List<String> getDenyDelete(DeleteType type) {
        switch (type) {
            case LOGICAL:
                return Collections.unmodifiableList(logicalDelete);
            case PHYSICAL:
                return Collections.unmodifiableList(physicalDelete);
            default:
                throw new NotImplementedException("Security information parsing for delete type '" + type + "'");
        }
    }

    public String getSchematron() {
        return schematron;
    }

    @Override
    public List<FieldMetadata> getPrimaryKeyInfo() {
        return Collections.unmodifiableList(primaryKeyInfo);
    }

    @Override
    public List<FieldMetadata> getLookupFields() {
        return Collections.unmodifiableList(lookupFields);
    }

    @Override
    public void declareUsage(ContainedComplexTypeMetadata usage) {
        usages.add(usage);
    }

    @Override
    public void freezeUsages() {
        for (ContainedComplexTypeMetadata usage : usages) {
            usage.finalizeUsage();
        }
    }

    @Override
    public void setSubTypes(List<ComplexTypeMetadata> subTypes) {
        this.subTypes.clear();
        this.subTypes.addAll(subTypes);
    }

    public boolean hasField(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return false;
        }
        if (fieldName.indexOf('/') < 0) {
            boolean contains = fieldMetadata.containsKey(fieldName);
            if (!contains) {
                for (TypeMetadata typeMetadata : getSuperTypes()) {
                    contains |= ((ComplexTypeMetadata) typeMetadata).hasField(fieldName);
                }
            }
            return contains;
        }
        StringTokenizer tokenizer = new StringTokenizer(fieldName, "/"); //$NON-NLS-1$
        ComplexTypeMetadata currentType = this;
        while (tokenizer.hasMoreTokens()) {
            String current = tokenizer.nextToken();
            if (!currentType.hasField(current)) {
                return false;
            }
            if (tokenizer.hasMoreTokens()) {
                TypeMetadata type = currentType.getField(current).getType();
                if (!(type instanceof ComplexTypeMetadata)) {
                    return false;
                }
                currentType = (ComplexTypeMetadata) type;
            }
        }
        return true;
    }

    public Collection<ComplexTypeMetadata> getSubTypes() {
        List<ComplexTypeMetadata> subTypes = new LinkedList<ComplexTypeMetadata>();
        for (ComplexTypeMetadata subType : this.subTypes) {
            subTypes.add(subType);
            subTypes.addAll(subType.getSubTypes());
        }
        return subTypes;
    }

    public void registerSubType(ComplexTypeMetadata type) {
        subTypes.add(type);
    }

    public TypeMetadata freeze() {
        if (isFrozen) {
            return this;
        }
        // Gets fields from super types.
        if (!superTypes.isEmpty()) {
            Collection<FieldMetadata> thisTypeFields = new LinkedList<FieldMetadata>(fieldMetadata.values());
            fieldMetadata.clear();
            List<TypeMetadata> thisSuperTypes = new LinkedList<TypeMetadata>(superTypes);
            superTypes.clear();
            // Only accept super types with same instantiable status (if more than one).
            // TODO Type should use declareUsage iso. super type for this!
            if (thisSuperTypes.size() > 1) {
                Iterator<TypeMetadata> iterator = thisSuperTypes.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().isInstantiable() != isInstantiable) {
                        iterator.remove();
                    }
                }
            }
            for (TypeMetadata superType : thisSuperTypes) {
                if (isInstantiable() == superType.isInstantiable()) {
                    superType = superType.freeze();
                    if (superType instanceof ComplexTypeMetadata) {
                        ((ComplexTypeMetadata) superType).registerSubType(this);
                    }
                    superTypes.add(superType);
                } else {
                    superType = superType.freeze();
                }
                if (superType instanceof ComplexTypeMetadata) {
                    ((ComplexTypeMetadata) superType).registerSubType(this);
                    Collection<FieldMetadata> superTypeFields = ((ComplexTypeMetadata) superType).getFields();
                    for (FieldMetadata superTypeField : superTypeFields) {
                        superTypeField.adopt(this);
                    }
                }
            }
            for (FieldMetadata thisTypeField : thisTypeFields) {
                fieldMetadata.put(thisTypeField.getName(), thisTypeField);
            }
        }
        isFrozen = true;
        // Freeze all fields.
        Collection<FieldMetadata> values = new LinkedList<FieldMetadata>(fieldMetadata.values());
        for (FieldMetadata value : values) {
            FieldMetadata frozenFieldDeclaration = value.freeze();
            fieldMetadata.put(value.getName(), frozenFieldDeclaration);
            if (keyFields.containsKey(value.getName()) && !frozenFieldDeclaration.isKey()) {
                frozenFieldDeclaration.promoteToKey();
            }
        }
        for (Map.Entry<String, FieldMetadata> keyField : keyFields.entrySet()) {
            keyField.setValue(keyField.getValue().freeze());
        }
        // Freeze primary info
        List<FieldMetadata> frozenPrimaryKeyInfo = new LinkedList<FieldMetadata>();
        for (FieldMetadata pkInfo : primaryKeyInfo) {
            FieldMetadata freeze = pkInfo.freeze();
            if (freeze != null) {
                frozenPrimaryKeyInfo.add(freeze);
            }
        }
        primaryKeyInfo = frozenPrimaryKeyInfo;
        // Freeze lookup fields
        List<FieldMetadata> frozenLookupFields = new LinkedList<FieldMetadata>();
        for (FieldMetadata lookupField : lookupFields) {
            FieldMetadata freeze = lookupField.freeze();
            if (freeze != null) {
                frozenLookupFields.add(freeze);
            }
        }
        lookupFields = frozenLookupFields;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ComplexTypeMetadata)) {
            return false;
        }
        ComplexTypeMetadata that = (ComplexTypeMetadata) o;
        if (!name.equals(that.getName())) return false;
        if (nameSpace != null ? !nameSpace.equals(that.getNamespace()) : that.getNamespace() != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = nameSpace != null ? nameSpace.hashCode() : 0;
        result = 31 * result + name.hashCode();
        return result;
    }
}
