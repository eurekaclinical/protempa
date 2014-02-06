package org.protempa.backend.ksb.bioportal;

import org.protempa.AbstractionDefinition;
import org.protempa.ContextDefinition;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PropositionDefinition;
import org.protempa.TemporalPropositionDefinition;
import org.protempa.backend.AbstractCommonsKnowledgeSourceBackend;
import org.protempa.backend.annotations.BackendInfo;
import org.protempa.backend.annotations.BackendProperty;

import java.util.List;

/**
 *
 */
@BackendInfo(displayName = "BioPortal Knowledge Source Backend")
public class BioportalKnowledgeSourceBackend extends AbstractCommonsKnowledgeSourceBackend {

    private String apiKey;
    private String ontologyUrl;

    public String getApiKey() {
        return apiKey;
    }

    @BackendProperty(displayName = "API Key")
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getOntologyUrl() {
        return ontologyUrl;
    }

    @BackendProperty(displayName = "Ontology URL")
    public void setOntologyUrl(String ontologyUrl) {
        this.ontologyUrl = ontologyUrl;
    }

    @Override
    public PropositionDefinition readPropositionDefinition(String id) throws KnowledgeSourceReadException {
        return null;
    }

    @Override
    public AbstractionDefinition readAbstractionDefinition(String id) throws KnowledgeSourceReadException {
        return null;
    }

    @Override
    public ContextDefinition readContextDefinition(String id) throws KnowledgeSourceReadException {
        return null;
    }

    @Override
    public TemporalPropositionDefinition readTemporalPropositionDefinition(String id) throws KnowledgeSourceReadException {
        return null;
    }

    @Override
    public String[] readAbstractedInto(String propId) throws KnowledgeSourceReadException {
        return new String[0];
    }

    @Override
    public String[] readIsA(String propId) throws KnowledgeSourceReadException {
        return new String[0];
    }

    @Override
    public String[] readInduces(String propId) throws KnowledgeSourceReadException {
        return new String[0];
    }

    @Override
    public String[] readSubContextOfs(String propId) throws KnowledgeSourceReadException {
        return new String[0];
    }

    @Override
    public List<String> getKnowledgeSourceSearchResults(String searchKey) throws KnowledgeSourceReadException {
        return null;
    }
}
