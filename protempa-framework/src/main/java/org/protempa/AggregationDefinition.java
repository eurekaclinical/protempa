package org.protempa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Segment;
import org.protempa.proposition.value.BooleanValue;
import org.protempa.proposition.value.Unit;
import org.protempa.proposition.value.Value;


public class AggregationDefinition extends AbstractAbstractionDefinition
		implements PatternFinderUser {

	private static final long serialVersionUID = 347148747748359831L;

	private String algorithmId;

	private Class valueType;

	private final Set<String> paramIds;

	/**
	 * Stores the value of the skip-start restart search directive. Value must
	 * be > 0, -1 if disabled.
	 */
	private int skipStart = -1;

	/**
	 * Stores the value of the skip-end restart search directive. Value must be >
	 * 0, -1 if disabled.
	 */
	private int skipEnd = -1;

	/**
	 * Stores the value of the skip restart search directive. Value must be > 0,
	 * -1 if disabled.
	 */
	private int skip = -1;

	/**
	 * Stores the value of the max-overlapping restart search directive. Value
	 * must be > 0, -1 if disabled.
	 */
	private int maxOverlapping = -1;

	private final Map<List<Object>, Object> resultCache = new HashMap<List<Object>, Object>();

	private SlidingWindowWidthMode slidingWindowWidthMode = SlidingWindowWidthMode.DEFAULT;

	private int maximumPatternLength = Integer.MAX_VALUE;

	private int minimumPatternLength = 1;

	private Integer minimumDuration = 0;

	private Unit minimumDurationUnits;

	private Integer maximumDuration = null;

	private Unit maximumDurationUnits;

	private final MinMaxGapBetweenValues gapBtwValues;

	public AggregationDefinition(KnowledgeBase kb, String id) {
		super(kb, id);
		paramIds = new HashSet<String>();
		this.gapBtwValues = new MinMaxGapBetweenValues();
	}

	public final void addPrimitiveParameter(String paramId) {
		this.paramIds.add(paramId);
		recalculateDirectChildren();
	}

	public final Set<String> getPrimitiveParameters() {
		return Collections.unmodifiableSet(paramIds);
	}

	public boolean getAbstractedFrom(String abstractParamId) {
		return false;
	}

	public Set<String> getAbstractedFrom() {
		return Collections.emptySet();
	}

	public String getAlgorithmId() {
		return algorithmId;
	}

	public void setAlgorithmId(String algorithmId) {
		this.algorithmId = algorithmId;
	}

	public Class getValueType() {
		return valueType;
	}

	public void setValueType(Class valueType) {
		this.valueType = valueType;
	}

	// private Map getResultCache() {
	// return resultCache;
	// }

	// private static class TrueCondition implements Condition {
	// 
	// private static final long serialVersionUID = -4553725098910530892L;
	//
	// private final Declaration[] declarations;
	//
	// private final AggregationDefinition def;
	//
	// private final Algorithm algorithm;
	//
	// public TrueCondition(Declaration[] declarations,
	// AggregationDefinition rule, Algorithm algorithm) {
	// this.declarations = declarations;
	// this.def = rule;
	// this.algorithm = algorithm;
	// }
	//
	// public Declaration[] getRequiredTupleMembers() {
	// return declarations;
	// }
	//
	// public boolean isAllowed(Tuple arg0) throws ConditionException {
	// Segment seg = (Segment) arg0.get(declarations[0]);
	// // Note: detector.satisfiedByHelper checks for a cached result.
	// return def.satisfiedByHelper(seg, algorithm);
	// }
	// }
	//
	// private static class TrueConsequence implements Consequence {
	// 
	// private static final long serialVersionUID = 2455607587534331595L;
	//
	// private AggregationDefinition def;
	//
	// private Declaration[] declarations;
	//
	// public TrueConsequence(Declaration[] declarations,
	// AggregationDefinition detector) {
	// this.def = detector;
	// this.declarations = declarations;
	// }
	//
	// public void invoke(Tuple arg0) throws ConsequenceException {
	// Segment seg = (Segment) arg0.get(declarations[0]);
	// List key = new ArrayList(3);
	// key.add(seg.getSequence().getPropositionIds());
	// key.add(new Integer(seg.getFirstIndex()));
	// key.add(new Integer(seg.getLastIndex()));
	//
	// AbstractParameter abstractParameter = AbstractParameterFactory
	// .getFromAbstraction(def.getId(), seg, (Value) def
	// .getResultCache().remove(key), null);
	//
	// try {
	// arg0.getWorkingMemory().assertObject(abstractParameter);
	// arg0.getWorkingMemory().retractObject(
	// arg0.getFactHandleForObject(seg));
	//
	// Segment nextSeg = LowLevelAbstractionFinder
	// .nextSegmentAfterMatch(def, seg);
	//
	// if (nextSeg != null) {
	// arg0.getWorkingMemory().assertObject(nextSeg);
	// }
	// } catch (FactException e) {
	// throw new ConsequenceException(e);
	// }
	// }
	// }

    public void accept(PropositionDefinitionVisitor processor) {
//        if (processor == null) {
//            throw new IllegalArgumentException("processor cannot be null.");
//        }
//		processor.visit(this);
	}

    public void acceptChecked(PropositionDefinitionCheckedVisitor processor)
            throws ProtempaException {
//        if (processor == null) {
//            throw new IllegalArgumentException("processor cannot be null.");
//        }
//        processor.visit(this);
    }

	//public void accept(PropositionDefinitionVisitor processor) {
		// processor.doProcess(this);
		// List l = new ArrayList(2);
		// try {
		// org.drools.rule.Rule tRule = new org.drools.rule.Rule('t' + getId()
		// + "_" + getId());
		// tRule.addParameterDeclaration("segment", new SegmentObjectType(
		// getId()));
		// Declaration[] declarations = (Declaration[]) tRule
		// .getParameterDeclarations().toArray(new Declaration[1]);
		// TrueCondition tCond = new TrueCondition(declarations, this,
		// algorithms.readAlgorithm(algorithmId));
		// tRule.addCondition(tCond);
		// tRule.setConsequence(new TrueConsequence(declarations, this));
		// HashMap applicationData = new HashMap();
		// applicationData.put("abstractionId", getId());
		// tRule.setApplicationData(applicationData);
		//
		// org.drools.rule.Rule fRule = new org.drools.rule.Rule('f' + getId()
		// + "_" + getId());
		// fRule.addParameterDeclaration("segment", new SegmentObjectType(
		// getId()));
		// declarations = (Declaration[]) tRule.getParameterDeclarations()
		// .toArray(new Declaration[1]);
		// FalseCondition fCond = new FalseCondition(declarations, this,
		// algorithms.readAlgorithm(algorithmId));
		// fRule.addCondition(fCond);
		// fRule.setConsequence(new FalseConsequence(declarations, this,
		// algorithms.readAlgorithm(algorithmId)));
		// fRule.setApplicationData(applicationData);
		//
		// l.add(tRule);
		// l.add(fRule);
		// } catch (InvalidRuleException e) {
		// ProtempaUtil.logger().log(Level.SEVERE,
		// "Could not create rules from " + toString() + ".", e);
		// }

		// return l;
	//}

	/**
	 * Returns whether or not a time series segment satisfies the constraints of
	 * an algorithm.
	 * 
	 * @param seg
	 *            a time series <code>Segment</code>.
	 * @param algorithm
	 *            an <code>Algorithm</code>, or <code>null</code> to
	 *            specify no algorithm.
	 * @return <code>null</code> if the time series segment does not satisfy
	 *         the constraints of the given algorithm, or an algorithm-specific
	 *         <code>Value</code> if it does. If no algorithm is specified,
	 *         <code>BooleanValue.TRUE</code> is returned.
	 * @throws AlgorithmProcessingException
	 */
	final Value satisfiedBy(Segment<PrimitiveParameter> seg, Algorithm algorithm)
			throws AlgorithmProcessingException {
		if (algorithm != null) {
			configure(algorithm);
			Value result = algorithm.compute(seg, null);
			return result;
		} else {
			return BooleanValue.TRUE;
		}
	}

	public final Integer getMinimumGapBetweenValues() {
		return this.gapBtwValues.getMinimumGapBetweenValues();
	}

	public final Unit getMinimumGapBetweenValuesUnits() {
		return this.gapBtwValues.getMinimumGapBetweenValuesUnits();
	}

	public final void setMinimumGapBetweenValues(Integer minimumGapBetweenValues) {
		this.gapBtwValues.setMinimumGapBetweenValues(minimumGapBetweenValues);
	}

	public final void setMinimumGapBetweenValuesUnits(Unit units) {
		this.gapBtwValues.setMinimumGapBetweenValuesUnits(units);
	}

	public final Integer getMaximumGapBetweenValues() {
		return this.gapBtwValues.getMaximumGapBetweenValues();
	}

	public final Unit getMaximumGapBetweenValuesUnits() {
		return this.gapBtwValues.getMaximumGapBetweenValuesUnits();
	}

	public final void setMaximumGapBetweenValues(Integer maximumGapBetweenValues) {
		this.gapBtwValues.setMaximumGapBetweenValues(maximumGapBetweenValues);
	}

	public final void setMaximumGapBetweenValuesUnits(Unit units) {
		this.gapBtwValues.setMaximumGapBetweenValuesUnits(units);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.PatternFinderUser#setMaximumPatternLength(int)
	 */
	public final void setMaximumNumberOfValues(int l) {
		if (l < 1) {
			throw new IllegalArgumentException(
					"maximumNumberOfValues must be > 0");
		}
		this.maximumPatternLength = l;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.SimpleRule#getMaximumPatternLength()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.PatternFinderUser#getMaximumPatternLength()
	 */
	public final int getMaximumNumberOfValues() {
		return this.maximumPatternLength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.PatternFinderUser#setMinimumPatternLength(int)
	 */
	public final void setMinimumNumberOfValues(int l) {
		if (l < 1) {
			throw new IllegalArgumentException(
					"minimumNumberOfValues must be > 0");
		}
		this.minimumPatternLength = l;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.SimpleRule#getMinimumPatternLength()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.PatternFinderUser#getMinimumPatternLength()
	 */
	public final int getMinimumNumberOfValues() {
		return this.minimumPatternLength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.PatternFinderUser#setSkipStart(int)
	 */
	public final void setSkipStart(int arg) {
		skipStart = arg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.PatternFinderUser#unsetSkipStart()
	 */
	public final void unsetSkipStart() {
		skipStart = -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.PatternFinderUser#getSkipStart()
	 */
	public final int getSkipStart() {
		return skipStart;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.PatternFinderUser#setSkipEnd(int)
	 */
	public final void setSkipEnd(int arg) {
		skipEnd = arg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.PatternFinderUser#unsetSkipEnd()
	 */
	public final void unsetSkipEnd() {
		skipEnd = -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.PatternFinderUser#getSkipEnd()
	 */
	public final int getSkipEnd() {
		return skipEnd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.PatternFinderUser#setSkip(int)
	 */
	public final void setSkip(int arg) {
		skip = arg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.PatternFinderUser#unsetSkip()
	 */
	public final void unsetSkip() {
		skip = -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.PatternFinderUser#getSkip()
	 */
	public final int getSkip() {
		return skip;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.PatternFinderUser#setMaxOverlapping(int)
	 */
	public final void setMaxOverlapping(int arg) {
		maxOverlapping = arg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.PatternFinderUser#unsetMaxOverlapping()
	 */
	public final void unsetMaxOverlapping() {
		maxOverlapping = -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.protempa.PatternFinderUser#getMaxOverlapping()
	 */
	public final int getMaxOverlapping() {
		return maxOverlapping;
	}

	private boolean satisfiesOverlapAndGap(Segment<PrimitiveParameter> segment) {
		PrimitiveParameter eprev = segment.get(0);
		for (int i = 1, n = segment.size(); i < n; i++) {
			PrimitiveParameter e = segment.get(i);
			if (!this.gapBtwValues.satisfiesGap(eprev, e)) {
				return false;
			}
			eprev = e;
		}
		return true;
	}

	/**
	 * Configures the detector prior to testing its condition. Will only be
	 * called if the algorithm has been set.
	 * 
	 * @param algorithm
	 *            the <code>Algorithm</code> to be used. Guaranteed to be not
	 *            <code>null</code>.
	 */
	protected void configure(Algorithm algorithm) {

	}

	/**
	 * Test whether or not the given time series satisfies the constraints of
	 * this detector and an optional algorithm. If no algorithm is specified,
	 * then this test just uses the detector's constraints.
	 * 
	 * @param segment
	 *            a time series <code>Segment</code>.
	 * @param algorithm
	 *            an <code>Algorithm</code>, or <code>null</code> to
	 *            specify no algorithm.
	 * @return <code>true</code> if the time series segment satisfies the
	 *         constraints of this detector, <code>false</code> otherwise
	 * @throws AlgorithmProcessingException
	 */
	final boolean satisfiedByHelper(Segment<PrimitiveParameter> segment,
			Algorithm algorithm) throws AlgorithmProcessingException {
		List<Object> key = new ArrayList<Object>(3);
		key.add(segment.getSequence().getPropositionIds());
		key.add(segment.getFirstIndex());
		key.add(segment.getLastIndex());

		Object result = null;

		if (resultCache.containsKey(key)) {
			result = resultCache.get(key);
		} else {
			if (satisfiesOverlapAndGap(segment)) {
				result = satisfiedBy(segment, algorithm);
			}

			resultCache.put(key, result);
		}

		return result != null;
	}

	public final void setMinimumDuration(Integer minDuration) {
		if (minDuration == null) {
			this.minimumDuration = 0;
		} else {
			this.minimumDuration = minDuration;
		}
	}

	public final void setMinimumDurationUnits(Unit minDurationUnits) {
		this.minimumDurationUnits = minDurationUnits;
	}

	public final void setMaximumDuration(Integer maxDuration) {
		this.maximumDuration = maxDuration;
	}

	public final void setMaximumDurationUnits(Unit maxDurationUnits) {
		this.maximumDurationUnits = maxDurationUnits;
	}

	public final Integer getMinimumDuration() {
		return this.minimumDuration;
	}

	public final Unit getMinimumDurationUnits() {
		return this.minimumDurationUnits;
	}

	public final Unit getMaximumDurationUnits() {
		return this.maximumDurationUnits;
	}

	public final Integer getMaximumDuration() {
		return this.maximumDuration;
	}

	@Override
	protected String debugMessage() {
		StringBuilder buffer = new StringBuilder(super.debugMessage());
		buffer.append("\talgorithmId=" + this.algorithmId + "\n");
		buffer.append("\tminimumPatternLength=" + this.minimumPatternLength
				+ "\n");
		buffer.append("\tmaximumPatternLength=" + this.maximumPatternLength
				+ "\n");
		buffer.append(this.gapBtwValues.debugMessage() + "\n");
		buffer.append("\tminDuration=" + this.minimumDuration + " "
				+ this.minimumDurationUnits + "\n");
		buffer.append("\tmaxDuration=" + this.maximumDuration + " "
				+ this.maximumDurationUnits + "\n");
		buffer.append("\tmaxOverlapping=" + this.maxOverlapping + "\n");
		buffer.append("\tparamIds=" + this.paramIds + "\n");
		buffer.append("\tskip=" + this.skip + "\n");
		buffer.append("\tskipStart=" + this.skipStart + "\n");
		buffer.append("\tskipEnd=" + this.skipEnd + "\n");
		buffer.append("\tvalueType=" + this.valueType + "\n");
		return buffer.toString();
	}

	/**
	 * Gets the sliding window width mode. The default value is
	 * {@link SlidingWindowWidthMode.DEFAULT}.
	 * 
	 * @return a {@link SlidingWindowWidthMode} object (will never be
	 *         <code>null</code>).
	 */
	public SlidingWindowWidthMode getSlidingWindowWidthMode() {
		return slidingWindowWidthMode;
	}

	/**
	 * Sets the sliding window width mode.
	 * 
	 * @param slidingWindowWidthMode
	 *            a {@link SlidingWindowWidthMode}. If <code>null</code>,
	 *            will set the mode to the default ({@link SlidingWindowWidthMode.DEFAULT}).
	 */
	public void setSlidingWindowWidthMode(
			SlidingWindowWidthMode slidingWindowWidthMode) {
		if (slidingWindowWidthMode == null) {
			this.slidingWindowWidthMode = SlidingWindowWidthMode.DEFAULT;
		} else {
			this.slidingWindowWidthMode = slidingWindowWidthMode;
		}
	}

	public boolean isConcatenable() {
		return false;
	}

	public boolean isSolid() {
		return false;
	}

	@Override
	protected void recalculateDirectChildren() {
		String[] old = this.directChildren;
		this.directChildren = this.paramIds.toArray(new String[this.paramIds.size()]);
		this.changes.firePropertyChange(DIRECT_CHILDREN_PROPERTY, old,
				this.directChildren);
	}
}
