package org.protempa;

/**
 * Constructs a string containing a proposition definition's abbreviated display
 * name if it exists, otherwise the full display name.
 * 
 * @author Andrew Post
 */
public class PropositionDefinitionFormat {

    /**
     * Constructs a string containing the named proposition definition's
     * abbreviated display name if it exists, otherwise the full display name.
     *
     * @param propId
     *            the id {@link String} of a proposition definition.
     * @param ks
     *            the {@link KnowledgeSource} containing the proposition
     *            definition.
     * @return a {@link String}.
     */
    public String shortDisplayName(String propId, KnowledgeSource ks)
            throws KnowledgeSourceReadException {
        if (ks != null) {
            return shortDisplayName(ks.readPropositionDefinition(propId));
        } else {
            return propId;
        }
    }

    /**
     * Constructs a string containing a proposition definition's abbreviated
     * display name if it exists, otherwise the full display name.
     *
     * @param def
     *            a {@link PropositionDefinition}.
     * @return a {@link String}.
     */
    public String shortDisplayName(PropositionDefinition def) {
        if (def == null) {
            return null;
        }
        String abbrevName = def.getAbbreviatedDisplayName();
        if (abbrevName.length() > 0) {
            return abbrevName;
        } else {
            return def.getDisplayName();
        }
    }
}
