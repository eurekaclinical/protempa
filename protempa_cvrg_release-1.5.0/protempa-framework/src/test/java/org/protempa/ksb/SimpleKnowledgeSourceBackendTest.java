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
package org.protempa.ksb;

import java.util.Set;
import org.arp.javautil.arrays.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.protempa.AbstractionDefinition;
import org.protempa.ConstantDefinition;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.LowLevelAbstractionDefinition;
import org.protempa.PrimitiveParameterDefinition;
import org.protempa.PropositionDefinition;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.backend.ksb.SimpleKnowledgeSourceBackend;

/**
 *
 * @author Andrew Post
 */
public class SimpleKnowledgeSourceBackendTest {

    @Test
    public void testReadPropositionDefinition()
            throws KnowledgeSourceReadException {
        ConstantDefinition expected = new ConstantDefinition("foo");
        KnowledgeSourceBackend b = new SimpleKnowledgeSourceBackend(expected);
        PropositionDefinition actual = b.readPropositionDefinition("foo");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReadPropositionDefinition2()
            throws KnowledgeSourceReadException {
        PropositionDefinition fooDef = new ConstantDefinition("foo");
        KnowledgeSourceBackend b = new SimpleKnowledgeSourceBackend(fooDef);
        PropositionDefinition actual = b.readAbstractionDefinition("foo");
        Assert.assertEquals(null, actual);
    }

    @Test
    public void testReadAbstractionDefinition() 
            throws KnowledgeSourceReadException {
        PropositionDefinition expected =
                new LowLevelAbstractionDefinition("foo");
        KnowledgeSourceBackend b = new SimpleKnowledgeSourceBackend(expected);
        AbstractionDefinition actual = b.readAbstractionDefinition("foo");
        Assert.assertEquals(expected, actual);
    }
    
    @Test
    public void testReadAbstractionDefinition2() 
            throws KnowledgeSourceReadException {
        PropositionDefinition fooDef =
                new ConstantDefinition("foo");
        KnowledgeSourceBackend b = new SimpleKnowledgeSourceBackend(fooDef);
        AbstractionDefinition actual = b.readAbstractionDefinition("foo");
        Assert.assertEquals(null, actual);
    }
    
    @Test
    public void readIsA() throws KnowledgeSourceReadException {
        ConstantDefinition fooDef = new ConstantDefinition("foo");
        ConstantDefinition barDef = 
                new ConstantDefinition("bar");
        ConstantDefinition bazDef = 
                new ConstantDefinition("baz");
        barDef.setInverseIsA(fooDef.getId());
        bazDef.setInverseIsA(fooDef.getId());
        KnowledgeSourceBackend b = new SimpleKnowledgeSourceBackend(fooDef, 
                barDef, bazDef);
        String[] actual = b.readIsA("foo");
        Set<String> expected = Arrays.asSet(new String[]{"bar", "baz"});
        Assert.assertEquals(expected, Arrays.asSet(actual));
    }
    
    @Test
    public void readAbstractedInto() throws KnowledgeSourceReadException {
        PropositionDefinition fooDef = new PrimitiveParameterDefinition("foo");
        LowLevelAbstractionDefinition barDef = 
                new LowLevelAbstractionDefinition("bar");
        LowLevelAbstractionDefinition bazDef = 
                new LowLevelAbstractionDefinition("baz");
        barDef.addPrimitiveParameterId(fooDef.getId());
        bazDef.addPrimitiveParameterId(fooDef.getId());
        KnowledgeSourceBackend b = new SimpleKnowledgeSourceBackend(fooDef, 
                barDef, bazDef);
        String[] actual = b.readAbstractedInto("foo");
        Set<String> expected = Arrays.asSet(new String[]{"bar", "baz"});
        Assert.assertEquals(expected, Arrays.asSet(actual));
    }
}
