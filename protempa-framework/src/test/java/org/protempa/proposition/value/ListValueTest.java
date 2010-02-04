package org.protempa.proposition.value;

import java.util.Arrays;
import java.util.List;

import org.protempa.proposition.value.ListValue;
import org.protempa.proposition.value.NumberValue;
import org.protempa.proposition.value.ValueFactory;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author Andrew Post
 * 
 */
public class ListValueTest extends TestCase {
	public void testParse() {
		List<?> l = Arrays.asList(new Long[] { 1L, 2L, 3L, 4L });
		ListValue v = (ListValue) ValueFactory.LIST.getInstance("[1,2,3,4]");
		Assert.assertEquals(l.size(), v.size());
		for (int i = 0, n = l.size(); i < n; i++) {
			Assert.assertEquals(l.get(i), ((NumberValue) v.get(i)).longValue());
		}
	}
}
