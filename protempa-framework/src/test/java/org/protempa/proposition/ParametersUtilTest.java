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

import java.util.Arrays;
import java.util.List;

import org.protempa.DataSourceBackendSourceSystem;
import org.protempa.ProtempaTestCase;

/**
 * JUnit tests for the <code>ParametersUtil</code> class.
 *
 * @author Andrew Post
 */
public class ParametersUtilTest extends ProtempaTestCase {

    List<PrimitiveParameter> params;

    @Override
    protected void setUp() throws Exception {
        PrimitiveParameter[] paramsArr = new PrimitiveParameter[4];
        paramsArr[3] = new PrimitiveParameter("TEST", getUid());
        paramsArr[3].setPosition(6L);
        paramsArr[3].setSourceSystem(DataSourceBackendSourceSystem.getInstance("TEST"));
        paramsArr[2] = new PrimitiveParameter("TEST", getUid());
        paramsArr[2].setPosition(4L);
        paramsArr[2].setSourceSystem(DataSourceBackendSourceSystem.getInstance("TEST"));
        paramsArr[1] = new PrimitiveParameter("TEST", getUid());
        paramsArr[1].setPosition(2L);
        paramsArr[1].setSourceSystem(DataSourceBackendSourceSystem.getInstance("TEST"));
        paramsArr[0] = new PrimitiveParameter("TEST", getUid());
        paramsArr[0].setPosition(0L);
        paramsArr[0].setSourceSystem(DataSourceBackendSourceSystem.getInstance("TEST"));

        params = Arrays.asList(paramsArr);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetView1() {
        assertEquals(params, PropositionUtil.getView(params, null, null));
    }

    public void testGetView2() {
        assertEquals(params.subList(1, 4), PropositionUtil.getView(params,
                new Long(1), null));
    }

    public void testGetView3() {
        assertEquals(params.subList(0, 2), PropositionUtil.getView(params, null,
                new Long(2)));
    }

    public void testGetView4() {
        assertEquals(params.subList(1, 3), PropositionUtil.getView(params,
                new Long(1), new Long(5)));
    }
}
