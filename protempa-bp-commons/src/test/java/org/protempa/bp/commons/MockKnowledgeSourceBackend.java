package org.protempa.bp.commons;

import org.protempa.bp.commons.*;

@BackendInfo(displayName = "Mock Knowledge Source Backend")
public class MockKnowledgeSourceBackend 
        extends AbstractCommonsKnowledgeSourceBackend {

    
    private String url;

    public String getUrl() {
        return url;
    }

    @BackendProperty(
        displayName = "URL",
        description = "The URL to the knowledge base"
    )
    public void setUrl(String url) {
        this.url = url;
    }

}
