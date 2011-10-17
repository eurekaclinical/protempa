package org.arp.javautil.fileutils.meb;

import java.util.ArrayList;

public class ParseArgsLoad extends ParseArgs{

	ParseArgsLoad(){
		setValidArg("host", false);
		setValidArg("sid", false);
		setValidArg("user", false);
		setValidArg("pass", false);
		setValidArg("file", true);
	}
	
	@Override
	public boolean parse(String [] nargs){
		
		super.parse(nargs);
		
		ArrayList<String> arglist = getArgList();
		for( int n=0; n<arglist.size(); n++ ){
			
			String arg = arglist.get(n);
			if (validArg(arg,"file")) {
				this.storeValue("file", arglist.get(++n));
			} else {
				System.err.println("Invalid arg: " + arglist.get(n));
				parseError = true;
			}
		}
		
		return parseError;
	}
}
