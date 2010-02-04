package org.protempa.proposition;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.protempa.proposition.value.Granularity;


public final class TemporalEventFactory {
	private DateFormat dateFormat;
	private Granularity granularity;

	public TemporalEventFactory(DateFormat dateFormat, Granularity granularity) {
		if (dateFormat == null)
			this.dateFormat = DateFormat.getDateTimeInstance();
		else
			this.dateFormat = dateFormat;
		this.granularity = granularity;
	}

	public Event getInstance(String id, String timestamp) throws ParseException {
		return getInstance(id, timestamp != null ? this.dateFormat
				.parse(timestamp) : null);
	}

	public Event getInstance(String id, Date timestamp) {
		return getInstance(id, timestamp != null ? timestamp.getTime() : null);
	}

	public Event getInstance(String id, Long timestamp) {
		Event pp = new Event(id);
		pp.setInterval(new PointInterval(timestamp, this.granularity,
				timestamp, this.granularity));
		return pp;
	}

	public Event getInstance(String id, String start, String finish)
			throws ParseException {

		return getInstance(id, start != null ? this.dateFormat.parse(start)
				: null, finish != null ? this.dateFormat.parse(finish) : null);
	}

	public Event getInstance(String id, Date start, Date finish) {
		return getInstance(id, start != null ? start.getTime() : null,
				finish != null ? finish.getTime() : null);
	}

	public Event getInstance(String id, Long start, Long finish) {
		Event e = new Event(id);
		e.setInterval(new SimpleAbstractParameterInterval(start,
				this.granularity, finish, this.granularity));
		return e;
	}
}
