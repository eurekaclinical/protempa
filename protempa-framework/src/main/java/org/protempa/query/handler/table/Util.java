package org.protempa.query.handler.table;

import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;

/**
 *
 * @author Andrew Post
 */
public class Util {

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
        return StringUtils.join(derivedPropDisplayNames, ',');
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

    static Logger logger() {
        return LazyLoggerHolder.instance;
    }

    private static class LazyLoggerHolder {

        private static Logger instance =
                Logger.getLogger(Util.class.getPackage().getName());
    }
}
