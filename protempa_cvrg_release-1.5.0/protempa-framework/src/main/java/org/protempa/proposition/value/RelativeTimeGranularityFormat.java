/*
 * #%L
 * Protempa Framework
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
package org.protempa.proposition.value;

import java.text.FieldPosition;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * <b>NOTE:</b> This class is not thread-safe, since it uses a NumberFormat 
 * instance and a MessageFormat instance as private fields.
 * 
 * @author Andrew Post
 */
class RelativeTimeGranularityFormat extends Format {
    private static final long serialVersionUID = -5276847494971693256L;
    
    private final Granularity granularity;
    private final MessageFormat messageFormat;
    private final long length;
    private final NumberFormat numberFormat;

    RelativeTimeGranularityFormat(Granularity granularity, long length,
            String pattern) {
        this(granularity, length, pattern, true);
    }

    RelativeTimeGranularityFormat(Granularity granularity, long length,
            String pattern, boolean groupingUsed) {
        this.granularity = granularity;
        this.messageFormat = new MessageFormat(pattern);
        this.length = length;
        this.numberFormat = NumberFormat.getInstance();
        this.numberFormat.setGroupingUsed(groupingUsed);
    }

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo,
            FieldPosition pos) {
        assert obj instanceof Long : obj + " is not a Long";
        String val = this.numberFormat.format(obj);
        return messageFormat.format(new Object[]{val}, toAppendTo, pos);
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        Object result = messageFormat.parseObject(source, pos);

        if (result != null && result instanceof Object[]) {
            Object[] resultArr = (Object[]) result;
            if (resultArr.length > 0) {
                Number num = numberFormat.parse((String) resultArr[0],
                        new ParsePosition(0));
                if (granularity != null) {
                    return num.longValue() * length;
                } else {
                    return num;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
