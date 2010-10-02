package org.protempa.query.handler.table;

import org.arp.javautil.arrays.Arrays;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;

/**
 *
 * @author Andrew Post
 */
public class Util {

    static final String NULL_COLUMN = "(empty)";

    private Util() {}

    static String propositionDefinitionDisplayNames(String[] propIds,
            KnowledgeSource knowledgeSource)
            throws KnowledgeSourceReadException {
        String[] derivedPropDisplayNames = new String[propIds.length];
        for (int i = 0; i < propIds.length; i++) {
            PropositionDefinition propDef =
                    knowledgeSource.readPropositionDefinition(propIds[i]);
            derivedPropDisplayNames[i] =
                    Util.propositionDefinitionDisplayName(propDef);
        }
        return Arrays.join(derivedPropDisplayNames, ",");
    }

    static String propositionDefinitionDisplayName(
            PropositionDefinition propositionDefinition) {
        String abbrevDisplayName =
                propositionDefinition.getAbbreviatedDisplayName();
        if (abbrevDisplayName.length() > 0) {
            return abbrevDisplayName;
        } else {
            String displayName = propositionDefinition.getDisplayName();
            if (displayName.length() > 0) {
                return displayName;
            } else {
                return propositionDefinition.getId();
            }
        }
    }
}
