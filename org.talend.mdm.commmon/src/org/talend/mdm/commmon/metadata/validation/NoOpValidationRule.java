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

package org.talend.mdm.commmon.metadata.validation;

import org.talend.mdm.commmon.metadata.ValidationHandler;

class NoOpValidationRule implements ValidationRule {

    public static ValidationRule INSTANCE = new NoOpValidationRule();

    private NoOpValidationRule() {
    }

    @Override
    public boolean perform(ValidationHandler handler) {
        // No op
        return true;
    }

    @Override
    public boolean continueOnFail() {
        return true;
    }
}
