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
package org.protempa.dest.table;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceCache;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;

/**
 *
 * @author Andrew Post
 */
public class LinkTraverser {

    private final Set<Proposition> cache;

    public LinkTraverser() {
        this.cache = new HashSet<>();
    }

    /**
     * Traverses links from a proposition to a list of propositions.
     *
     * @param links the {@link Link}s to traverse. If <code>null</code> or an
     * empty array, then the supplied proposition is returned.
     * @param proposition the {@link Proposition} from which to start. Cannot be
     * <code>null</code>.
     * @param forwardDerivations map of propositions from raw data toward
     * derived propositions.
     * @param backwardDerivations map of propositions from derived propositions
     * toward raw data.
     * @param references a map of unique id to the corresponding proposition for
     * propositions that are referred to by other propositions.
     * @param knowledgeSource the {@link KnowledgeSource}.
     * @return the list of {@link Propositions} at the end of the traversals.
     * @throws KnowledgeSourceReadException if an error occurred reading from
     * the knowledge source.
     */
    public List<Proposition> traverseLinks(Link[] links,
            Proposition proposition,
            Map<Proposition, Set<Proposition>> forwardDerivations,
            Map<Proposition, Set<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references,
            KnowledgeSourceCache ksCache) {
        LinkedList<Proposition> result = new LinkedList<>();
        Logger logger = Util.logger();
        result.add(proposition);
        if (links != null) {
            int num = 1;
            for (Link link : links) {
                int j = 0;
                while (j < num) {
                    Proposition prop = result.remove();
                    Collection<Proposition> c = link.traverse(prop,
                            forwardDerivations, backwardDerivations,
                            references, ksCache, this.cache);
                    result.addAll(c);
                    j++;
                }
                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "{0} traversed to {1} with {2}",
                            new Object[]{getClass().getName(), result, link});
                }
                num = result.size();
                this.cache.clear();
            }
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "{0} traversed to {1}",
                    new Object[]{getClass().getName(), result});
        }
        return result;
    }
}
