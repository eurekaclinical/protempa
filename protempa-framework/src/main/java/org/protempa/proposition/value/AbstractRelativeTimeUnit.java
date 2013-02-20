/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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

/**
 * Base class for relative time units.
 * 
 * @author Andrew Post
 */
public abstract class AbstractRelativeTimeUnit extends AbstractTimeUnit {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4125851501045482152L;

	AbstractRelativeTimeUnit(String name, String pluralName,
            String abbreviation, String shortFormat, String mediumFormat,
            String longFormat, long length, int calUnits) {
        super(name, pluralName, abbreviation, shortFormat,
                mediumFormat, longFormat, length, calUnits);
    }

    @Override
    public long addToPosition(long position, int duration) {
        return position + duration * getLength();
    }

    public int length(int lengthInBaseUnits) {
        if (lengthInBaseUnits < 0) {
            throw new IllegalArgumentException(
                    "lengthInBaseUnits must be >= 0L");
        }
        return (int) (lengthInBaseUnits / getLength());
    }
}
