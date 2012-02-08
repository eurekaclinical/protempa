/*
 * #%L
 * JavaUtil
 * %%
 * Copyright (C) 2012 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.arp.javautil.log;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


public class LogFormatterSingleLine extends Formatter {

    private long starttime = 0L;
    private long lasttime = 0L;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
    protected String delimiter = " ";
    private String recordFormat = "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %2$s %3$s %4$s %5$s %6$s %7$s\n";
    
    public LogFormatterSingleLine(){
    	super();
        starttime = lasttime = System.currentTimeMillis();
    }
    
    protected void setDelimiter(String delimiter){
    	recordFormat = recordFormat.replaceAll(" ", delimiter);	
    }
    
	@Override
	public String format(LogRecord record) {
		
		String classname = record.getSourceClassName();
		
		return String.format(
				recordFormat, record
						.getMillis(), record.getLevel(), memoryInfo(), timeInfo(record), 
				record.getMessage(), classname.substring(classname.lastIndexOf(".")+1), record.getSourceMethodName());
	}
	
    public String timeInfo(LogRecord record) {

            long now = System.currentTimeMillis();
            
            String elapsedTime = ElapsedTime.getElapsedTime(now, starttime);
            String lappedTime = ElapsedTime.getElapsedTime(now, lasttime);

            lasttime = System.currentTimeMillis();
      
            return String.format("%s %s", elapsedTime, lappedTime );
    }

    private String memoryInfo() {
        return decimalFormat.format(Runtime.getRuntime().totalMemory());
    }
    
}
