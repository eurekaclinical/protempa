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
package org.protempa.proposition;

import org.protempa.proposition.interval.IntervalFactory;

import junit.framework.TestCase;
import org.protempa.DataSourceType;

import org.protempa.proposition.value.RelativeHourGranularity;

/**
 * @author Andrew Post
 * 
 */
public class AbstractParameterTest extends TestCase {

    private static final IntervalFactory intervalFactory =
            new IntervalFactory();
    private AbstractParameter p;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        p = new AbstractParameter("TEST");
        p.setDataSourceType(DataSourceType.DERIVED);
        p.setInterval(intervalFactory.getInstance(0L,
                RelativeHourGranularity.HOUR, 12L,
                RelativeHourGranularity.HOUR));

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        p = null;
    }

    /**
     * Having the method here stops JUnit from complaining that there are no
     * tests...
     */
    public void testEmptyTest() {
    }
}
