package org.protempa.query.handler;

import java.util.List;
import java.util.Map;

import org.protempa.FinderException;
import org.protempa.KnowledgeSource;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;

/**
 * Interface defining the operations for handling a single result from a
 * Protempa query.
 * 
 * @author Michel Mansour
 * 
 */
public interface QueryResultsHandler {

    /**
     * Performs all initialization functions to prepare the handler. This method
     * is guaranteed to be called by Protempa before any query result processing
     * is done.
     *
     * @throws FinderException
     *             if any exceptions occur at a lower level
     */
    public void init(KnowledgeSource knowledgeSource) throws FinderException;

    /**
     * Performs all clean-up functions for the handler. This method is
     * guaranteed to be called by Protempa as soon as all query results have
     * been retrieved from the data source.
     *
     * @throws FinderException
     *             if any exceptions occur at a lower level
     */
    public void finish() throws FinderException;

    /**
     * Handles a single query result, which is the list of propositions
     * associated with the given key.
     *
     * @param keyId
     *            the identifying key id for the result
     * @param propositions
     *            the proposition results for the given key
     * @param derivationsList a mapping from propositions to derived
     * abstractions.
     * and propositions.
     */
    public void handleQueryResult(String keyId,
            List<Proposition> propositions, 
            Map<Proposition,List<Proposition>> derivations,
            Map<UniqueIdentifier,Proposition> references)
            throws FinderException;
}
