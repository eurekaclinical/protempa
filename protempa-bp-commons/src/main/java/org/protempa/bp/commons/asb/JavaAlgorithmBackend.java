package org.protempa.bp.commons.asb;

import org.protempa.asb.java.AbstractJavaAlgorithmBackend;
import org.protempa.bp.commons.BackendInfo;
import org.protempa.bp.commons.BackendProperty;

/**
 *
 * @author Andrew Post
 */
@BackendInfo(
    displayName="Java Algorithm Backend"
)
public class JavaAlgorithmBackend extends AbstractJavaAlgorithmBackend {
    
    private String algorithms;

    public String getAlgorithms() {
        return algorithms;
    }

    @BackendProperty(
        displayName="Algorithms"
    )
    public void setAlgorithms(String algorithms) {
        this.algorithms = algorithms;
    }

    @Override
    protected String getAlgorithmsPropertiesResourceName() {
        return this.algorithms != null ? this.algorithms
                : super.getAlgorithmsPropertiesResourceName();
    }


}
