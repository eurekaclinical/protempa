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

import java.text.Format;

/**
 * Base class for defining time units.
 * 
 * @author Andrew Post
 */
public abstract class AbstractTimeUnit implements Unit {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1592143864041255117L;
	private transient String name;
    private transient String pluralName;
    private transient String abbreviation;
    private transient long length;
    private transient int calUnits;
    private transient Format shortFormat;
    private transient Format mediumFormat;
    private transient Format longFormat;

    AbstractTimeUnit(String name, String pluralName, String abbreviation,
            String shortFormat, String mediumFormat,
            String longFormat, long length, int calUnits) {
        this.name = name;
        this.pluralName = pluralName;
        this.abbreviation = abbreviation;
        this.length = length;
        this.calUnits = calUnits;
        this.shortFormat = new RelativeTimeUnitFormat(this, length,
                shortFormat);
        this.mediumFormat = new RelativeTimeUnitFormat(this, length,
                mediumFormat);
        this.longFormat = new RelativeTimeUnitFormat(this, length, longFormat);
    }

    @Override
    public String getPluralName() {
        return pluralName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAbbreviatedName() {
        return abbreviation;
    }

    protected long getLength() {
        return length;
    }

    /**
     * The equivalent units in {@link java.util.Calendar}.
     *
     * @return a unit <code>int</code>
     */
    public int getCalendarUnits() {
        return this.calUnits;
    }

    @Override
    public Format getLongFormat() {
        return this.longFormat;
    }

    @Override
    public Format getMediumFormat() {
        return this.mediumFormat;
    }

    @Override
    public Format getShortFormat() {
        return this.shortFormat;
    }



    /**
     * Returns the unit's name.
     *
     * @see java.lang.Object#toString()
     * @see #getName()
     */
    @Override
    public String toString() {
        return name;
    }
}
