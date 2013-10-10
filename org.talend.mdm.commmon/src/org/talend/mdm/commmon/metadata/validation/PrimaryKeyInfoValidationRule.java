/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package org.talend.mdm.commmon.metadata.validation;

import java.util.List;

import javax.xml.XMLConstants;

import org.talend.mdm.commmon.metadata.*;
import org.w3c.dom.Element;

class PrimaryKeyInfoValidationRule implements ValidationRule {

    private final ComplexTypeMetadata type;

    PrimaryKeyInfoValidationRule(ComplexTypeMetadata type) {
        this.type = type;
    }

    @Override
    public boolean perform(ValidationHandler handler) {
        boolean success = true;
        List<FieldMetadata> primaryKeyInfo = type.getPrimaryKeyInfo();
        for (FieldMetadata pkInfo : primaryKeyInfo) {
            // PK Info must be defined in the entity (can't reference other entity field).
            ComplexTypeMetadata ctm = pkInfo.getContainingType();
            while (ctm instanceof ContainedComplexTypeMetadata) {
                ctm = ((ContainedComplexTypeMetadata) ctm).getContainerType();
            }
            if (!type.equals(ctm)) {
                handler.error(type, "Primary key info must refer a field of the same entity.",
                        pkInfo.<Element> getData(MetadataRepository.XSD_DOM_ELEMENT),
                        pkInfo.<Integer> getData(MetadataRepository.XSD_LINE_NUMBER),
                        pkInfo.<Integer> getData(MetadataRepository.XSD_COLUMN_NUMBER),
                        ValidationError.PRIMARY_KEY_INFO_NOT_IN_ENTITY);
                success &= false;
                continue;
            }
            // Order matters here: check if field is correct (exists) before checking isMany().
            ValidationFactory.getRule(pkInfo).perform(handler);
            // No need to check isMany() if field definition is already wrong.
            if (pkInfo.isMany()) {
                handler.error(type, "Primary key info element cannot be a repeatable element.",
                        pkInfo.<Element> getData(MetadataRepository.XSD_DOM_ELEMENT),
                        pkInfo.<Integer> getData(MetadataRepository.XSD_LINE_NUMBER),
                        pkInfo.<Integer> getData(MetadataRepository.XSD_COLUMN_NUMBER),
                        ValidationError.PRIMARY_KEY_INFO_CANNOT_BE_REPEATABLE);
                success &= false;
                continue;
            }
            if (!isPrimitiveTypeField(pkInfo)) {
                handler.warning(type, "Primary key info should refer to a field with a primitive XSD type.",
                        pkInfo.<Element> getData(MetadataRepository.XSD_DOM_ELEMENT),
                        pkInfo.<Integer> getData(MetadataRepository.XSD_LINE_NUMBER),
                        pkInfo.<Integer> getData(MetadataRepository.XSD_COLUMN_NUMBER),
                        ValidationError.PRIMARY_KEY_INFO_TYPE_NOT_PRIMITIVE);
            }
        }
        return success;
    }

    private static boolean isPrimitiveTypeField(FieldMetadata lookupField) {
        TypeMetadata currentType = lookupField.getType();
        if (!XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(currentType.getNamespace())) {
            while (!currentType.getSuperTypes().isEmpty()) {
                TypeMetadata superType = currentType.getSuperTypes().iterator().next();
                if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(superType.getNamespace())
                        && (Types.ANY_TYPE.equals(superType.getName())
                        || Types.ANY_SIMPLE_TYPE.equals(superType.getName()))) {
                    break;
                }
                currentType = superType;
            }
        }
        return XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(currentType.getNamespace());
    }

    @Override
    public boolean continueOnFail() {
        return true;
    }
}
