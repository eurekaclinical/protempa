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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.arp.javautil.arrays.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.protempa.backend.ksb.SimpleKnowledgeSourceBackend;

/**
 *
 * @author Andrew Post
 */
public class KnowledgeSourceImplWrapperTest {

    @Test
    public void testInWrapperEmptyWrappedPropositionDefinition()
            throws KnowledgeSourceReadException {
        ConstantDefinition fooDef = new ConstantDefinition("foo");
        KnowledgeSource wrapped = new KnowledgeSourceImpl(
                new SimpleKnowledgeSourceBackend());
        KnowledgeSourceImplWrapper wrapper =
                new KnowledgeSourceImplWrapper(wrapped, fooDef);
        Assert.assertEquals(fooDef, wrapper.readPropositionDefinition("foo"));
    }

    @Test
    public void testInWrapperOtherWrappedPropositionDefinition()
            throws KnowledgeSourceReadException {
        ConstantDefinition fooDef = new ConstantDefinition("foo");
        ConstantDefinition barDef = new ConstantDefinition("bar");
        KnowledgeSource wrapped = new KnowledgeSourceImpl(
                new SimpleKnowledgeSourceBackend(barDef));
        KnowledgeSource wrapper = new KnowledgeSourceImplWrapper(wrapped,
                fooDef);
        Assert.assertEquals(barDef, wrapper.readPropositionDefinition("bar"));
    }

    @Test
    public void testInWrapperOverridesWrappedPropositionDefinition()
            throws KnowledgeSourceReadException {
        ConstantDefinition fooDef1 = new ConstantDefinition("foo");
        ConstantDefinition fooDef2 = new ConstantDefinition("foo");
        KnowledgeSource wrapped = new KnowledgeSourceImpl(
                new SimpleKnowledgeSourceBackend(fooDef2));
        KnowledgeSource wrapper = new KnowledgeSourceImplWrapper(wrapped,
                fooDef1);
        Assert.assertEquals(fooDef1, wrapper.readPropositionDefinition("foo"));
    }

