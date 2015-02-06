package org.protempa;

/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2014 Emory University
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.arp.javautil.arrays.Arrays;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.protempa.backend.ksb.SimpleKnowledgeSourceBackend;

/**
 *
 * @author Andrew Post
 */
public class KnowledgeSourceImplTest {
    private KnowledgeSourceImpl knowledgeSource;
    
    @Before
    public void setUp() {
        Map<String, String[]> constantPropIds = new HashMap<>();
        constantPropIds.put("foo", new String[]{});
        constantPropIds.put("bar", new String[]{"foo", "baz"});
        constantPropIds.put("baz", new String[]{"oof"});
        constantPropIds.put("oof", new String[]{});
        constantPropIds.put("rab", new String[]{"zab"});
        constantPropIds.put("zab", new String[]{});
        List<PropositionDefinition> propDefs = new ArrayList<>();
        for (Map.Entry<String, String[]> me : constantPropIds.entrySet()) {
            ConstantDefinition constantDefinition = 
                    new ConstantDefinition(me.getKey());
            constantDefinition.setInverseIsA(me.getValue());
            propDefs.add(constantDefinition);
        }
        this.knowledgeSource = new KnowledgeSourceImpl(
                new SimpleKnowledgeSourceBackend(
                        propDefs.toArray(
                                new PropositionDefinition[propDefs.size()])));
    }
    
    @After
    public void tearDown() throws SourceCloseException {
        this.knowledgeSource.close();
        this.knowledgeSource = null;
    }
    
    @Test
    public void testCollectSubtreeMultilevel() throws KnowledgeSourceReadException {
        Set<String> expected = 
                Arrays.asSet(new String[] {"bar", "foo", "baz", "oof"});
        Assert.assertEquals("collectSubtree failed", 
                expected, 
                knowledgeSource.collectPropIdDescendantsUsingInverseIsA("bar"));
    }
    
    @Test
    public void testCollectSubtreeNoChildren() throws KnowledgeSourceReadException {
        Set<String> expected = Collections.singleton("zab");
        Assert.assertEquals("collectSubtree failed", 
                expected, 
                knowledgeSource.collectPropIdDescendantsUsingInverseIsA("zab"));
    }
    
    @Test
    public void testCollectSubtreeMultipleArguments() throws KnowledgeSourceReadException {
        Set<String> expected = 
                Arrays.asSet(new String[] {"bar", "foo", "baz", "oof", "rab", "zab"});
        Assert.assertEquals("collectSubtree failed", 
                expected, 
                knowledgeSource.collectPropIdDescendantsUsingInverseIsA("bar", "rab"));
    }
}
