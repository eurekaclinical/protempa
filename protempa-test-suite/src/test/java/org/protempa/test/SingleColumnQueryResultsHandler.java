/*
 * #%L
 * Protempa Test Suite
 * %%
 * Copyright (C) 2012 Emory University
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
package org.protempa.test;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.protempa.FinderException;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.query.handler.QueryResultsHandler;
import org.protempa.query.handler.QueryResultsHandlerValidationFailedException;

final class SingleColumnQueryResultsHandler implements QueryResultsHandler {

    private final Writer writer;

    private final Map<String, Map<Proposition, List<Proposition>>> data = new HashMap<String, Map<Proposition, List<Proposition>>>();

    /**
     * Creates a new instance that will write to the given writer. The finish()
     * method will close the writer.
     * 
     * @param writer
     *            the {@link Writer} to output to
     */
    public SingleColumnQueryResultsHandler(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void init(KnowledgeSource knowledgeSource) throws FinderException {

    }

    @Override
    public void finish() throws FinderException {
        try {
            SortedSet<String> sortedKeyIds = new TreeSet<String>(this.data.keySet());
            for (String keyId : sortedKeyIds) {
                writeLine(keyId);
                List<Proposition> sortedProps = new ArrayList<Proposition>(this.data.get(keyId).keySet());
                Collections.sort(sortedProps, new PropositionComparator());
                for (Proposition p : sortedProps) {
                    writeLine(p.getId());
                    List<Proposition> sortedDerivations = new ArrayList<Proposition>(this.data.get(keyId).get(p));
                    Collections.sort(sortedProps, new PropositionComparator());
                    for (Proposition d : sortedDerivations) {
                        writeLine(d.getId());
                    }
                }
            }
            this.writer.close();
        } catch (IOException e) {
            throw new FinderException(e);
        }
    }

    private void writeLine(String str) throws IOException {
        this.writer.write(str);
        this.writer.write("\n");
    }

    private void storeDerivations(
            Map<Proposition, List<Proposition>> derivations,
            List<Proposition> outputDerivations) throws IOException {
        for (Entry<Proposition, List<Proposition>> pp : derivations.entrySet()) {
            for (Proposition p : pp.getValue()) {
                outputDerivations.add(p);
            }
        }
    }

    @Override
    public void handleQueryResult(String keyId, List<Proposition> propositions,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references) throws FinderException {
        try {
            this.data.put(keyId, new HashMap<Proposition, List<Proposition>>());
            for (Proposition p : propositions) {
                this.data.get(keyId).put(p, new ArrayList<Proposition>());
                storeDerivations(forwardDerivations, this.data.get(keyId)
                        .get(p));
                storeDerivations(backwardDerivations,
                        this.data.get(keyId).get(p));
            }
        } catch (IOException ex) {
            throw new FinderException(ex);
        }
    }

    @Override
    public void validate(KnowledgeSource knowledgeSource)
            throws QueryResultsHandlerValidationFailedException,
            KnowledgeSourceReadException {

    }

    private static class PropositionComparator implements
            Comparator<Proposition> {

        @Override
        public int compare(Proposition o1, Proposition o2) {
            return o1.getId().compareTo(o2.getId());
        }

    }
}
