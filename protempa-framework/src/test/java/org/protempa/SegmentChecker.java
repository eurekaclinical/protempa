package org.protempa;

import org.protempa.proposition.Segment;

import junit.framework.TestCase;

public abstract class SegmentChecker extends TestCase {
	protected Long minStart;

	protected Long maxStart;

	protected Long minFinish;

	protected Long maxFinish;

	protected Long minDuration;

	protected Long maxDuration;

	protected Segment seg;

	public void testLength1MinStart() {
		assertEquals(minStart, seg.getInterval().getMinimumStart());
	}

	public void testLength1MaxStart() {
		assertEquals(maxStart, seg.getInterval().getMaximumStart());
	}

	public void testLength1MinFinish() {
		assertEquals(minFinish, seg.getInterval().getMinimumFinish());
	}

	public void testLength1MaxFinish() {
		assertEquals(maxFinish, seg.getInterval().getMaximumFinish());
	}

	public void testLength1MinDuration() {
		assertEquals(minDuration, seg.getInterval().getMinimumLength());
	}

	public void testLength1MaxDuration() {
		assertEquals(maxDuration, seg.getInterval().getMaximumLength());
	}
}
