package org.protempa.proposition;

import java.util.Arrays;
import java.util.List;

import org.protempa.DataSourceBackendDataSourceType;
import org.protempa.proposition.PrimitiveParameter;


import junit.framework.TestCase;

/**
 * JUnit tests for the <code>ParametersUtil</code> class.
 * 
 * @author Andrew Post
 */
public class ParametersUtilTest extends TestCase {
	List<PrimitiveParameter> params;

	@Override
	protected void setUp() throws Exception {
		PrimitiveParameter[] paramsArr = new PrimitiveParameter[4];
		paramsArr[3] = new PrimitiveParameter("TEST");
		paramsArr[3].setTimestamp(6L);
		paramsArr[3].setDataSourceType(DataSourceBackendDataSourceType.getInstance("TEST"));
		paramsArr[2] = new PrimitiveParameter("TEST");
		paramsArr[2].setTimestamp(4L);
		paramsArr[2].setDataSourceType(DataSourceBackendDataSourceType.getInstance("TEST"));
		paramsArr[1] = new PrimitiveParameter("TEST");
		paramsArr[1].setTimestamp(2L);
		paramsArr[1].setDataSourceType(DataSourceBackendDataSourceType.getInstance("TEST"));
		paramsArr[0] = new PrimitiveParameter("TEST");
		paramsArr[0].setTimestamp(0L);
		paramsArr[0].setDataSourceType(DataSourceBackendDataSourceType.getInstance("TEST"));

		params = Arrays.asList(paramsArr);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetView1() {
		assertEquals(params, PropositionUtil.getView(params, null, null));
	}

	public void testGetView2() {
		assertEquals(params.subList(1, 4), PropositionUtil.getView(params,
				new Long(1), null));
	}

	public void testGetView3() {
		assertEquals(params.subList(0, 2), PropositionUtil.getView(params, null,
				new Long(2)));
	}

	public void testGetView4() {
		assertEquals(params.subList(1, 3), PropositionUtil.getView(params,
				new Long(1), new Long(5)));
	}
}
