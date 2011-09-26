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
