package org.arp.javautil.fileutils.meb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ParseArgs extends HashMap<String, Object> {

	private ArrayList<String> arglist = new ArrayList<String>();
	private HashMap<String, Boolean> validArgs = new HashMap<String, Boolean>();
	protected boolean parseError = false;

	public ParseArgs() {
	}

	protected boolean validArg(String arg, String template) {

		boolean valid = inValidArgList(arg);
		if (!valid)
			return false;

		if (arg.equals("-" + template) || arg.equals("--" + template)
				|| arg.equals("-" + template.substring(0, 1))
				|| arg.equals("--" + template.substring(0, 1)))
			return true;
		else
			return false;
	}

	protected void storeValue(String key, Object value) {
		this.put(key, value);
	}

	protected boolean inValidArgList(String key) {
		Boolean required = validArgs.get(key);
		if (required != null)
			return true;
		else
			return false;
	}

	public void setValidArg(String key, Boolean required) {
		validArgs.put(key, required);
	}

	// Override this method for specific parsing
	public boolean parse(String[] args) {

		// this.args = args;

		this.put("verbose", false);
		this.put("debug", false);

		for (int n = 0; n < args.length; n++) {

			if (validArg(args[n], "verbose")) {
				storeValue("verbose", true);
			} else if (validArg(args[n], "debug")) {
				storeValue("debug", true);
			} else {
				arglist.add(args[n]);
			}
		}

		return parseError;
	}

	public boolean checkForArgs(String[] keys) {

		boolean error = false;
		for (String key : keys) {
			String value = this.getString(key);
			if ((value == null) || value.equals("")) {
				System.err.format("Invalid value for -%s: %s\n", key, value);
				error = true;
			}
		}
		return error;
	}

	public String getString(String key) {
		return (String) this.get(key);
	}

	public Boolean getBoolean(String key) {
		return (Boolean) this.get(key);
	}

	public ArrayList<String> getArgList() {
		return arglist;
	}

}
