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

package org.talend.mdm.commmon.metadata.compare;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.*;

import java.util.*;

public class Compare {

    private static final Logger LOGGER = Logger.getLogger(Compare.class);

    public static DiffResults compare(MetadataRepository left, MetadataRepository right) {
        DiffResults diffResults = new DiffResults();
        Collection<ComplexTypeMetadata> entityTypes = left.getUserComplexTypes();
        DumpContent dumpContent = new DumpContent();
        for (ComplexTypeMetadata leftType : entityTypes) {
            // Check if left type still exists
            ComplexTypeMetadata rightType = right.getComplexType(leftType.getName());
            if (rightType == null) {
                // Right type does not exist
                diffResults.removeChanges.add(new RemoveChange(leftType));
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("[REMOVED] Type " + leftType + " no longer exist.");
                }
            } else {
                // Read left content
                List<MetadataVisitable> leftContent = new ArrayList<MetadataVisitable>(leftType.accept(dumpContent));
                dumpContent.reset();
                // Read right content
                List<MetadataVisitable> rightContent = new ArrayList<MetadataVisitable>(rightType.accept(dumpContent));
                dumpContent.reset();
                // Compare contents
                Map<String, FieldMetadata> removedElementNames = new HashMap<String, FieldMetadata>();
                for (MetadataVisitable leftVisitable : leftContent) {
                    int index = rightContent.indexOf(leftVisitable);
                    if (index < 0) {
                        // Different (right does not exist, but might be removed or modified).
                        if (leftVisitable instanceof FieldMetadata) {
                            FieldMetadata field = (FieldMetadata) leftVisitable;
                            removedElementNames.put(field.getName(), field);
                        }
                    } else {
                        rightContent.remove(index);
                    }
                }
                if (!rightContent.isEmpty()) {
                    Iterator<MetadataVisitable> addedElements = rightContent.iterator();
                    while (addedElements.hasNext()) {
                        MetadataVisitable current = addedElements.next();
                        MetadataVisitable modifiedElement = null;
                        if (current instanceof FieldMetadata) {
                            modifiedElement = removedElementNames.get(((FieldMetadata) current).getName());
                        }
                        if (modifiedElement != null) {
                            // Modified element (only exist in right, not in left).
                            diffResults.modifyChanges.add(new ModifyChange(modifiedElement, current));
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("[MODIFIED] " + current + " was modified" + "\t was " + modifiedElement + "\t now " + current);
                            }
                            removedElementNames.remove(((FieldMetadata) current).getName());
                        } else {
                            // Added element (only exist in right, not in left).
                            diffResults.addChanges.add(new AddChange(current));
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("[ADDED] " + current + " was added.");
                            }
                        }
                        addedElements.remove();
                    }
                }
                // Process removed elements
                for (FieldMetadata fieldMetadata : removedElementNames.values()) {
                    // Different (right does not exist).
                    diffResults.removeChanges.add(new RemoveChange(fieldMetadata));
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("[REMOVED] " + fieldMetadata + " no longer exist.");
                    }
                }
                if (!rightContent.isEmpty()) { // Not expected at all -> likely a bug
                    throw new IllegalStateException("Elements remain for comparison.");
                }
            }
        }
        return diffResults;
    }

    public static class DumpContent extends DefaultMetadataVisitor<List<MetadataVisitable>> {

        private final Stack<MetadataVisitable> content = new Stack<MetadataVisitable>();

        public void reset() {
            content.clear();
        }

        @Override
        public List<MetadataVisitable> visit(MetadataRepository repository) {
            super.visit(repository);
            return content;
        }

        @Override
        public List<MetadataVisitable> visit(SimpleTypeMetadata simpleType) {
            content.push(simpleType);
            super.visit(simpleType);
            return content;
        }

        @Override
        public List<MetadataVisitable> visit(ComplexTypeMetadata complexType) {
            content.push(complexType);
            super.visit(complexType);
            return content;
        }

        @Override
        public List<MetadataVisitable> visit(ContainedComplexTypeMetadata containedType) {
            super.visit(containedType);
            content.push(containedType);
            return content;
        }

        @Override
        public List<MetadataVisitable> visit(ReferenceFieldMetadata referenceField) {
            super.visit(referenceField);
            content.push(referenceField);
            return content;
        }

        @Override
        public List<MetadataVisitable> visit(ContainedTypeFieldMetadata containedField) {
            super.visit(containedField);
            content.push(containedField);
            return content;
        }

        @Override
        public List<MetadataVisitable> visit(FieldMetadata fieldMetadata) {
            super.visit(fieldMetadata);
            content.push(fieldMetadata);
            return content;
        }

        @Override
        public List<MetadataVisitable> visit(SimpleTypeFieldMetadata simpleField) {
            super.visit(simpleField);
            content.push(simpleField);
            return content;
        }

        @Override
        public List<MetadataVisitable> visit(EnumerationFieldMetadata enumField) {
            content.push(enumField);
            super.visit(enumField);
            return content;
        }
    }

    public static class DiffResults {

        private final List<AddChange> addChanges = new LinkedList<AddChange>();

        private final List<RemoveChange> removeChanges = new LinkedList<RemoveChange>();

        private final List<ModifyChange> modifyChanges = new LinkedList<ModifyChange>();

        public List<AddChange> getAddChanges() {
            return Collections.unmodifiableList(addChanges);
        }

        public List<RemoveChange> getRemoveChanges() {
            return Collections.unmodifiableList(removeChanges);
        }

        public List<ModifyChange> getModifyChanges() {
            return Collections.unmodifiableList(modifyChanges);
        }

        public List<Change> getActions() {
            List<Change> allChanges = new ArrayList<Change>(addChanges.size() + removeChanges.size() + modifyChanges.size());
            allChanges.addAll(addChanges);
            allChanges.addAll(removeChanges);
            allChanges.addAll(modifyChanges);
            return allChanges;
        }
    }

}
