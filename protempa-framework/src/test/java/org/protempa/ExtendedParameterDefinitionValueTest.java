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
package org.protempa;

import java.util.UUID;

import junit.framework.TestCase;

import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.DerivedSourceId;
import org.protempa.proposition.DerivedUniqueId;
import org.protempa.proposition.interval.IntervalFactory;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.value.NumberValue;

/**
 * @author Andrew Post
 */
public class ExtendedParameterDefinitionValueTest extends TestCase {

    private static final IntervalFactory intervalFactory =
            new IntervalFactory();
    private LowLevelAbstractionDefinition llad;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.llad = new LowLevelAbstractionDefinition("TEST");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        llad = null;
    }

    public void testMatches() {
        ExtendedParameterDefinition completeDef = new ExtendedParameterDefinition(
                llad.getId());
        completeDef.setAbbreviatedDisplayName("t");
        completeDef.setDisplayName("test");
        completeDef.setValue(new NumberValue(13));

        AbstractParameter param = new AbstractParameter("TEST", uid());
        param.setDataSourceType(DataSourceType.DERIVED);
        param.setValue(new NumberValue(13));
        param.setInterval(intervalFactory.getInstance());

        assertTrue(completeDef.getMatches(param));
    }

    public void testDoesMatchValue() {
        ExtendedParameterDefinition completeDef = new ExtendedParameterDefinition(
                llad.getId());
        completeDef.setAbbreviatedDisplayName("t");
        completeDef.setDisplayName("test");
        completeDef.setValue(new NumberValue(13));

        AbstractParameter param = new AbstractParameter("TEST", uid());
        param.setDataSourceType(DataSourceType.DERIVED);
        param.setValue(new NumberValue(13));
        param.setInterval(intervalFactory.getInstance());

        assertTrue(completeDef.getMatches(param));
    }

    public void testDoesMatchNullValue() {
        ExtendedParameterDefinition nullValueDef = new ExtendedParameterDefinition(
                llad.getId());
        nullValueDef.setAbbreviatedDisplayName("t");
        nullValueDef.setDisplayName("test");

        AbstractParameter param = new AbstractParameter("TEST", uid());
        param.setValue(new NumberValue(13));
        param.setInterval(intervalFactory.getInstance());
        param.setDataSourceType(DataSourceType.DERIVED);

        assertTrue(nullValueDef.getMatches(param));
    }

    public void testDoesNotMatchOnValue() {
        ExtendedParameterDefinition def1 = new ExtendedParameterDefinition(llad.getId());
        def1.setAbbreviatedDisplayName("t");
        def1.setDisplayName("test");
        def1.setValue(new NumberValue(13));

        AbstractParameter param = new AbstractParameter("TEST", uid());
        param.setDataSourceType(DataSourceType.DERIVED);
        param.setValue(new NumberValue(12));
        param.setInterval(intervalFactory.getInstance());

        assertFalse(def1.getMatches(param));
    }
    
    private static UniqueId uid() {
        return new UniqueId(
                DerivedSourceId.getInstance(),
                new DerivedUniqueId(UUID.randomUUID().toString()));
    }
}
