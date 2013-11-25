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
package org.protempa.ksb;

import java.util.HashSet;
import org.arp.javautil.arrays.Arrays;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.protempa.ContextDefinition;
import org.protempa.EventDefinition;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceImpl;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.PrimitiveParameterDefinition;
import org.protempa.TemporalExtendedPropositionDefinition;
import org.protempa.TemporalPropositionDefinition;
import org.protempa.backend.ksb.KnowledgeSourceBackend;
import org.protempa.backend.ksb.SimpleKnowledgeSourceBackend;

/**
 *
 * @author Andrew Post
 */
public class KnowledgeSourceContextDefinitionTest {
    private KnowledgeSourceImpl ks;
    private PrimitiveParameterDefinition ppd;
    private EventDefinition ed;
    private ContextDefinition cd;
    private ContextDefinition subContext1;
    private ContextDefinition subContext2;
    
    @Before
    public void setUp() {
        ppd = new PrimitiveParameterDefinition("foo");
        cd = new ContextDefinition("bar");
        TemporalExtendedPropositionDefinition tepd = new TemporalExtendedPropositionDefinition("foo");
        
        ed = new EventDefinition("baz");
        TemporalExtendedPropositionDefinition tepd2 = new TemporalExtendedPropositionDefinition("baz");
        
        TemporalExtendedPropositionDefinition[] tepds = {tepd, tepd2};
        cd.setInducedBy(tepds);
        
        subContext2 = new ContextDefinition("barbar");
        
        subContext1 = new ContextDefinition("foofoo");
        cd.setSubContexts(new String[]{subContext1.getId(), subContext2.getId()});
        
        
        
        KnowledgeSourceBackend b = new SimpleKnowledgeSourceBackend(ppd, ed, cd, subContext1, subContext2);
        ks = new KnowledgeSourceImpl(b);
    }
    
    @After
    public void tearDown() {
        ppd = null;
        ed = null;
        ks = null;
        cd = null;
    }
    
    @Test
    public void testReadPropositionDefinition() throws KnowledgeSourceReadException {
        Assert.assertEquals(cd, ks.readPropositionDefinition(cd.getId()));
    }
    
    @Test
    public void testReadPropositionDefinitionCached() throws KnowledgeSourceReadException {
        ks.readPropositionDefinition(cd.getId());
        Assert.assertEquals(cd, ks.readPropositionDefinition(cd.getId()));
    }
    
    @Test
    public void testReadContextDefinition() throws KnowledgeSourceReadException {
        Assert.assertEquals(cd, ks.readContextDefinition(cd.getId()));
    }
    
    @Test
    public void testReadContextDefinitionCached() throws KnowledgeSourceReadException {
        ks.readContextDefinition(cd.getId());
        Assert.assertEquals(cd, ks.readContextDefinition(cd.getId()));
    }
    
    @Test
    public void testReadNotContextDefinition() throws KnowledgeSourceReadException {
        Assert.assertEquals(null, ks.readContextDefinition(ppd.getId()));
    }
    
    @Test
    public void testReadNotAbstractionDefinition() throws KnowledgeSourceReadException {
        Assert.assertEquals(null, ks.readAbstractionDefinition(cd.getId()));
    }
    
    @Test
    public void testReadTemporalPropositionDefinition() throws KnowledgeSourceReadException {
        Assert.assertEquals(cd, ks.readTemporalPropositionDefinition(cd.getId()));
    }
    
    @Test
    public void testReadTemporalPropositionDefinitionCached() throws KnowledgeSourceReadException {
        ks.readTemporalPropositionDefinition(cd.getId());
        Assert.assertEquals(cd, ks.readTemporalPropositionDefinition(cd.getId()));
    }
    
    @Test
    public void testInducesFoo() throws KnowledgeSourceReadException {
        Assert.assertEquals(Arrays.asList(new ContextDefinition[] {cd}), ks.readInduces(ppd.getId()));
    }
    
    @Test
    public void testInducesFooCached() throws KnowledgeSourceReadException {
        ks.readInduces(ppd.getId());
        Assert.assertEquals(Arrays.asList(new ContextDefinition[] {cd}), ks.readInduces(ppd.getId()));
    }
    
    @Test
    public void testInducesBaz() throws KnowledgeSourceReadException {
        Assert.assertEquals(Arrays.asList(new ContextDefinition[] {cd}), ks.readInduces(ed.getId()));
    }
    
    @Test
    public void testInducesBazCached() throws KnowledgeSourceReadException {
        ks.readInduces(ed.getId());
        Assert.assertEquals(Arrays.asList(new ContextDefinition[] {cd}), ks.readInduces(ed.getId()));
    }
    
    @Test
    public void testInducesBogus() throws KnowledgeSourceReadException {
        Assert.assertEquals(Arrays.asList(new ContextDefinition[] {}), ks.readInduces("bogus"));
    }
    
