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


public class LogFormatterTab extends LogFormatterSingleLine {
    
    public LogFormatterTab(){
    	super();
    	this.setDelimiter("\t");
    }
}
