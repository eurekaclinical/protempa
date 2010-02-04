package org.protempa.dsb;

import java.util.HashSet;
import java.util.Set;

import org.protempa.backend.BackendInstanceSpec;
import org.protempa.proposition.value.Value;


final class MockTerminologyAdaptor extends AbstractTerminologyAdaptor {

	public Set<String> standardToLocalTerms(String standardTerm) {
		return new HashSet<String>(0);
	}

	public Set<String> localToStandardTerms(String localTerm) {
		return new HashSet<String>(0);
	}

    public void initialize(BackendInstanceSpec config)
            throws TerminologyAdaptorInitializationException {
    }

    public Value localToStandardUnits(String propId, Value value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
