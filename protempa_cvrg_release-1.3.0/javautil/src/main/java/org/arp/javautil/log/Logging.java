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

import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Convenience functions for good logging.
 * 
 * @author Andrew Post
 */
public class Logging {
    private static final ThreadLocal<NumberFormat> numberFormat = new ThreadLocal<NumberFormat>() {
        @Override
        protected NumberFormat initialValue() {
            return NumberFormat.getIntegerInstance();
        }
    };
    
    /**
     * Prints a count of something to a log with an appropriate singular or
     * plural message.
     * 
     * @param logger the {@link Logger} to which to print.
     * @param level the logging {@link Level}.
     * @param count the count to print.
     * @param singularMessage the message string to print if the count is 
     * <code>1</code>. In {@link java.text.MessageFormat} style. The 
     * <code>{0}</code> parameter will be replaced by the count.
     * @param pluralMessage the message string to print if the count is 
     * <code>0</code> or greater than <code>1</code>.  in
     * {@link java.text.MessageFormat} style. the <code>{0}</code> parameter
     * will be replaced by the count.
     */
    public static void logCount(Logger logger, Level level, int count, 
            String singularMessage, String pluralMessage) {
        logCount(logger, level, count, singularMessage, pluralMessage, 
                null, null);
    }
    
    /**
     * Prints a count of something to a log with an appropriate singular or
     * plural message.
     * 
     * @param logger the {@link Logger} to which to print.
     * @param level the logging {@link Level}.
     * @param count the count to print.
     * @param singularMessage the message string to print if the count is 
     * <code>1</code>. In {@link java.text.MessageFormat} style. The 
     * <code>{0}</code> parameter will be replaced by the count.
     * @param pluralMessage the message string to print if the count is 
     * <code>0</code> or greater than <code>1</code>.  in
     * {@link java.text.MessageFormat} style. the <code>{0}</code> parameter
     * will be replaced by the count.
     * @param singularParams additional message parameters for the singular 
     * message, starting with <code>{1}</code>.
     * @param pluralParams additional message parameters for the plural 
     * message, starting with <code>{1}</code>.
     */
    public static void logCount(Logger logger, Level level, int count, 
            String singularMessage, String pluralMessage, 
            Object[] singularParams, Object[] pluralParams) {
        String countStr = numberFormat.get().format(count);
        if (count > 1 || count == 0) {
            logger.log(level, pluralMessage, 
                    processLogCountParams(countStr, pluralParams));
        } else {
            logger.log(level, singularMessage, 
                    processLogCountParams(countStr, singularParams));
        }
    }

    private static Object[] processLogCountParams(String countStr, 
            Object[] params) {
        Object[] sp;
        if (params != null && params.length > 0) {
            sp = new Object[1 + params.length];
            sp[0] = countStr;
            System.arraycopy(params, 0, sp, 1, params.length);
        } else {
            sp = new Object[] {countStr};
        }
        return sp;
    }
}
