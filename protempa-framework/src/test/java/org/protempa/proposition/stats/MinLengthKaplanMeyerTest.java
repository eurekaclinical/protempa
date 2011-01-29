package org.protempa.proposition.stats;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.protempa.DataSourceType;
import org.protempa.DatabaseDataSourceType;
import org.protempa.DerivedDataSourceType;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalAbstractParameterFactory;
import org.protempa.proposition.TemporalEventFactory;
import org.protempa.proposition.TemporalPrimitiveParameterFactory;
import org.protempa.proposition.value.AbsoluteTimeGranularity;
import org.protempa.proposition.value.AbsoluteTimeUnit;
import org.protempa.proposition.value.NumberValue;

public class MinLengthKaplanMeyerTest extends TestCase {
	private List<Proposition> propositions;
	private MinLengthKaplanMeyer mls;
	private DataSourceType dbDataSourceType;
	private DataSourceType derivedDataSourceType;

	@Override
	protected void setUp() throws Exception {
		this.propositions = new ArrayList<Proposition>();
		this.dbDataSourceType = 
                        DatabaseDataSourceType.getInstance("MockTestDatabase");
		this.derivedDataSourceType = DerivedDataSourceType.getInstance();
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

		TemporalPrimitiveParameterFactory tppf = new TemporalPrimitiveParameterFactory(
				df, AbsoluteTimeGranularity.DAY);

		PrimitiveParameter pp = tppf.getInstance("1", "1/1/07",
				new NumberValue(2),dbDataSourceType);
		this.propositions.add(pp);

		TemporalEventFactory tef = new TemporalEventFactory(df,
				AbsoluteTimeGranularity.DAY);

		Event event = tef.getInstance("2", "1/1/07", "2/1/07", derivedDataSourceType);
		Event event2 = tef.getInstance("2", "8/15/96", "9/12/96", derivedDataSourceType);
		Event event3 = tef.getInstance("2", "2/1/05", "3/2/05", derivedDataSourceType);
		Event event4 = tef.getInstance("2", "2/1/05", "1/20/06", derivedDataSourceType);
		this.propositions.add(event);
		this.propositions.add(event2);
		this.propositions.add(event3);
		this.propositions.add(event4);

		TemporalAbstractParameterFactory tapf = new TemporalAbstractParameterFactory(
				df, AbsoluteTimeGranularity.DAY);

		AbstractParameter ap = tapf.getInstance("3", "1/1/72", "1/2/72");
		AbstractParameter ap2 = tapf.getInstance("3", "4/2/81", "4/1/82");
		AbstractParameter ap3 = tapf.getInstance("3", "4/2/81", "3/30/82");
		
		this.propositions.add(ap);
		this.propositions.add(ap2);
		this.propositions.add(ap3);

		this.mls = new MinLengthKaplanMeyer(AbsoluteTimeUnit.DAY);
		this.mls.visit(this.propositions);
	}

	@Override
	protected void tearDown() throws Exception {
		this.propositions = null;
		this.mls = null;
	}

	public void testPrimitiveParameter1NoValue() {
		assertEquals(null, this.mls.get("1"));
	}

	public void testPrimitiveParameter1WithValue() {
		Map<Long, Integer> expected = new HashMap<Long, Integer>();
		expected.put(0L, 1);
		assertEquals(expected, this.mls.get("1", new NumberValue(2)));
	}

	public void testEvent2() {
		Map<Long, Integer> expected = new HashMap<Long, Integer>();
		expected.put(28L, 1);
		expected.put(31L, 1);
		expected.put(29L, 1);
		expected.put(353L, 1);
		assertEquals(expected, this.mls.get("2"));
	}

	public void testAbstractParameter3() {
		Map<Long, Integer> expected = new HashMap<Long, Integer>();
		expected.put(364L, 1);
		expected.put(1L, 1);
		expected.put(362L, 1);
		assertEquals(expected, this.mls.get("3"));
	}

}
