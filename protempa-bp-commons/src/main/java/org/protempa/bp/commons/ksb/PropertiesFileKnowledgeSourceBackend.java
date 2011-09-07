package org.protempa.bp.commons.ksb;

import org.protempa.bp.commons.BackendInfo;
import org.protempa.bp.commons.BackendProperty;
import org.protempa.bp.commons.CommonsBackend;
import org.protempa.backend.ksb.AbstractPropertiesFileKnowledgeSourceBackend;

/**
 *
 * @author Andrew Post
 */
@BackendInfo(displayName = "Properties File Knowledge Source Backend")
public class PropertiesFileKnowledgeSourceBackend
        extends AbstractPropertiesFileKnowledgeSourceBackend {

    private String primitiveParameterDefinitions;
    private String eventDefinitions;
    private String constantDefinitions;

    public String getEventDefinitions() {
        return eventDefinitions;
    }

    @BackendProperty(displayName = "Constant Definitions")
    public void setConstantDefinitions(String constantDefinitions) {
        this.constantDefinitions = constantDefinitions;
    }

    public String getConstantDefinitions() {
        return this.constantDefinitions;
    }

    @BackendProperty(displayName = "Event Definitions")
    public void setEventDefinitions(String eventDefinitions) {
        this.eventDefinitions = eventDefinitions;
    }

    public String getPrimitiveParameterDefinitions() {
        return primitiveParameterDefinitions;
    }

    @BackendProperty(displayName = "Primitive Parameter Definitions")
    public void setPrimitiveParameterDefinitions(
            String primitiveParameterDefinitions) {
        this.primitiveParameterDefinitions = primitiveParameterDefinitions;
    }

    @Override
    protected String getEventDefinitionsPropertiesResourceName() {
        return this.eventDefinitions;
    }

    @Override
    protected String getPrimitiveParameterDefinitionsPropertiesResourceName() {
        return this.primitiveParameterDefinitions;
    }

    @Override
    protected String getConstantDefinitionsPropertiesResourceName() {
        return this.constantDefinitions;
    }

    @Override
    public String getDisplayName() {
        return CommonsBackend.backendInfo(this).displayName();
    }
}
