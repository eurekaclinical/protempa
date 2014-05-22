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
package org.protempa;

import java.util.Collections;
import junit.framework.TestCase;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.interval.IntervalFactory;
import org.protempa.proposition.value.NumberValue;

/**
 * @author Andrew Post
 */
public class ExtendedParameterDefinitionValueTest extends TestCase {

    private static final IntervalFactory intervalFactory
            = new IntervalFactory();
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

    public void testMatches() throws KnowledgeSourceReadException {
        ExtendedParameterDefinition completeDef = new ExtendedParameterDefinition(
                llad.getId());
        completeDef.setAbbreviatedDisplayName("t");
        completeDef.setDisplayName("test");
        completeDef.setValue(new NumberValue(13));

        AbstractParameter param = new AbstractParameter(llad.getId());
        param.setDataSourceType(DataSourceType.DERIVED);
        param.setValue(new NumberValue(13));
        param.setInterval(intervalFactory.getInstance());

        assertTrue(completeDef.getMatches(param, Collections.singleton(llad.getId())));
    }

    public void testDoesMatchValue() throws KnowledgeSourceReadException {
        ExtendedParameterDefinition completeDef = new ExtendedParameterDefinition(
                llad.getId());
        completeDef.setAbbreviatedDisplayName("t");
        completeDef.setDisplayName("test");
        completeDef.setValue(new NumberValue(13));

        AbstractParameter param = new AbstractParameter(llad.getId());
        param.setDataSourceType(DataSourceType.DERIVED);
        param.setValue(new NumberValue(13));
        param.setInterval(intervalFactory.getInstance());

        assertTrue(completeDef.getMatches(param, Collections.singleton(llad.getId())));
    }

    public void testDoesMatchNullValue() throws KnowledgeSourceReadException {
        ExtendedParameterDefinition nullValueDef = new ExtendedParameterDefinition(
                llad.getId());
        nullValueDef.setAbbreviatedDisplayName("t");
        nullValueDef.setDisplayName("test");

        AbstractParameter param = new AbstractParameter(llad.getId());
        param.setValue(new NumberValue(13));
        param.setInterval(intervalFactory.getInstance());
        param.setDataSourceType(DataSourceType.DERIVED);

        assertTrue(nullValueDef.getMatches(param, Collections.singleton(llad.getId())));
    }

    public void testDoesNotMatchOnValue() throws KnowledgeSourceReadException {
        ExtendedParameterDefinition def1 = new ExtendedParameterDefinition(llad.getId());
        def1.setAbbreviatedDisplayName("t");
        def1.setDisplayName("test");
        def1.setValue(new NumberValue(13));

        AbstractParameter param = new AbstractParameter(llad.getId());
        param.setDataSourceType(DataSourceType.DERIVED);
        param.setValue(new NumberValue(12));
        param.setInterval(intervalFactory.getInstance());

        assertFalse(def1.getMatches(param, Collections.singleton(llad.getId())));
    }
}
