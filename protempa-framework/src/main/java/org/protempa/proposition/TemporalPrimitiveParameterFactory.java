package org.protempa.proposition;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.protempa.proposition.value.Granularity;
import org.protempa.proposition.value.Value;


public final class TemporalPrimitiveParameterFactory {
	private final DateFormat dateFormat;
	private final Granularity granularity;

	public TemporalPrimitiveParameterFactory(DateFormat dateFormat,
			Granularity granularity) {
		if (dateFormat == null)
			this.dateFormat = DateFormat.getDateTimeInstance();
		else
			this.dateFormat = dateFormat;
		this.granularity = granularity;
	}

	public PrimitiveParameter getInstance(String id, String timestamp)
			throws ParseException {
		return getInstance(id, timestamp != null ? this.dateFormat
				.parse(timestamp) : null);
	}

	public PrimitiveParameter getInstance(String id, String timestamp,
			Value value) throws ParseException {
		PrimitiveParameter result = getInstance(id, timestamp);
		result.setValue(value);
		return result;
	}

	public PrimitiveParameter getInstance(String id, Date timestamp) {
		return getInstance(id, timestamp != null ? timestamp.getTime() : null);
	}

	public PrimitiveParameter getInstance(String id, Long timestamp) {
		PrimitiveParameter pp = new PrimitiveParameter(id);
		if (timestamp != null)
			pp.setTimestamp(timestamp);
		pp.setGranularity(this.granularity);
		return pp;
	}
}
