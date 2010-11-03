package org.protempa.ksb.protege;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.protempa.AbstractionDefinition;
import org.protempa.ConstantDefinition;
import org.protempa.EventDefinition;
import org.protempa.KnowledgeBase;
import org.protempa.KnowledgeSourceBackendInitializationException;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PrimitiveParameterDefinition;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.bp.commons.AbstractCommonsKnowledgeSourceBackend;
import org.protempa.proposition.value.AbsoluteTimeUnit;
import org.protempa.proposition.value.RelativeHourUnit;
import org.protempa.proposition.value.Unit;

import edu.stanford.smi.protege.event.ProjectEvent;
import edu.stanford.smi.protege.event.ProjectListener;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;

/**
 * Abstract class for converting a Protege knowledge base into a PROTEMPA
 * knowledge base.
 * 
 * @author Andrew Post
 */
public abstract class ProtegeKnowledgeSourceBackend extends
        AbstractCommonsKnowledgeSourceBackend implements ProjectListener {

    private ConnectionManager cm;
    private Units units;
    private final Map<String, Instance> instanceCache;
    private Cls eventCls;
    private Cls constantCls;
    private Cls abstractParameterCls;
    private Cls primitiveParameterCls;
    private Map<String, List<String>> termToPropMap;

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
        this.termToPropMap = new HashMap<String, List<String>>();
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
                this.constantCls = this.cm.getCls("Constant");
//                Util.logger().log(Level.INFO, "Start terms: " + Calendar.getInstance().getTimeInMillis());
//                this.populateTermToPropMap();
//                Util.logger().log(Level.INFO, "End terms: " + Calendar.getInstance().getTimeInMillis());
            } catch (KnowledgeSourceReadException e) {
                throw new KnowledgeSourceBackendInitializationException(e);
            }
        }
    }

    abstract ConnectionManager initConnectionManager(
            BackendInstanceSpec configuration)
            throws KnowledgeSourceBackendInitializationException;

    ConnectionManager getConnectionManager() {
        return this.cm;
    }

    private void populateTermToPropMap() throws KnowledgeSourceReadException {
        Slot termSlot = cm.getSlot("term");
        Slot termIdSlot = cm.getSlot("termId");
        List<Instance> clsInsts = new ArrayList<Instance>();
        clsInsts.addAll(this.cm.getInstances(this.abstractParameterCls));
        clsInsts.addAll(this.cm.getInstances(this.eventCls));
        clsInsts.addAll(this.cm.getInstances(this.primitiveParameterCls));
        clsInsts.addAll(this.cm.getInstances(this.constantCls));

        for (Instance event : clsInsts) {
            for (Object inst : this.cm.getOwnSlotValues(event, termSlot)) {
                Instance termInst = (Instance) inst;
                String termId = (String) this.cm.getOwnSlotValue(termInst,
                        termIdSlot);
                if (!termToPropMap.containsKey(termId)) {
                    termToPropMap.put(termId, new ArrayList<String>());
                }
                termToPropMap.get(termId).add(event.getName());
            }
        }
    }

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
        this.instanceCache.clear();
        this.abstractParameterCls = null;
        this.eventCls = null;
        this.primitiveParameterCls = null;
        this.constantCls = null;
    }

    @Override
    public ConstantDefinition readConstantDefinition(String name,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        Instance instance = getInstance(name);
        if (instance != null && this.cm.hasType(instance, this.constantCls)) {
            convertIsA(instance, protempaKnowledgeBase);
        }
        return protempaKnowledgeBase.getConstantDefinition(name);
    }

    @Override
    public EventDefinition readEventDefinition(String name,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        Instance instance = getInstance(name);
        if (instance != null && this.cm.hasType(instance, this.eventCls)) {
            convertIsA(instance, protempaKnowledgeBase);
        }
        return protempaKnowledgeBase.getEventDefinition(name);
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

    private boolean hasInstance(String name)
            throws KnowledgeSourceReadException {
        return this.instanceCache.containsKey(name)
                || this.cm.getInstance(name) != null;
    }

    @Override
    public PrimitiveParameterDefinition readPrimitiveParameterDefinition(
            String name, KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        Instance instance = getInstance(name);
        if (instance != null
                && this.cm.hasType(instance, this.primitiveParameterCls)) {
            convertIsA(instance, protempaKnowledgeBase);
            return protempaKnowledgeBase.getPrimitiveParameterDefinition(name);
        } else {
            return null;
        }
    }

    @Override
    public AbstractionDefinition readAbstractionDefinition(String name,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        Instance candidateAbstractParameter = getInstance(name);
        if (candidateAbstractParameter != null
                && this.cm.hasType(candidateAbstractParameter,
                        this.cm.getCls("ParameterConstraint"))) {
            candidateAbstractParameter = (Instance) this.cm.getOwnSlotValue(
                    candidateAbstractParameter,
                    this.cm.getSlot("allowedValueOf"));
        }
        if (candidateAbstractParameter != null
                && this.cm.hasType(candidateAbstractParameter,
                        this.abstractParameterCls)) {
            convertAbstractedFrom(candidateAbstractParameter,
                    protempaKnowledgeBase);
            AbstractionDefinition result = protempaKnowledgeBase
                    .getAbstractionDefinition(name);
            return result;
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
            KnowledgeBase protempaKb) throws KnowledgeSourceReadException {
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
    private void convertIsA(Instance proposition, KnowledgeBase protempaKb)
            throws KnowledgeSourceReadException {
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
    @Override
    public void formChanged(ProjectEvent arg0) {
        this.fireKnowledgeSourceBackendUpdated();
    }

    @Override
    public void projectClosed(ProjectEvent arg0) {
        this.fireKnowledgeSourceBackendUpdated();
    }

    @Override
    public void projectSaved(ProjectEvent arg0) {
        this.fireKnowledgeSourceBackendUpdated();
    }

    @Override
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

    @Override
    public boolean hasAbstractionDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        return hasInstance(id);
    }

    @Override
    public boolean hasEventDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        return hasInstance(id);
    }

    @Override
    public boolean hasConstantDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        return hasInstance(id);
    }

    @Override
    public boolean hasPrimitiveParameterDefinition(String id,
            KnowledgeBase protempaKnowledgeBase)
            throws KnowledgeSourceReadException {
        return hasInstance(id);
    }
}
