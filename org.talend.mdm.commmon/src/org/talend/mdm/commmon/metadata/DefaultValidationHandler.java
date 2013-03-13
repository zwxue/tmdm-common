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

import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class DefaultValidationHandler implements ValidationHandler {

    public static final Logger LOGGER = Logger.getLogger(DefaultValidationHandler.class);

    private final Set<String> messages = new HashSet<String>();

    @Override
    public void error(TypeMetadata type, String message, int lineNumber, int columnNumber) {
        messages.add(message + " (line: " + lineNumber + ", column:" + columnNumber + ")");
    }

    @Override
    public void fatal(TypeMetadata type, String message, int lineNumber, int columnNumber) {
        messages.add(message + " (line: " + lineNumber + ", column:" + columnNumber + ")");
    }

    @Override
    public void warning(TypeMetadata type, String message, int lineNumber, int columnNumber) {
        LOGGER.warn(message);
    }

    @Override
    public void end() {
        if (!messages.isEmpty()) {
            StringBuilder aggregatedMessages = new StringBuilder();
            for (String message : messages) {
                aggregatedMessages.append(message).append('\n').append('\t');
            }
            throw new RuntimeException("Data model is invalid:\n\t" + aggregatedMessages.toString());
        }
    }
}
