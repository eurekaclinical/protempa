package org.protempa;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Locale;

/**
 * Formats a proposition definition according to the following rules:
 * <ol>
 * <li>The abbreviated display name, if not <code>null</code>.
 * <li>The id.
 * </ol>
 * 
 * @author Andrew Post
 * 
 */
public class DisplayNameFormat extends Format {

	private static final long serialVersionUID = 7005729678612629890L;

	public static enum Style {
		FULL, SHORT
	}

	public static DisplayNameFormat getInstance() {
		return new DisplayNameFormat();
	}

	public static DisplayNameFormat getInstance(Locale locale) {
		return getInstance();
	}

	public static DisplayNameFormat getInstance(Style style) {
		return new DisplayNameFormat(style);
	}

	public static DisplayNameFormat getInstance(Style style, Locale locale) {
		return getInstance(style);
	}

	private final Style style;

	protected DisplayNameFormat() {
		this(Style.FULL);
	}

	protected DisplayNameFormat(Style style) {
		if (style == null)
			this.style = Style.FULL;
		else
			this.style = style;
	}

	@Override
	public StringBuffer format(Object obj, StringBuffer toAppendTo,
			FieldPosition pos) {
		if (!(obj instanceof PropositionDefinition))
			throw new IllegalArgumentException(
					"obj must be a PropositionDefinition");
		PropositionDefinition pd = (PropositionDefinition) obj;

		String displayName;
		if (this.style == Style.FULL)
			displayName = pd.getDisplayName();
		else
			displayName = pd.getAbbreviatedDisplayName();

		if (displayName != null)
			toAppendTo.append(displayName);
		else
			toAppendTo.append(pd.getId());
		return toAppendTo;
	}

	@Override
	public Object parseObject(String source, ParsePosition pos) {
		String msg = "Parsing an abstraction definition from a short display name is unsupported.";
		throw new UnsupportedOperationException(msg);
	}

}
