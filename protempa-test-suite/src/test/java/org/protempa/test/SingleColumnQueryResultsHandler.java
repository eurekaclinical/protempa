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

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;

import org.protempa.dest.AbstractQueryResultsHandler;
import org.protempa.dest.QueryResultsHandlerCloseException;
import org.protempa.dest.QueryResultsHandlerProcessingException;

final class SingleColumnQueryResultsHandler
        extends AbstractQueryResultsHandler {

    private final Map<String, Map<Proposition, Set<? extends Proposition>>> data;
    private final BufferedWriter writer;

    /**
     * Creates a new instance that will write to the given writer. The finish()
     * method will close the writer.
     *
     * @param writer the {@link Writer} to output to
     */
    SingleColumnQueryResultsHandler(BufferedWriter writer) {
        this.data = new HashMap<>();
        this.writer = writer;
    }

    @Override
    public void handleQueryResult(String keyId, List<Proposition> propositions, 
            Map<Proposition, Set<Proposition>> forwardDerivations, 
            Map<Proposition, Set<Proposition>> backwardDerivations, 
            Map<UniqueId, Proposition> references) 
            throws QueryResultsHandlerProcessingException {
        Map<Proposition, Set<? extends Proposition>> result = new HashMap<>();
        for (Proposition p : propositions) {
            if (p.getCreateDate() == null) {
                throw new QueryResultsHandlerProcessingException("invalid proposition with no create date: " + p);
            }
            Set<Proposition> allDerivations = new HashSet<>();
            allDerivations.addAll(forwardDerivations.getOrDefault(p, Collections.emptySet()));
            allDerivations.addAll(backwardDerivations.getOrDefault(p, Collections.emptySet()));
            result.put(p, allDerivations);
        }
        this.data.put(keyId, result);
    }

    @Override
    public void finish() throws QueryResultsHandlerProcessingException {
        try {
            SortedSet<String> sortedKeyIds = new TreeSet<>(this.data.keySet());
            for (String keyId : sortedKeyIds) {
                writeLine(keyId);
                List<PropositionWithDerivations> sortedProps = new ArrayList<>();
                for (Map.Entry<Proposition, Set<? extends Proposition>> pp : this.data.get(keyId).entrySet()) {
                    sortedProps.add(new PropositionWithDerivations(pp.getKey(), pp.getValue()));
                }
                Collections.sort(sortedProps, new PropositionWithDerivationsComparator());
                for (PropositionWithDerivations pwd : sortedProps) {
                    writeLine("PROPOSITION " + pwd.getProposition().getId());
                    List<Proposition> sortedDerivations = new ArrayList<>(pwd.getDerivations());
                    Collections.sort(sortedDerivations, new PropositionComparator());
                    for (Proposition d : sortedDerivations) {
                        writeLine("DERIVATION " + d.getId());
                    }
                }
            }
        } catch (IOException e) {
            throw new QueryResultsHandlerProcessingException(e);
        }
    }

    @Override
    public void close() throws QueryResultsHandlerCloseException {
        this.data.clear();
    }
    
    private void writeLine(String str) throws IOException {
        this.writer.write(str);
        this.writer.newLine();
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
                Set<? extends Proposition> derivations) {
            this.proposition = proposition;
            this.derivations = new ArrayList<>(derivations);
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
