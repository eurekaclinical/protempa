package org.protempa.query.handler;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.protempa.FinderException;
import org.protempa.KnowledgeSource;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueId;

/**
 * Implements de-identification. Only replaces key ids so far.
 *
 * @author Andrew Post
 */
public final class DeidentifyQueryResultsHandler
        implements QueryResultsHandler {

    private static final long serialVersionUID = 4289223507110468993L;
    private static final NumberFormat disguisedKeyFormat =
            NumberFormat.getInstance();
    private boolean keyIdDisguised;
    private final QueryResultsHandler handler;
    private final Map<String, String> keyMapper;
    private int nextDisguisedKey;

    public DeidentifyQueryResultsHandler(QueryResultsHandler handler) {
        if (handler == null)
            throw new IllegalArgumentException("handler cannot be null");
        this.handler = handler;
        this.keyMapper = new HashMap<String, String>();
        this.keyIdDisguised = true;
        this.nextDisguisedKey = 1;
    }

    /**
     * Returns whether key ids will be disguised. Default is <code>true</code>.
     * 
     * @return <code>true</code> or <code>false</code>.
     */
    public boolean isKeyIdDisguised() {
        return keyIdDisguised;
    }

    /**
     * Sets whether to disguise key ids.
     *
     * @param keyDisguised <code>true</code> or <code>false</code>.
     */
    public void setKeyIdDisguised(boolean keyDisguised) {
        this.keyIdDisguised = keyDisguised;
    }

    @Override
    public void handleQueryResult(String keyId, List<Proposition> propositions,
            Map<Proposition, List<Proposition>> forwardDerivations,
            Map<Proposition, List<Proposition>> backwardDerivations,
            Map<UniqueId, Proposition> references)
            throws FinderException {
        keyId = disguiseKeyIds(keyId);
        this.handler.handleQueryResult(keyId, propositions, forwardDerivations,
                backwardDerivations, references);
    }

    @Override
    public void init(KnowledgeSource knowledgeSource) throws FinderException {
        this.handler.init(knowledgeSource);
        this.nextDisguisedKey = 1;
    }

    @Override
    public void finish() throws FinderException {
        this.handler.finish();
        this.keyMapper.clear();
    }

    private String disguiseKeyIds(String keyId) {
        if (this.keyIdDisguised) {
            if (this.keyMapper.containsKey(keyId)) {
                keyId = this.keyMapper.get(keyId);
            } else {
                keyId = disguisedKeyFormat.format(nextDisguisedKey++);
            }
        }
        return keyId;
    }
}
