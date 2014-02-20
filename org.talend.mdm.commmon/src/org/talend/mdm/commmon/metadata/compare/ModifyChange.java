/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package org.talend.mdm.commmon.metadata.compare;

import org.talend.mdm.commmon.metadata.MetadataVisitable;

public class ModifyChange extends Change {

    private final MetadataVisitable current;

    ModifyChange(MetadataVisitable previous, MetadataVisitable current) {
        super(previous, "Modification on " + previous);
        this.current = current;
    }

    public MetadataVisitable getPrevious() {
        return getElement();
    }

    public MetadataVisitable getCurrent() {
        return current;
    }
}
