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

import javax.xml.XMLConstants;
import java.util.Iterator;
import java.util.List;

public class ReferenceFieldMetadata extends MetadataExtensions implements FieldMetadata {

    private final boolean isKey;

    private final boolean isMany;

    private final boolean allowFKIntegrityOverride;

    private final boolean isFKIntegrity;

    private final List<String> hideUsers;

    private TypeMetadata fieldType;

    private final List<String> writeUsers;

    private final boolean isMandatory;

    private final String name;

    private final TypeMetadata declaringType;

    private FieldMetadata referencedField;

    private FieldMetadata foreignKeyInfo;

    private ComplexTypeMetadata referencedType;

    private ComplexTypeMetadata containingType;

    private boolean isFrozen;

    private int cachedHashCode;

    public ReferenceFieldMetadata(ComplexTypeMetadata containingType,
                                  boolean isKey,
                                  boolean isMany,
                                  boolean isMandatory,
                                  String name,
                                  ComplexTypeMetadata referencedType,
                                  FieldMetadata referencedField,
                                  FieldMetadata foreignKeyInfo,
                                  boolean fkIntegrity,
                                  boolean allowFKIntegrityOverride,
                                  TypeMetadata fieldType,
                                  List<String> allowWriteUsers,
                                  List<String> hideUsers) {
        this.isMandatory = isMandatory;
        this.name = name;
        this.referencedField = referencedField;
        this.foreignKeyInfo = foreignKeyInfo;
        this.containingType = containingType;
        this.declaringType = containingType;
        this.allowFKIntegrityOverride = allowFKIntegrityOverride;
        this.isFKIntegrity = fkIntegrity;
        this.referencedType = referencedType;
        this.isKey = isKey;
        this.isMany = isMany;
        this.referencedType = referencedType;
        this.fieldType = fieldType;
        this.writeUsers = allowWriteUsers;
        this.hideUsers = hideUsers;
    }

    public FieldMetadata getReferencedField() {
        return referencedField;
    }

    public ComplexTypeMetadata getReferencedType() {
        return referencedType;
    }

    public String getName() {
        return name;
    }

    public boolean hasForeignKeyInfo() {
        return foreignKeyInfo != null;
    }

    public FieldMetadata getForeignKeyInfoField() {
        return foreignKeyInfo;
    }

    public ComplexTypeMetadata getContainingType() {
        return containingType;
    }

    public void setContainingType(ComplexTypeMetadata typeMetadata) {
        this.containingType = typeMetadata;
    }

    public FieldMetadata freeze(ValidationHandler handler) {
        if (isFrozen) {
            return this;
        }
        isFrozen = true;
        fieldType = fieldType.freeze(handler);
        if (foreignKeyInfo != null) {
            foreignKeyInfo = foreignKeyInfo.freeze(handler);
        }
        referencedType = (ComplexTypeMetadata) referencedType.freeze(handler);
        referencedField = referencedField.freeze(handler);
        return this;
    }

    public void promoteToKey(ValidationHandler handler) {
        handler.error(this,
                "Key field cannot be a foreign key element.",
                this.<Element>getData(MetadataRepository.XSD_DOM_ELEMENT),
                this.<Integer>getData(MetadataRepository.XSD_LINE_NUMBER),
                this.<Integer>getData(MetadataRepository.XSD_COLUMN_NUMBER),
                ValidationError.FIELD_KEY_CANNOT_BE_FOREIGN_KEY);
    }

