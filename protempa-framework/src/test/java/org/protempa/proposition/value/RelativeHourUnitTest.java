package org.protempa.proposition.value;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;

import junit.framework.TestCase;

/**
 * Test cases for RelativeHourUnit.
 * 
 * @author Andrew Post
 */
public class RelativeHourUnitTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetDurationInHours() {
		assertEquals(2, Math.round(RelativeHourUnit.HOUR
				.length(2 * 60 * 1000 * 60)));
	}

	public void testSerializable() throws IOException, ClassNotFoundException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bytes);
		out.writeObject(RelativeHourUnit.HOUR);
		out.close();

		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(
				bytes.toByteArray()));
		Unit unit = (Unit) in.readObject();
		assertEquals(RelativeHourUnit.HOUR, unit);
		in.close();
	}

	public void testReallyLongDuration() {
		assertEquals(596, RelativeHourUnit.HOUR.length(Integer.MAX_VALUE));
	}

	public void testDistanceBetween20Hours() throws ParseException {
		assertEquals(20, RelativeHourGranularity.HOUR.distance(0L,
				20L * 60 * 60 * 1000, RelativeHourGranularity.HOUR,
				RelativeHourUnit.HOUR));
	}
}
