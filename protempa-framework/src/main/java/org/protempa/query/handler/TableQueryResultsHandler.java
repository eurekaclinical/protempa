/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.protempa.query.handler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;

import org.apache.commons.lang3.StringUtils;
import org.arp.javautil.string.StringUtil;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.query.Query;
import org.protempa.query.handler.table.TableColumnSpec;
import org.protempa.query.handler.table.TableColumnSpecValidationFailedException;

/**
 *
 * @author Andrew Post
 */
public final class TableQueryResultsHandler
        extends AbstractQueryResultsHandler {

    public class TableUsingKnowledgeSource extends AbstractUsingKnowledgeSource {

        private KnowledgeSource knowledgeSource;

        public class TableForQuery extends AbstractForQuery {

            private TableForQuery(Query query) {

            }

            @Override
            public void start() throws QueryResultsHandlerProcessingException {
                Logger logger = Util.logger();

                if (headerWritten) {
                    try {
                        List<String> columnNames = new ArrayList<>();
                        columnNames.add("KeyId");
                        for (TableColumnSpec columnSpec : columnSpecs) {
                            logger.log(Level.FINE, "Processing columnSpec type {0}",
                                    columnSpec.getClass().getName());
                            String[] colNames
                                    = columnSpec.columnNames(knowledgeSource);
                            assert colNames.length > 0 :
                                    "colNames must have length > 0";

                            for (int index = 0; index < colNames.length; index++) {
                                String colName = colNames[index];
                                if (replace.containsKey(colName)) {
                                    colNames[index] = replace.get(colName);
                                }
                            }

                            if (logger.isLoggable(Level.FINE)) {
                                logger.log(
                                        Level.FINE,
                                        "Got the following columns for proposition {0}: {1}",
                                        new Object[]{StringUtils.join(rowPropositionIds, ", "),
                                            StringUtils.join(colNames, ", ")});
                            }
                            for (String colName : colNames) {
                                columnNames.add(colName);
                            }
                        }
                        StringUtil.escapeAndWriteDelimitedColumns(columnNames,
                                columnDelimiter, out);
                        out.newLine();
                    } catch (KnowledgeSourceReadException ex1) {
                        throw new QueryResultsHandlerProcessingException("Error reading knowledge source", ex1);
                    } catch (IOException ex) {
                        throw new QueryResultsHandlerProcessingException("Could not write header", ex);
                    }
                }

            }

            @Override
            public void handleQueryResult(String keyId, List<Proposition> propositions,
                    Map<Proposition, List<Proposition>> forwardDerivations,
                    Map<Proposition, List<Proposition>> backwardDerivations,
                    Map<UniqueId, Proposition> references)
                    throws QueryResultsHandlerProcessingException {
                int n = columnSpecs.length;
                Util.logger().log(Level.FINER, "Processing keyId {0}", keyId);
                for (Proposition prop : propositions) {
                    if (!org.arp.javautil.arrays.Arrays.contains(
                            rowPropositionIds, prop.getId())) {
                        continue;
                    }
                    try {
                        StringUtil.escapeAndWriteDelimitedColumn(keyId,
                                columnDelimiter, out);
                        if (n > 0) {
                            out.write(columnDelimiter);
                        }
                        for (int i = 0; i < n; i++) {
                            TableColumnSpec columnSpec = columnSpecs[i];
                            columnSpec.columnValues(keyId, prop,
                                    forwardDerivations, backwardDerivations,
                                    references, knowledgeSource,
                                    replace, columnDelimiter, out);
                            if (i < n - 1) {
                                out.write(columnDelimiter);
                            } else {
                                out.newLine();
                            }

                        }
                    } catch (KnowledgeSourceReadException ex1) {
                        throw new QueryResultsHandlerProcessingException(
                                "Could not read knowledge source", ex1);
                    } catch (IOException ex) {
                        throw new QueryResultsHandlerProcessingException(
                                "Could not write row" + ex);
                    }
                }
            }

            /**
             * Infers a list of propositions to populate all of the specified
             * columns.
             *
             * @return an array of proposition id {@link String}s.
             */
            @Override
            public String[] getPropositionIdsNeeded() throws QueryResultsHandlerProcessingException {
                if (inferPropositionIdsNeeded) {
                    Set<String> result = new HashSet<>();
                    org.arp.javautil.arrays.Arrays.addAll(result,
                            rowPropositionIds);
                    for (TableColumnSpec columnSpec : columnSpecs) {
                        String[] inferredPropIds;
                        try {
                            inferredPropIds = columnSpec.getInferredPropositionIds(
                                    knowledgeSource, rowPropositionIds);
                        } catch (KnowledgeSourceReadException ex) {
                            throw new QueryResultsHandlerProcessingException("Error getting proposition ids needed", ex);
                        }
                        org.arp.javautil.arrays.Arrays.addAll(result, inferredPropIds);
                    }
                    return result.toArray(new String[result.size()]);
                } else {
                    return ArrayUtils.EMPTY_STRING_ARRAY;
                }
            }

        }

        private TableUsingKnowledgeSource(KnowledgeSource knowledgeSource) {
            this.knowledgeSource = knowledgeSource;
        }

        @Override
        public void validate()
                throws QueryResultsHandlerValidationFailedException {
            List<String> invalidPropIds = new ArrayList<>();
            try {
                for (String propId : rowPropositionIds) {
                    if (!knowledgeSource.hasPropositionDefinition(propId)) {
                        invalidPropIds.add(propId);
                    }
                }
                if (!invalidPropIds.isEmpty()) {
                    throw new QueryResultsHandlerValidationFailedException(
                            "Invalid row proposition id(s): "
                            + StringUtils.join(invalidPropIds, ", "));
                }
                int i = 1;
                for (TableColumnSpec columnSpec : columnSpecs) {
                    try {
                        columnSpec.validate(knowledgeSource);
                    } catch (TableColumnSpecValidationFailedException ex) {
                        throw new QueryResultsHandlerValidationFailedException(
                                "Validation of column spec " + i + " failed", ex);
                    }
                    i++;
                }
            } catch (KnowledgeSourceReadException ex) {
                throw new QueryResultsHandlerValidationFailedException("Error during validation", ex);
            }
        }

        @Override
        public TableForQuery forQuery(Query query) throws QueryResultsHandlerInitException {
            return new TableForQuery(query);
        }

    }

    private static final long serialVersionUID = -1503401944818776787L;
    private final char columnDelimiter;
    private final String[] rowPropositionIds;
    private final TableColumnSpec[] columnSpecs;
    private final boolean headerWritten;
    private final BufferedWriter out;
    private final Map<String, String> replace;
    private final boolean inferPropositionIdsNeeded;

    public TableQueryResultsHandler(BufferedWriter out, char columnDelimiter,
            String[] rowPropositionIds, TableColumnSpec[] columnSpecs,
            boolean headerWritten) {
        this(out, columnDelimiter, rowPropositionIds, columnSpecs,
                headerWritten, true);
    }

    public TableQueryResultsHandler(BufferedWriter out, char columnDelimiter,
            String[] rowPropositionIds, TableColumnSpec[] columnSpecs,
            boolean headerWritten, boolean inferPropositionIdsNeeded) {
        checkConstructorArgs(rowPropositionIds, columnSpecs);
        this.columnDelimiter = columnDelimiter;
        this.rowPropositionIds = rowPropositionIds.clone();
        ProtempaUtil.internAll(this.rowPropositionIds);
        this.columnSpecs = columnSpecs.clone();
        this.headerWritten = headerWritten;
        this.out = out;
        this.replace = new HashMap<>();
        this.replace.put(null, "(null)");
        this.replace.put("", "(empty)");
        this.inferPropositionIdsNeeded = inferPropositionIdsNeeded;
    }

    public String[] getRowPropositionIds() {
        return this.rowPropositionIds.clone();
    }

    public char getColumnDelimiter() {
        return this.columnDelimiter;
    }

    public TableColumnSpec[] getColumnSpecs() {
        return this.columnSpecs.clone();
    }

    public boolean isHeaderWritten() {
        return this.headerWritten;
    }

    @Override
    public TableUsingKnowledgeSource usingKnowledgeSource(KnowledgeSource knowledgeSource) throws QueryResultsHandlerInitException {
        return new TableUsingKnowledgeSource(knowledgeSource);
    }

    private void checkConstructorArgs(String[] rowPropositionIds,
            TableColumnSpec[] columnSpecs) {
        ProtempaUtil.checkArray(rowPropositionIds, "rowPropositionIds");
        ProtempaUtil.checkArray(columnSpecs, "columnSpecs");
    }

}
