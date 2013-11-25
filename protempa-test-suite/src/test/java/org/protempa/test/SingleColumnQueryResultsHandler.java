/*
 * #%L
 * Protempa Test Suite
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
import org.apache.commons.lang3.ArrayUtils;

import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.query.handler.AbstractQueryResultsHandler;
import org.protempa.query.handler.QueryResultsHandlerProcessingException;

final class SingleColumnQueryResultsHandler 
        extends AbstractQueryResultsHandler {

    private final Writer writer;

    private final Map<String, Map<Proposition, List<Proposition>>> data = 
            new HashMap<>();

    /**
     * Creates a new instance that will write to the given writer. The finish()
     * method will close the writer.
     * 
     * @param writer
     *            the {@link Writer} to output to
     */
    SingleColumnQueryResultsHandler(Writer writer) {
        this.writer = writer;
    }

    @Override
    public void finish() throws QueryResultsHandlerProcessingException {
        try {
            SortedSet<String> sortedKeyIds = new TreeSet<>(
                    this.data.keySet());
            for (String keyId : sortedKeyIds) {
                writeLine(keyId);
                List<PropositionWithDerivations> sortedProps = 
                        new ArrayList<>();
                for (Entry<Proposition, List<Proposition>> pp : this.data.get(
                        keyId).entrySet()) {
                    sortedProps.add(new PropositionWithDerivations(pp.getKey(),
                            pp.getValue()));
                }
                Collections.sort(sortedProps,
                        new PropositionWithDerivationsComparator());
                for (PropositionWithDerivations pwd : sortedProps) {
                    writeLine(pwd.getProposition().getId());
                    List<Proposition> sortedDerivations = 
                            new ArrayList<>(pwd.getDerivations());
                    Collections.sort(sortedDerivations,
                            new PropositionComparator());
                    for (Proposition d : sortedDerivations) {
                        writeLine(d.getId());
                    }
                }
            }
            this.writer.close();
        } catch (IOException e) {
            throw new QueryResultsHandlerProcessingException(e);
        }
    }

    @Override
    public void handleQueryResult(String keyId, List<Proposition> propositions,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references) 
            throws QueryResultsHandlerProcessingException {
        try {
            this.data.put(keyId, 
                    new HashMap<Proposition, List<Proposition>>());
            for (Proposition p : propositions) {
                this.data.get(keyId).put(p, new ArrayList<Proposition>());
                storeDerivations(forwardDerivations.get(p), this.data
                        .get(keyId).get(p));
                storeDerivations(backwardDerivations.get(p),
                        this.data.get(keyId).get(p));
            }
        } catch (IOException ex) {
            throw new QueryResultsHandlerProcessingException(ex);
        }
    }

    @Override
    public void validate() {

    }

    /**
     * Returns an empty string array always.
     * 
     * @return an empty {@link String} array. Guaranteed not <code>null</code>.
     */
    @Override
    public String[] getPropositionIdsNeeded() {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }
    
    private void writeLine(String str) throws IOException {
        this.writer.write(str);
        this.writer.write("\n");
    }

    private void storeDerivations(List<Proposition> derivations,
            List<Proposition> outputDerivations) throws IOException {
        if (derivations != null && derivations.size() > 0) {
            for (Proposition d : derivations) {
                outputDerivations.add(d);
            }
        }
    }

    private static class PropositionComparator implements
            Comparator<Proposition> {

        @Override
        public int compare(Proposition o1, Proposition o2) {
            return o1.getId().compareTo(o2.getId());
        }

    }

    private static class PropositionWithDerivations {
        private final Proposition proposition;
        private final List<Proposition> derivations;

        PropositionWithDerivations(Proposition proposition,
                List<Proposition> derivations) {
            this.proposition = proposition;
            this.derivations = derivations;
        }

        public Proposition getProposition() {
            return this.proposition;
        }

        public List<Proposition> getDerivations() {
            return this.derivations;
        }
    }

    private static class PropositionWithDerivationsComparator implements
            Comparator<PropositionWithDerivations> {

        @Override
        public int compare(PropositionWithDerivations o1,
                PropositionWithDerivations o2) {
            if (o1.getProposition().getId().equals(o2.getProposition().getId())) {
                if (o1.getDerivations().size() != o2.getDerivations().size()) {
                    return o1.getDerivations().size()
                            - o2.getDerivations().size();
                } else {
                    for (int i = 0; i < o1.getDerivations().size(); i++) {
                        if (!o1.getDerivations().get(i).getId()
                                .equals(o2.getDerivations().get(i).getId())) {
                            return o1
                                    .getDerivations()
                                    .get(i)
                                    .getId()
                                    .compareTo(
                                            o2.getDerivations().get(i).getId());
                        }
                    }
                    return 0;
                }
            } else {
                return o1.getProposition().getId()
                        .compareTo(o2.getProposition().getId());
            }
        }

    }
}