    @Test
    public void testInWrapperIsAPropositionDefinition()
            throws KnowledgeSourceReadException {
        ConstantDefinition fooDef = new ConstantDefinition("foo");
        ConstantDefinition barDef = new ConstantDefinition("bar");
        ConstantDefinition bazDef = new ConstantDefinition("baz");
        barDef.setInverseIsA("foo");
        bazDef.setInverseIsA("foo");
        KnowledgeSource wrapped = new KnowledgeSourceImpl(
                new SimpleKnowledgeSourceBackend());
        KnowledgeSource wrapper = new KnowledgeSourceImplWrapper(wrapped,
                fooDef, barDef, bazDef);
        Set<ConstantDefinition> expected = 
                Arrays.asSet(new ConstantDefinition[]{barDef, bazDef});
        Set<PropositionDefinition> actual =
                new HashSet<>(wrapper.readIsA("foo"));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testInWrapperIsAPropositionDefinition2()
            throws KnowledgeSourceReadException {
        ConstantDefinition fooDef = new ConstantDefinition("foo");
        ConstantDefinition barDef = new ConstantDefinition("bar");
        ConstantDefinition bazDef = new ConstantDefinition("baz");
        barDef.setInverseIsA("foo");
        bazDef.setInverseIsA("foo");
        KnowledgeSource wrapped = new KnowledgeSourceImpl(
                new SimpleKnowledgeSourceBackend(fooDef));
        KnowledgeSource wrapper = new KnowledgeSourceImplWrapper(wrapped,
                barDef, bazDef);
        Set<ConstantDefinition> expected = 
                Arrays.asSet(new ConstantDefinition[]{barDef, bazDef});
        Set<PropositionDefinition> actual =
                new HashSet<>(wrapper.readIsA("foo"));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testInWrapperIsAPropositionDefinition3()
            throws KnowledgeSourceReadException {
        ConstantDefinition fooDef = new ConstantDefinition("foo");
        ConstantDefinition barDef = new ConstantDefinition("bar");
        ConstantDefinition bazDef = new ConstantDefinition("baz");
        bazDef.setInverseIsA("foo");
        barDef.setInverseIsA("foo");
        KnowledgeSource wrapped = new KnowledgeSourceImpl(
                new SimpleKnowledgeSourceBackend(barDef, fooDef));
        KnowledgeSource wrapper = new KnowledgeSourceImplWrapper(wrapped,
                bazDef);
        Set<ConstantDefinition> expected = 
                Arrays.asSet(new ConstantDefinition[]{barDef, bazDef});
        Set<PropositionDefinition> actual =
                new HashSet<>(wrapper.readIsA("foo"));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testInWrapperIsAPropositionDefinition3WithOverride()
            throws KnowledgeSourceReadException {
        ConstantDefinition fooDef1 = new ConstantDefinition("foo");
        ConstantDefinition fooDef2 = new ConstantDefinition("foo");
        ConstantDefinition barDef = new ConstantDefinition("bar");
        ConstantDefinition bazDef = new ConstantDefinition("baz");
        bazDef.setInverseIsA("foo");
        barDef.setInverseIsA("foo");
        KnowledgeSource wrapped = new KnowledgeSourceImpl(
                new SimpleKnowledgeSourceBackend(barDef, fooDef1));
        KnowledgeSource wrapper = new KnowledgeSourceImplWrapper(wrapped,
                fooDef2, bazDef);
        Set<ConstantDefinition> expected = 
                Arrays.asSet(new ConstantDefinition[]{barDef, bazDef});
        Set<PropositionDefinition> actual =
                new HashSet<>(wrapper.readIsA("foo"));
        Assert.assertEquals(expected, actual);
    }
    
    @Test
    public void testInWrapperIsAPropositionDefinition3WithOverride2()
            throws KnowledgeSourceReadException {
        ConstantDefinition fooDef1 = new ConstantDefinition("foo");
        ConstantDefinition fooDef2 = new ConstantDefinition("foo");
        ConstantDefinition barDef = new ConstantDefinition("bar");
        ConstantDefinition bazDef = new ConstantDefinition("baz");
        fooDef1.setInverseIsA("bar");
        fooDef2.setInverseIsA("baz");
        KnowledgeSource wrapped = new KnowledgeSourceImpl(
                new SimpleKnowledgeSourceBackend(bazDef, fooDef1));
        KnowledgeSource wrapper = new KnowledgeSourceImplWrapper(wrapped,
                fooDef2, barDef);
        PropositionDefinition[] expected = {};
        List<PropositionDefinition> actual = wrapper.readIsA("bar");
        Assert.assertEquals(Arrays.asList(expected), actual);
    }
    
    @Test
    public void testInWrapperIsAPropositionDefinition3WithOverride3()
            throws KnowledgeSourceReadException {
        ConstantDefinition fooDef1 = new ConstantDefinition("foo");
        ConstantDefinition fooDef2 = new ConstantDefinition("foo");
        ConstantDefinition barDef = new ConstantDefinition("bar");
        ConstantDefinition bazDef = new ConstantDefinition("baz");
        fooDef1.setInverseIsA("bar");
        fooDef2.setInverseIsA("baz");
        KnowledgeSource wrapped = new KnowledgeSourceImpl(
                new SimpleKnowledgeSourceBackend(bazDef, fooDef1));
        KnowledgeSource wrapper = new KnowledgeSourceImplWrapper(wrapped,
                fooDef2, barDef);
        PropositionDefinition[] expected = {fooDef2};
        List<PropositionDefinition> actual = wrapper.readIsA("baz");
        Assert.assertEquals(Arrays.asList(expected), actual);
    }

    @Test
    public void testInWrapperEmptyWrappedAbstractionDefinition()
            throws KnowledgeSourceReadException {
        AbstractionDefinition fooDef =
                new LowLevelAbstractionDefinition("foo");
        KnowledgeSource wrapped = new KnowledgeSourceImpl(
                new SimpleKnowledgeSourceBackend());
        KnowledgeSourceImplWrapper wrapper =
                new KnowledgeSourceImplWrapper(wrapped, fooDef);
        Assert.assertEquals(fooDef, wrapper.readAbstractionDefinition("foo"));
    }

    @Test
    public void testInWrapperOtherWrappedAbstractionDefinition()
            throws KnowledgeSourceReadException {
        AbstractionDefinition fooDef =
                new LowLevelAbstractionDefinition("foo");
        AbstractionDefinition barDef =
                new LowLevelAbstractionDefinition("bar");
        KnowledgeSource wrapped = new KnowledgeSourceImpl(
                new SimpleKnowledgeSourceBackend(barDef));
        KnowledgeSource wrapper = new KnowledgeSourceImplWrapper(wrapped,
                fooDef);
        Assert.assertEquals(barDef, wrapper.readAbstractionDefinition("bar"));
    }

    @Test
    public void testInWrapperOverridesWrappedAbstractionDefinition()
            throws KnowledgeSourceReadException {
        AbstractionDefinition fooDef1 =
                new LowLevelAbstractionDefinition("foo");
        AbstractionDefinition fooDef2 =
                new LowLevelAbstractionDefinition("foo");
        KnowledgeSource wrapped = new KnowledgeSourceImpl(
                new SimpleKnowledgeSourceBackend(fooDef2));
        KnowledgeSource wrapper = new KnowledgeSourceImplWrapper(wrapped,
                fooDef1);
        Assert.assertEquals(fooDef1, wrapper.readAbstractionDefinition("foo"));
    }

    @Test
    public void testInWrapperIsAAbstractionDefinition()
            throws KnowledgeSourceReadException {
        LowLevelAbstractionDefinition fooDef =
                new LowLevelAbstractionDefinition("foo");
        LowLevelAbstractionDefinition barDef =
                new LowLevelAbstractionDefinition("bar");
        LowLevelAbstractionDefinition bazDef =
                new LowLevelAbstractionDefinition("baz");
        barDef.setInverseIsA("foo");
        bazDef.setInverseIsA("foo");
        KnowledgeSource wrapped = new KnowledgeSourceImpl(
                new SimpleKnowledgeSourceBackend());
        KnowledgeSource wrapper = new KnowledgeSourceImplWrapper(wrapped,
                fooDef, barDef, bazDef);
        Set<LowLevelAbstractionDefinition> expected =
                Arrays.asSet(
                new LowLevelAbstractionDefinition[]{barDef, bazDef});
        Set<PropositionDefinition> actual =
                new HashSet<>(wrapper.readIsA("foo"));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testInWrapperAbstractedIntoAbstractionDefinition()
            throws KnowledgeSourceReadException {
        PrimitiveParameterDefinition fooDef =
                new PrimitiveParameterDefinition("foo");
        LowLevelAbstractionDefinition barDef =
                new LowLevelAbstractionDefinition("bar");
        barDef.addPrimitiveParameterId("foo");
        KnowledgeSource wrapped = new KnowledgeSourceImpl(
                new SimpleKnowledgeSourceBackend());
        KnowledgeSource wrapper = new KnowledgeSourceImplWrapper(wrapped,
                fooDef, barDef);
        PropositionDefinition[] expected = {barDef};
        Assert.assertEquals(Arrays.asList(expected),
                wrapper.readAbstractedInto("foo"));
    }

    @Test
    public void testInWrapperAbstractedIntoAbstractionDefinition2()
            throws KnowledgeSourceReadException {
        PrimitiveParameterDefinition fooDef =
                new PrimitiveParameterDefinition("foo");
        LowLevelAbstractionDefinition barDef =
                new LowLevelAbstractionDefinition("bar");
        barDef.addPrimitiveParameterId("foo");
        KnowledgeSource wrapped = new KnowledgeSourceImpl(
                new SimpleKnowledgeSourceBackend(fooDef));
        KnowledgeSource wrapper = new KnowledgeSourceImplWrapper(wrapped,
                barDef);
        PropositionDefinition[] expected = {barDef};
        Assert.assertEquals(Arrays.asList(expected),
                wrapper.readAbstractedInto("foo"));
    }
}
