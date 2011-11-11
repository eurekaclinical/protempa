package org.protempa.backend.dsb.filter;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.proposition.interval.Interval;
import org.protempa.proposition.interval.IntervalFactory;
import org.protempa.proposition.value.Granularity;

/**
 * A filter for a position (e.g., date/time) range.
 * 
 * @author Andrew Post
 */
public class PositionFilter extends AbstractFilter {

	/**
	 * Which side of the proposition's interval to which to apply the position
	 * bounds. For timestamped propositions, these are ignored.
	 */
	public static enum Side {
		START("start"), FINISH("finish");

		private String xmlName;

		private Side(String xmlName) {
			this.xmlName = xmlName;
		}

		public String getXmlName() {
			return xmlName;
		}
	}

	private static final IntervalFactory intervalFactory = new IntervalFactory();

	private Long start;
	private Granularity startGran;
	private Long finish;
	Granularity finishGran;

	private Interval ival;

	private Side startSide;

	private Side finishSide;

	/**
	 * Creates a filter with a position range.
	 * 
	 * @param propIds
	 *            a {@link String[]} of proposition ids on which to filter.
	 * @param start
	 *            the start position in Protempa's {@link Long} representation.
	 * @param startGran
	 *            the {@link Granularity} with which to interpret the start
	 *            position.
	 * @param finish
	 *            the finish position in Protempa's {@link Long} representation.
	 * @param finishGran
	 *            the {@link Granularity} with which to interpret the finish
	 *            position.
	 */
	public PositionFilter(String[] propIds, Long start, Granularity startGran, Long finish, Granularity finishGran) {
		this(propIds, start, startGran, finish, finishGran, null, null);
	}

	/**
	 * Initializes a filter with a position range.
	 * 
	 * @param propIds
	 *            a {@link String[]} of proposition ids on which to filter.
	 * @param start
	 *            the start position in Protempa's {@link Long} representation.
	 * @param startGran
	 *            the {@link Granularity} with which to interpret the start
	 *            position.
	 * @param finish
	 *            the finish position in Protempa's {@link Long} representation.
	 * @param finishGran
	 *            the {@link Granularity} with which to interpret the finish
	 *            position.
	 * @param startSide
	 *            the {@link Side} of the proposition to which to apply the
	 *            start bound. The default is {@link Side.START} (if
	 *            <code>null</code> is specified). Ignored for timestamped
	 *            propositions.
	 * @param finishSide
	 *            the {@link Side} of the proposition to which to apply the
	 *            finish bound. The default is {@link Side.FINISH} (if
	 *            <code>null</code> is specified). Ignored for timestamped
	 *            propositions.
	 */
	public PositionFilter(String[] propIds, Long start, Granularity startGran, Long finish, Granularity finishGran, Side startSide, Side finishSide) {
		super(propIds);
		this.start = start;
		this.startGran = startGran;
		this.finish = finish;
		this.finishGran = finishGran;
		this.startSide = startSide;
		this.finishSide = finishSide;
		init();
	}

	/**
	 * Complete this object's initialization. This method should be called
	 * externally only by Castor.
	 */
	private void init() {
		if (ival == null) {
			this.ival = intervalFactory.getInstance(start, startGran, finish, finishGran);
			if (startSide == null)
				startSide = Side.START;
			if (finishSide == null)
				finishSide = Side.FINISH;
		}
	}

	/**
	 * Returns the {@link Granularity} with which to interpret the start
	 * position.
	 * 
	 * @return the startGranularity a {@link Granularity}.
	 */
	public Granularity getStartGranularity() {
		return startGran;
	}

	/**
	 * Returns the {@link Granularity} with which to interpret the finish
	 * position.
	 * 
	 * @return the finishGranularity a {@link Granularity}.
	 */
	public Granularity getFinishGranularity() {
		return finishGran;
	}

	/**
	 * Returns the maximum finish position in Protempa's {@link Long}
	 * representation.
	 * 
	 * @return a {@link Long}.
	 */
	public Long getMaximumFinish() {
		return this.ival.getMaximumFinish();
	}

	/**
	 * Returns the maximum start position in Protempa's {@link Long}
	 * representation.
	 * 
	 * @return a {@link Long}.
	 */
	public Long getMaximumStart() {
		return this.ival.getMaximumStart();
	}

	/**
	 * Returns the minimum finish position in Protempa's {@link Long}
	 * representation.
	 * 
	 * @return a {@link Long}.
	 */
	public Long getMinimumFinish() {
		return this.ival.getMinimumFinish();
	}

	/**
	 * Returns the minimum start position in Protempa's {@link Long}
	 * representation.
	 * 
	 * @return a {@link Long}.
	 */
	public Long getMinimumStart() {
		return this.ival.getMinimumStart();
	}

	/**
	 * Returns the start position supplied in the constructor.
	 * 
	 * @return a {@link Long}.
	 */
	public Long getStart() {
		return start;
	}

	/**
	 * Returns the finish position supplied in the constructor.
	 * 
	 * @return a {@link Long}.
	 */
	public Long getFinish() {
		return finish;
	}

	/**
	 * Returns the side of the proposition to which to apply the lower bound.
	 * 
	 * @return a {@link Side}.
	 */
	public Side getStartSide() {
		return this.startSide;
	}

	/**
	 * Returns the side of the proposition to which to apply the upper bound.
	 * 
	 * @return a {@link Side}.
	 */
	public Side getFinishSide() {
		return this.finishSide;
	}

	@Override
	public void accept(FilterVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
