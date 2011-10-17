package org.arp.javautil.fileutils.meb;

import java.util.ArrayList;

public class ParseArgsDump extends ParseArgs {

	ParseArgsDump(){
		setValidArg("host", false);
		setValidArg("sid", false);
		setValidArg("user", false);
		setValidArg("pass", false);
		setValidArg("remote", false);
		setValidArg("local", false);
	}
	
	@Override
	public boolean parse(String[] args) {

		super.parse(args);

		ArrayList<String> arglist = getArgList();
		for (int n = 0; n < arglist.size(); n++) {

			String arg = arglist.get(n);

			if (validArg(arg, "user"))
				storeValue("user", arglist.get(++n));
			else if (validArg(arg, "pass"))
				storeValue("pass", arglist.get(++n));
			else if (validArg(arg, "host"))
				storeValue("host", arglist.get(++n));
			else if (validArg(arg, "sid"))
				storeValue("sid", arglist.get(++n));
			else {
				System.err.println("Invalid arg: " + arg);
				parseError = true;
			}
		}

		return parseError;
	}


}
