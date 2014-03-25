/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package org.talend.mdm.commmon.metadata.compare;

import org.talend.mdm.commmon.metadata.MetadataVisitable;

public abstract class Change {

    private final MetadataVisitable element;

    private final String message;

    Change(MetadataVisitable element, String message) {
        this.element = element;
        this.message = message;
    }

    public MetadataVisitable getElement() {
        return element;
    }

    public String getMessage() {
        return message;
    }
}
