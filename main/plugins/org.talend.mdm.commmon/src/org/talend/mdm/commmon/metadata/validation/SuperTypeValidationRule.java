/*
 * Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

class SuperTypeValidationRule implements ValidationRule {

    private final ComplexTypeMetadata type;

    SuperTypeValidationRule(ComplexTypeMetadata type) {
        this.type = type;
    }

    @Override
    public boolean perform(ValidationHandler handler) {
        Collection<TypeMetadata> superTypes = type.getSuperTypes();
        if (!superTypes.isEmpty()) {
            List<TypeMetadata> thisSuperTypes = new LinkedList<TypeMetadata>(superTypes);
            for (TypeMetadata superType : thisSuperTypes) {
                if (type.isInstantiable() == superType.isInstantiable()) {
                    if (superType instanceof ComplexTypeMetadata) {
                        Collection<FieldMetadata> thisTypeKeyFields = type.getKeyFields();
                        for (FieldMetadata thisTypeKeyField : thisTypeKeyFields) {
                            if (!((ComplexTypeMetadata) superType).hasField(thisTypeKeyField.getName())) {
                                handler.error(superType, "Type '" + type.getName() + "' cannot add field(s) to its key because " +
                                        "super type '" + superType.getName() + "' already defines key.",
                                        type.<Element>getData(MetadataRepository.XSD_DOM_ELEMENT),
                                        type.<Integer>getData(MetadataRepository.XSD_LINE_NUMBER),
                                        type.<Integer>getData(MetadataRepository.XSD_COLUMN_NUMBER),
                                        ValidationError.TYPE_CANNOT_OVERRIDE_SUPER_TYPE_KEY);
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean continueOnFail() {
        return true;
    }
}
