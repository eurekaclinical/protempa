package org.protempa.query.handler.table;

import java.util.List;
import java.util.Map;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;
import org.protempa.query.handler.QueryResultsHandlerValidationFailedException;

/**
 * Specification of a column or sequence of columns in a delimited file
 * that represent a proposition or property.
 */
public interface TableColumnSpec {

    /**
     * Gets the names of the columns representing one instance of a
     * proposition or property. These columns may be repeated if the
     * specification results in matching more than one proposition or
     * property.
     *
     * @param knowledgeSource the active {@link KnowledgeSource}.
     * @return a {@link String[]} of column names.
     * @throws KnowledgeSourceReadException if an attempt at reading from
     * the knowledge source failed.
     */
    String[] columnNames(KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException;

    /**
     * Gets the values of the specified propositions or properties for
     * one row of data.
     *
     * @param key a key id {@link String}.
     * @param proposition a {@link List<Proposition>} for the specified
     * key with the specified proposition id.
     * @param knowledgeSource the active {@link KnowledgeSource}.
     * @return a {@link String[]} of column values.
     * @throws KnowledgeSourceReadException if an attempt at reading from
     * the knowledge source failed.
     */
    String[] columnValues(String key, Proposition proposition, 
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references,
            KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException;
    
    /**
     * Validates the fields of this column specification against the
     * knowledge source.
     * 
     * @param knowledgeSource a {@link KnowledgeSource}. Guaranteed not
     * <code>null</code>.
     * 
     * @throws QueryResultsHandlerValidationFailedException if validation
     * failed.
     * @throws KnowledgeSourceReadException if the knowledge source could
     * not be read.
     */
    void validate(KnowledgeSource knowledgeSource) throws
                TableColumnSpecValidationFailedException,
                KnowledgeSourceReadException;
}
