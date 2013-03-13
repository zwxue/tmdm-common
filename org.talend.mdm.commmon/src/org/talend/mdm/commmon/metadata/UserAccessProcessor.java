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

import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDAnnotation;
import org.w3c.dom.Element;

class UserAccessProcessor implements XmlSchemaAnnotationProcessor {

    @Override
    public void process(MetadataRepository repository, ComplexTypeMetadata type, XSDAnnotation annotation, XmlSchemaAnnotationProcessorState state) {
        if (annotation != null) {
            EList<Element> appInfoElements = annotation.getApplicationInformation();
            for (Element appInfo : appInfoElements) {
                if ("X_Hide".equals(appInfo.getAttribute("source"))) { //$NON-NLS-1$ //$NON-NLS-2$
                    state.getHide().add(appInfo.getTextContent());
                } else if ("X_Write".equals(appInfo.getAttribute("source"))) { //$NON-NLS-1$ //$NON-NLS-2$
                    state.getAllowWrite().add(appInfo.getTextContent());
                } else if ("X_Deny_Create".equals(appInfo.getAttribute("source"))) { //$NON-NLS-1$ //$NON-NLS-2$
                    state.getDenyCreate().add(appInfo.getTextContent());
                } else if ("X_Deny_LogicalDelete".equals(appInfo.getAttribute("source"))) { //$NON-NLS-1$ //$NON-NLS-2$
                    state.getDenyLogicalDelete().add(appInfo.getTextContent());
                } else if ("X_Deny_PhysicalDelete".equals(appInfo.getAttribute("source"))) { //$NON-NLS-1$ //$NON-NLS-2$
                    state.getDenyPhysicalDelete().add(appInfo.getTextContent());
                }
            }
        }
    }
}