    @Test
    public void testInducesBogusCached() throws KnowledgeSourceReadException {
        ks.readInduces("bogus");
        Assert.assertEquals(Arrays.asList(new ContextDefinition[] {}), ks.readInduces("bogus"));
    }
    
    @Test
    public void testInducedByContextId() throws KnowledgeSourceReadException {
        Assert.assertEquals(Arrays.asSet(new TemporalPropositionDefinition[]{ppd, ed}), new HashSet<>(ks.readInducedBy(cd.getId())));
    }
    
    @Test
    public void testInducedByContextIdCached() throws KnowledgeSourceReadException {
        ks.readInducedBy(cd.getId());
        Assert.assertEquals(Arrays.asSet(new TemporalPropositionDefinition[]{ppd, ed}), new HashSet<>(ks.readInducedBy(cd.getId())));
    }
    
    @Test
    public void testInducedByContextDefinition() throws KnowledgeSourceReadException {
        Assert.assertEquals(Arrays.asSet(new TemporalPropositionDefinition[]{ppd, ed}), new HashSet<>(ks.readInducedBy(cd)));
    }
    
    @Test
    public void testInducedByContextDefinitionCached() throws KnowledgeSourceReadException {
        ks.readInducedBy(cd);
        Assert.assertEquals(Arrays.asSet(new TemporalPropositionDefinition[]{ppd, ed}), new HashSet<>(ks.readInducedBy(cd)));
    }
    
    @Test
    public void testSubContextOfFooFooContextDefinition() throws KnowledgeSourceReadException {
        Assert.assertEquals(Arrays.asSet(new ContextDefinition[]{cd}), new HashSet<>(ks.readSubContextOfs(subContext1)));
    }
    
    @Test
    public void testSubContextOfFooFooContextDefinitionCached() throws KnowledgeSourceReadException {
        ks.readSubContextOfs(subContext1);
        Assert.assertEquals(Arrays.asSet(new ContextDefinition[]{cd}), new HashSet<>(ks.readSubContextOfs(subContext1)));
    }
    
    @Test
    public void testSubContextOfFooFooContextId() throws KnowledgeSourceReadException {
        Assert.assertEquals(Arrays.asSet(new ContextDefinition[]{cd}), new HashSet<>(ks.readSubContextOfs(subContext1.getId())));
    }
    
    @Test
    public void testSubContextOfFooFooContextIdCached() throws KnowledgeSourceReadException {
        ks.readSubContextOfs(subContext1.getId());
        Assert.assertEquals(Arrays.asSet(new ContextDefinition[]{cd}), new HashSet<>(ks.readSubContextOfs(subContext1.getId())));
    }
    
    @Test
    public void testSubContextsContextDefinition() throws KnowledgeSourceReadException {
        Assert.assertEquals(Arrays.asSet(new ContextDefinition[]{subContext1, subContext2}), new HashSet<>(ks.readSubContexts(cd)));
    }
    
    @Test
    public void testSubContextsContextDefinitionCached() throws KnowledgeSourceReadException {
        ks.readSubContexts(cd);
        Assert.assertEquals(Arrays.asSet(new ContextDefinition[]{subContext1, subContext2}), new HashSet<>(ks.readSubContexts(cd)));
    }
    
    @Test
    public void testSubContextsContextId() throws KnowledgeSourceReadException {
        Assert.assertEquals(Arrays.asSet(new ContextDefinition[]{subContext1, subContext2}), new HashSet<>(ks.readSubContexts(cd.getId())));
    }
    
    @Test
    public void testSubContextsContextIdCached() throws KnowledgeSourceReadException {
        ks.readSubContexts(cd.getId());
        Assert.assertEquals(Arrays.asSet(new ContextDefinition[]{subContext1, subContext2}), new HashSet<>(ks.readSubContexts(cd.getId())));
    }
    
    @Test
    public void testSubContextOfBarContextId() throws KnowledgeSourceReadException {
        Assert.assertEquals(Arrays.asSet(new ContextDefinition[]{}), new HashSet<>(ks.readSubContextOfs(cd.getId())));
    }
    
    @Test
    public void testSubContextOfBarContextIdCached() throws KnowledgeSourceReadException {
        ks.readSubContextOfs(cd.getId());
        Assert.assertEquals(Arrays.asSet(new ContextDefinition[]{}), new HashSet<>(ks.readSubContextOfs(cd.getId())));
    }
    
    @Test
    public void testSubContextOfBarContextDefinition() throws KnowledgeSourceReadException {
        Assert.assertEquals(Arrays.asSet(new ContextDefinition[]{}), new HashSet<>(ks.readSubContextOfs(cd)));
    }
    
    @Test
    public void testSubContextOfBarContextDefinitionCached() throws KnowledgeSourceReadException {
        ks.readSubContextOfs(cd);
        Assert.assertEquals(Arrays.asSet(new ContextDefinition[]{}), new HashSet<>(ks.readSubContextOfs(cd)));
    }
}
