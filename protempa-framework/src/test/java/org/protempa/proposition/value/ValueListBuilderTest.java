package org.protempa.proposition.value;

/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2015 Emory University
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

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Andrew Post
 */
public class ValueListBuilderTest {
    @Test
    public void testBuild() {
        NominalValue val1 = NominalValue.getInstance("foo");
        NominalValue val2 = NominalValue.getInstance("bar");
        ValueList<NominalValue> expected = ValueList.getInstance(val1, val2);
        
        ValueListBuilder<NominalValue> actualBuilder = new ValueListBuilder<>();
        actualBuilder.setElements(new NominalValueBuilder[]{val1.asBuilder(), val2.asBuilder()});
        ValueList<NominalValue> actual = actualBuilder.build();
        assertEquals(expected, actual);
    }
}