    @Override
    public void validate(final ValidationHandler handler) {
        int errorCount = handler.getErrorCount();
        fieldType.validate(handler);
        if (handler.getErrorCount() > errorCount) {
            return;
        }
        TypeMetadata currentType = fieldType;
        // TODO This is duplicated code from MetadataUtils, bring MetadataUtils to this module (non core-dependent parts).
        if (!XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(currentType.getNamespace())) {
            while (!currentType.getSuperTypes().isEmpty()) {
                TypeMetadata superType = currentType.getSuperTypes().iterator().next();
                if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(superType.getNamespace())
                        && ("anyType".equals(superType.getName()) //$NON-NLS-1$
                        || "anySimpleType".equals(superType.getName()))) { //$NON-NLS-1$
                    break;
                }
                currentType = superType;
                errorCount = handler.getErrorCount();
                currentType.validate(handler);
                if (handler.getErrorCount() > errorCount) {
                    return;
                }
            }
        }
        final Integer line = this.getData(MetadataRepository.XSD_LINE_NUMBER);
        final Integer column = this.getData(MetadataRepository.XSD_COLUMN_NUMBER);
        final Element xmlElement = this.getData(MetadataRepository.XSD_DOM_ELEMENT);
        if (!Types.STRING.equals(currentType.getName())) {
            handler.error(this,
                    "FK field '" + getName() + "' is invalid because it isn't typed as string (nor a string restriction).",
                    xmlElement,
                    line,
                    column,
                    ValidationError.FOREIGN_KEY_NOT_STRING_TYPED);
        }
        if (fieldType.freeze(handler).getData(MetadataRepository.DATA_MAX_LENGTH) != null) {
            handler.warning(this,
                    "FK field '" + getName() + "' uses max length restriction. Make sure to include square brackets in max length value.",
                    xmlElement,
                    line,
                    column,
                    ValidationError.FOREIGN_KEY_USES_MAX_LENGTH);
        }
        // When type does not exist, client expects the reference field as error iso. the referenced field.
        int previousErrorCount = handler.getErrorCount();
        referencedField.validate(new LocationOverride(this, handler, xmlElement, line, column));
        if (handler.getErrorCount() > previousErrorCount) {
            return;
        }
        // FK can not be non-PK check
        freeze(handler);
        if (!referencedField.isKey()) {
            // Compute valid PK fields as help for user
            StringBuilder referencedTypePK = new StringBuilder();
            Iterator<FieldMetadata> keyFields = referencedType.getKeyFields().iterator();
            while (keyFields.hasNext()) {
                referencedTypePK.append('\'').append(keyFields.next().getName()).append('\'');
                if (keyFields.hasNext()) {
                    referencedTypePK.append(' ');
                }
            }
            // Reports error
            handler.warning(referencedField,
                    "Foreign key should point to a primary key (recommended choices are: " + referencedTypePK + ")",
                    this.<Element>getData(MetadataRepository.XSD_DOM_ELEMENT),
                    this.<Integer>getData(MetadataRepository.XSD_LINE_NUMBER),
                    this.<Integer>getData(MetadataRepository.XSD_COLUMN_NUMBER),
                    ValidationError.FOREIGN_KEY_SHOULD_POINT_TO_PRIMARY_KEY);
        }
        // Foreign key info checks
        if (foreignKeyInfo != null) {
            errorCount = handler.getErrorCount();
            foreignKeyInfo.validate(handler);
            if (handler.getErrorCount() > errorCount) {
                return; // No need to perform other checks if field is already invalid.
            }
            if (!isPrimitiveTypeField(foreignKeyInfo)) {
                handler.warning(foreignKeyInfo,
                        "Foreign key info is not typed as primitive XSD.",
                        foreignKeyInfo.<Element>getData(MetadataRepository.XSD_DOM_ELEMENT),
                        foreignKeyInfo.<Integer>getData(MetadataRepository.XSD_LINE_NUMBER),
                        foreignKeyInfo.<Integer>getData(MetadataRepository.XSD_COLUMN_NUMBER),
                        ValidationError.FOREIGN_KEY_INFO_NOT_PRIMITIVE_XSD_TYPED);
            }
            if (foreignKeyInfo.isMany()) {
                handler.warning(foreignKeyInfo,
                        "Foreign key info should not be a repeatable element.",
                        foreignKeyInfo.<Element>getData(MetadataRepository.XSD_DOM_ELEMENT),
                        foreignKeyInfo.<Integer>getData(MetadataRepository.XSD_LINE_NUMBER),
                        foreignKeyInfo.<Integer>getData(MetadataRepository.XSD_COLUMN_NUMBER),
                        ValidationError.FOREIGN_KEY_INFO_REPEATABLE);
            }
        }
    }

    // TODO Duplicated code in org.talend.mdm.commmon.metadata.ComplexTypeMetadataImpl.isPrimitiveTypeField()
    private static boolean isPrimitiveTypeField(FieldMetadata lookupField) {
        TypeMetadata currentType = lookupField.getType();
        if (!XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(currentType.getNamespace())) {
            while (!currentType.getSuperTypes().isEmpty()) {
                TypeMetadata superType = currentType.getSuperTypes().iterator().next();
                if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(superType.getNamespace())
                        && ("anyType".equals(superType.getName()) //$NON-NLS-1$
                        || "anySimpleType".equals(superType.getName()))) { //$NON-NLS-1$
                    break;
                }
                currentType = superType;
            }
        }
        return XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(currentType.getNamespace());
    }

    public TypeMetadata getDeclaringType() {
        return containingType;
    }

    public boolean isFKIntegrity() {
        return isFKIntegrity;
    }

    public boolean allowFKIntegrityOverride() {
        return allowFKIntegrityOverride;
    }

    public void adopt(ComplexTypeMetadata metadata, MetadataRepository repository) {
        FieldMetadata copy = copy(repository);
        copy.setContainingType(metadata);
        metadata.addField(copy);
    }

    public TypeMetadata getType() {
        return referencedField.getType();
    }

    public boolean isKey() {
        return isKey;
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public FieldMetadata copy(MetadataRepository repository) {
        ComplexTypeMetadata referencedTypeCopy = (ComplexTypeMetadata) referencedType.copy(repository);
        FieldMetadata referencedFieldCopy = referencedField.copy(repository);
        FieldMetadata foreignKeyInfoCopy = hasForeignKeyInfo() ? foreignKeyInfo.copy(repository) : null;
        ComplexTypeMetadata containingTypeCopy = (ComplexTypeMetadata) containingType.copy(repository);
        return new ReferenceFieldMetadata(containingTypeCopy,
                isKey,
                isMany,
                isMandatory,
                name,
                referencedTypeCopy,
                referencedFieldCopy,
                foreignKeyInfoCopy,
                isFKIntegrity,
                allowFKIntegrityOverride,
                fieldType,
                writeUsers,
                hideUsers);
    }

    @Override
    public String toString() {
        return "Reference {" + //$NON-NLS-1$
                "containing type= " + containingType + //$NON-NLS-1$
                ", declaring type=" + declaringType + //$NON-NLS-1$
                ", name='" + name + '\'' + //$NON-NLS-1$
                ", isKey=" + isKey + //$NON-NLS-1$
                ", is many=" + isMany + //$NON-NLS-1$
                ", referenced type= " + referencedType + //$NON-NLS-1$
                ", referenced field= " + referencedField + //$NON-NLS-1$
                ", foreign key info='" + foreignKeyInfo + '\'' + //$NON-NLS-1$
                ", allow FK integrity override= " + allowFKIntegrityOverride + //$NON-NLS-1$
                ", check FK integrity= " + isFKIntegrity + //$NON-NLS-1$
                '}';
    }

    public boolean isMany() {
        return isMany;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public List<String> getHideUsers() {
        return hideUsers;
    }

    public List<String> getWriteUsers() {
        return writeUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReferenceFieldMetadata)) return false;

        ReferenceFieldMetadata that = (ReferenceFieldMetadata) o;

        if (allowFKIntegrityOverride != that.allowFKIntegrityOverride) return false;
        if (isFKIntegrity != that.isFKIntegrity) return false;
        if (isKey != that.isKey) return false;
        if (isMandatory != that.isMandatory) return false;
        if (isMany != that.isMany) return false;
        if (containingType != null ? !containingType.equals(that.containingType) : that.containingType != null)
            return false;
        if (declaringType != null ? !declaringType.equals(that.declaringType) : that.declaringType != null)
            return false;
        if (foreignKeyInfo != null ? !foreignKeyInfo.equals(that.foreignKeyInfo) : that.foreignKeyInfo != null)
            return false;
        if (hideUsers != null ? !hideUsers.equals(that.hideUsers) : that.hideUsers != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (writeUsers != null ? !writeUsers.equals(that.writeUsers) : that.writeUsers != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        if (isFrozen && cachedHashCode > 0) {
            return cachedHashCode;
        }
        int result = (isKey ? 1 : 0);
        result = 31 * result + (isMany ? 1 : 0);
        result = 31 * result + (foreignKeyInfo != null ? foreignKeyInfo.hashCode() : 0);
        result = 31 * result + (containingType != null ? containingType.hashCode() : 0);
        result = 31 * result + (declaringType != null ? declaringType.hashCode() : 0);
        result = 31 * result + (allowFKIntegrityOverride ? 1 : 0);
        result = 31 * result + (isFKIntegrity ? 1 : 0);
        result = 31 * result + (isMandatory ? 1 : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        cachedHashCode = result;
        return result;
    }

}
