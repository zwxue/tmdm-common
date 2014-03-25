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

package org.talend.mdm.commmon.metadata.validation;

import org.talend.mdm.commmon.metadata.*;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;

class ForeignKeyInfo implements ValidationRule {

    private final ReferenceFieldMetadata field;

    public ForeignKeyInfo(ReferenceFieldMetadata field) {
        this.field = field;
    }

    @Override
    public boolean perform(ValidationHandler handler) {
        // Foreign key info checks
        for (FieldMetadata foreignKeyInfo : field.getForeignKeyInfoFields()) {
            if(!ValidationFactory.getRule(foreignKeyInfo).perform(handler)) {
                continue;
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
            if (foreignKeyInfo.getContainingType() != null) {
                ComplexTypeMetadata foreignKeyInfoContainingType = foreignKeyInfo.getContainingType();
                while (foreignKeyInfoContainingType instanceof ContainedComplexTypeMetadata) {
                    foreignKeyInfoContainingType = ((ContainedComplexTypeMetadata) foreignKeyInfoContainingType).getContainerType();
                }
                if (foreignKeyInfoContainingType.isInstantiable() && !foreignKeyInfoContainingType.equals(field.getReferencedType())) {
                    handler.error(foreignKeyInfo,
                            "Foreign key info must reference an element in referenced type.",
                            foreignKeyInfo.<Element>getData(MetadataRepository.XSD_DOM_ELEMENT),
                            foreignKeyInfo.<Integer>getData(MetadataRepository.XSD_LINE_NUMBER),
                            foreignKeyInfo.<Integer>getData(MetadataRepository.XSD_COLUMN_NUMBER),
                            ValidationError.FOREIGN_KEY_INFO_NOT_REFERENCING_FK_TYPE);
                }
            }
        }
        return true;
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

    @Override
    public boolean continueOnFail() {
        return false;
    }
}
