package org.protempa.ksb.protege;

import edu.stanford.smi.protege.event.ProjectEvent;
import edu.stanford.smi.protege.event.ProjectListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import java.util.HashMap;
import java.util.Map;
import org.protempa.AbstractionDefinition;
import org.protempa.EventDefinition;
import org.protempa.KnowledgeBase;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceBackendInitializationException;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PrimitiveParameterDefinition;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.bp.commons.AbstractCommonsKnowledgeSourceBackend;
import org.protempa.proposition.value.AbsoluteTimeUnit;
import org.protempa.proposition.value.RelativeHourUnit;
import org.protempa.proposition.value.Unit;

/**
 * Abstract class for converting a Protege knowledge base into a PROTEMPA
 * knowledge base.
 * 
 * @author Andrew Post
 */
public abstract class ProtegeKnowledgeSourceBackend extends
		AbstractCommonsKnowledgeSourceBackend implements ProjectListener {

	private ConnectionManager cm;

	private KnowledgeSource knowledgeSource;

	private Units units;

	private final Map<String, Instance> instanceCache;

	private Cls eventCls;

	private Cls abstractParameterCls;

	private Cls primitiveParameterCls;

	private static enum Units {
		ABSOLUTE(AbsoluteTimeUnit.class), RELATIVE_HOURS(RelativeHourUnit.class);

		private Class<? extends Unit> unitClass;

		Units(Class<? extends Unit> unitClass) {
			this.unitClass = unitClass;
		}

		Class<? extends Unit> getUnitClass() {
			return this.unitClass;
		}
	};

    protected ProtegeKnowledgeSourceBackend() {
        this.instanceCache = new HashMap<String, Instance>();
    }

	@Override
	public void initialize(BackendInstanceSpec config)
            throws KnowledgeSourceBackendInitializationException {
		super.initialize(config);
		if (this.cm == null) {
			ConnectionManager pcm = initConnectionManager(config);

			pcm.init();
            this.cm = pcm;
            try {
                this.abstractParameterCls = this.cm.getCls("AbstractParameter");
                this.eventCls = this.cm.getCls("Event");
                this.primitiveParameterCls = this.cm
                        .getCls("PrimitiveParameter");
            } catch (KnowledgeSourceReadException e) {
                throw new KnowledgeSourceBackendInitializationException(e);
            }
		}
	}

	abstract ConnectionManager initConnectionManager(
            BackendInstanceSpec configuration)
            throws KnowledgeSourceBackendInitializationException;

	protected void initUnits(String unitsStr) 
            throws KnowledgeSourceBackendInitializationException {
		if (unitsStr != null) {
			try {
				this.units = Units.valueOf(unitsStr);
			} catch (Exception e) {
				throw new KnowledgeSourceBackendInitializationException(
						"Invalid units supplied: " + unitsStr);
			}
		} else {
			Util.logger().fine("No UNITS supplied, using ABSOLUTE");
		}
		if (this.units == null) {
			this.units = Units.ABSOLUTE;
		}
	}

	@Override
	public void close() {
		if (this.cm != null) {
			this.cm.close();
			this.cm = null;
		}
		this.knowledgeSource = null;
		this.instanceCache.clear();
		this.abstractParameterCls = null;
		this.eventCls = null;
		this.primitiveParameterCls = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.virginia.pbhs.protempa.AbstractKnowledgeSourceBackend#readEventDefinition(java.lang.String,
	 *      edu.virginia.pbhs.protempa.KnowledgeBase)
	 */
	@Override
	public EventDefinition readEventDefinition(String name,
			KnowledgeBase protempaKnowledgeBase) 
            throws KnowledgeSourceReadException {
		if (protempaKnowledgeBase != null && name != null) {
			Instance instance = getInstance(name);
			if (instance != null && this.cm.hasType(instance, this.eventCls)) {
				convertIsA(instance, protempaKnowledgeBase, knowledgeSource);
			}
			return protempaKnowledgeBase.getEventDefinition(name);
		} else {
			return null;
		}
	}

	private Instance getInstance(String name) 
            throws KnowledgeSourceReadException {
		Instance instance;
		if ((instance = this.instanceCache.get(name)) == null) {
			instance = this.cm.getInstance(name);
			this.instanceCache.put(name, instance);
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.virginia.pbhs.protempa.AbstractKnowledgeSourceBackend#readPrimitiveParameterDefinition(java.lang.String,
	 *      edu.virginia.pbhs.protempa.KnowledgeBase)
	 */
	@Override
	public PrimitiveParameterDefinition readPrimitiveParameterDefinition(
			String name, KnowledgeBase protempaKnowledgeBase)
        throws KnowledgeSourceReadException {
		if (protempaKnowledgeBase != null && name != null) {
			Instance instance = getInstance(name);
			if (instance != null
					&& this.cm.hasType(instance, this.primitiveParameterCls)) {
				convertIsA(instance, protempaKnowledgeBase, knowledgeSource);
				return protempaKnowledgeBase
						.getPrimitiveParameterDefinition(name);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.virginia.pbhs.protempa.AbstractKnowledgeSourceBackend#readAbstractionDefinition(java.lang.String,
	 *      edu.virginia.pbhs.protempa.KnowledgeBase)
	 */
	@Override
	public AbstractionDefinition readAbstractionDefinition(String name,
			KnowledgeBase protempaKnowledgeBase) 
            throws KnowledgeSourceReadException {
		if (protempaKnowledgeBase != null && name != null) {
			Instance candidateAbstractParameter = getInstance(name);
			if (candidateAbstractParameter != null
					&& this.cm.hasType(candidateAbstractParameter, this.cm
							.getCls("ParameterConstraint"))) {
				candidateAbstractParameter = (Instance) this.cm
						.getOwnSlotValue(candidateAbstractParameter, this.cm
								.getSlot("allowedValueOf"));
			}
			if (candidateAbstractParameter != null
					&& this.cm.hasType(candidateAbstractParameter,
							this.abstractParameterCls)) {
				convertAbstractedFrom(candidateAbstractParameter,
						protempaKnowledgeBase);
				AbstractionDefinition result =
                        protempaKnowledgeBase.getAbstractionDefinition(name);
                return result;
			} else {
				return null;
			}

		} else {
			return null;
		}
	}

	/**
	 * Converts the given proposition instance and all proposition instances in
	 * its dependency tree, which is defined by proposition instances connected
	 * by the abstractedFrom relationship.
	 * 
	 * @param proposition
	 *            a proposition instance.
	 * @param protegeKb
	 *            a Protege
	 *            <code>edu.stanford.smi.protege.model.KnowledgeBase</code>
	 *            instance.
	 * @param protempaKb
	 *            a PROTEMPA <code>KnowledgeBase</code> instance.
	 */
	private void convertAbstractedFrom(Instance proposition,
			KnowledgeBase protempaKb) {
		if (proposition != null && protempaKb != null) {
			PropositionConverter converter = InstanceConverterFactory
					.getInstance(proposition);
			if (converter != null
					&& !converter.protempaKnowledgeBaseHasProposition(
							proposition, protempaKb)) {
				converter.convert(proposition, protempaKb, this);
			}
		}
	}

	/**
	 * Converts the given proposition instance and all proposition instances in
	 * its dependency tree, which is defined by proposition instances connected
	 * by the isA relationship.
	 * 
	 * @param proposition
	 *            a proposition instance.
	 * @param protegeKb
	 *            a Protege
	 *            <code>edu.stanford.smi.protege.model.KnowledgeBase</code>
	 *            instance.
	 * @param protempaKb
	 *            a PROTEMPA <code>KnowledgeBase</code> instance.
	 */
	private void convertIsA(Instance proposition, KnowledgeBase protempaKb,
			KnowledgeSource knowledgeSource) {
		if (proposition != null && protempaKb != null) {
			PropositionConverter converter = InstanceConverterFactory
					.getInstance(proposition);
			if (converter != null
					&& !converter.protempaKnowledgeBaseHasProposition(
							proposition, protempaKb)) {
				converter.convert(proposition, protempaKb, this);
			}
		}
	}

	/*
	 * PROJECT CHANGED EVENTS. Protege calls projectClosed() when a remote
	 * project has changed.
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.stanford.smi.protege.event.ProjectListener#formChanged(edu.stanford.smi.protege.event.ProjectEvent)
	 */
	public void formChanged(ProjectEvent arg0) {
		this.fireKnowledgeSourceBackendUpdated();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.stanford.smi.protege.event.ProjectListener#projectClosed(edu.stanford.smi.protege.event.ProjectEvent)
	 */
	public void projectClosed(ProjectEvent arg0) {
		this.fireKnowledgeSourceBackendUpdated();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.stanford.smi.protege.event.ProjectListener#projectSaved(edu.stanford.smi.protege.event.ProjectEvent)
	 */
	public void projectSaved(ProjectEvent arg0) {
		this.fireKnowledgeSourceBackendUpdated();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.stanford.smi.protege.event.ProjectListener#runtimeClsWidgetCreated(edu.stanford.smi.protege.event.ProjectEvent)
	 */
	public void runtimeClsWidgetCreated(ProjectEvent arg0) {
		this.fireKnowledgeSourceBackendUpdated();
	}

	Unit parseUnit(String protegeUnitStr) {
		switch (this.units) {
		case ABSOLUTE:
			return Util.ABSOLUTE_DURATION_MULTIPLIER.get(protegeUnitStr);
		case RELATIVE_HOURS:
			return Util.RELATIVE_HOURS_DURATION_MULTIPLIER
					.get(protegeUnitStr);
		default:
			assert false : this.units;
			return null;
		}
	}
}
