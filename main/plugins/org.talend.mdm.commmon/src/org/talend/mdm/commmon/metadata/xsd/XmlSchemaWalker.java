/*
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package org.talend.mdm.commmon.metadata.xsd;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.*;

/**
 *
 */
public class XmlSchemaWalker {

    public static void walk(XSDSchema xmlSchema, XSDVisitor visitor) {
        visitor.visitSchema(xmlSchema);
        // Visit element first (create MDM entity types)
        EList elements = xmlSchema.getElementDeclarations();
        for (Object element : elements) {
            walk(((XSDElementDeclaration) element), visitor);
        }
        // Visit remaining types (sometimes used in case of inheritance by entity types).
        EList types = xmlSchema.getTypeDefinitions();
        for (Object type : types) {
            walk(((XSDTypeDefinition) type), visitor);
        }
    }

    public static void walk(XSDTypeDefinition type, XSDVisitor visitor) {
        if (type instanceof XSDSimpleTypeDefinition) {
            walk(((XSDSimpleTypeDefinition) type), visitor);
        } else if (type instanceof XSDComplexTypeDefinition) {
            walk(((XSDComplexTypeDefinition) type), visitor);
        } else {
            throw new IllegalArgumentException("Not supported XML Schema type: " + type.getClass().getName());
        }
    }

    public static void walk(XSDElementDeclaration element, XSDVisitor visitor) {
        visitor.visitElement(element);
    }

    private static void walk(XSDSimpleTypeDefinition xmlSchemaType, XSDVisitor visitor) {
        visitor.visitSimpleType(xmlSchemaType);
    }

    private static void walk(XSDComplexTypeDefinition xmlSchemaType, XSDVisitor visitor) {
        visitor.visitComplexType(xmlSchemaType);
    }

    public static void walk(XSDConcreteComponent component, XSDVisitor visitor) {
        if (component instanceof XSDElementDeclaration) {
            walk(((XSDElementDeclaration) component), visitor);
        }
    }
}