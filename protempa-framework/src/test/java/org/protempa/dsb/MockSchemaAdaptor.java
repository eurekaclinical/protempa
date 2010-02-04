package org.protempa.dsb;

import org.protempa.backend.BackendInstanceSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protempa.proposition.ConstantParameter;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.value.AbsoluteTimeGranularityFactory;
import org.protempa.proposition.value.AbsoluteTimeUnitFactory;
import org.protempa.proposition.value.GranularityFactory;
import org.protempa.proposition.value.UnitFactory;


final class MockSchemaAdaptor extends AbstractSchemaAdaptor {

	private final AbsoluteTimeGranularityFactory granularityFactory;
	
	private final AbsoluteTimeUnitFactory unitFactory;

	MockSchemaAdaptor() {
		this.granularityFactory = new AbsoluteTimeGranularityFactory();
		this.unitFactory = new AbsoluteTimeUnitFactory();
	}

	public List<String> getAllKeyIds(int start, int finish) {
		return new ArrayList<String>(0);
	}

	public List<ConstantParameter> getConstantParameters(String keyId, Set<String> paramIds) {
		return new ArrayList<ConstantParameter>(0);
	}

	public Map<String, List<PrimitiveParameter>> getPrimitiveParametersAsc(
			Set<String> paramIds, Long minValid,
			Long maxValid) {
		return new HashMap<String, List<PrimitiveParameter>>(0);
	}

	public  Map<String, List<PrimitiveParameter>> getPrimitiveParametersAsc(
			Set<String> keyIds, Set<String> paramIds,
			Long minValid, Long maxValid) {
		return new HashMap<String, List<PrimitiveParameter>>(0);
	}

	public  List<PrimitiveParameter> getPrimitiveParametersAsc(
			String keyId, Set<String> paramIds,
			Long minValid, Long maxValid) {
		return new ArrayList<PrimitiveParameter>(0);
	}

	public  List<PrimitiveParameter> getPrimitiveParametersDesc(
			String keyId, Set<String> paramIds,
			Long minValid, Long maxValid) {
		return new ArrayList<PrimitiveParameter>(0);
	}

	public  Map<String, List<Event>> getEventsAsc(Set<String> eventIds, 
            Long minValid, Long maxValid) {
		return new HashMap<String, List<Event>>(0);
	}

	public  Map<String, List<Event>> getEventsAsc(
			Set<String> keyIds, Set<String> paramIds,
			Long minValid, Long maxValid) {
		return new HashMap<String, List<Event>>(0);
	}

	public  List<Event> getEventsAsc(String keyId,
			Set<String> eventIds, Long minValid,
			Long maxValid) {
		return new ArrayList<Event>(0);
	}

	public  List<Event> getEventsDesc(String keyId,
			Set<String> eventIds, Long minValid,
			Long maxValid) {
		return new ArrayList<Event>(0);
	}

	public GranularityFactory getGranularityFactory() {
		return this.granularityFactory;
	}

	public String getKeyType() {
		return "CASE";
	}

	public String getKeyTypeDisplayName() {
		return "case";
	}

	public String getKeyTypePluralDisplayName() {
		return "cases";
	}

	public UnitFactory getUnitFactory() {
		return this.unitFactory;
	}

    public void initialize(BackendInstanceSpec config)
            throws SchemaAdaptorInitializationException {
    }

}
