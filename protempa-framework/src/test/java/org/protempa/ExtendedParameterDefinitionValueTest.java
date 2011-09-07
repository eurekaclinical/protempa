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
        KnowledgeBase kb = new KnowledgeBase();
        this.llad = new LowLevelAbstractionDefinition(kb, "TEST");
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
        param.setDataSourceType(DerivedDataSourceType.getInstance());
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
        param.setDataSourceType(DerivedDataSourceType.getInstance());
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
        param.setDataSourceType(DerivedDataSourceType.getInstance());

        assertTrue(nullValueDef.getMatches(param));
    }

    public void testDoesNotMatchOnValue() {
        ExtendedParameterDefinition def1 = new ExtendedParameterDefinition(llad.getId());
        def1.setAbbreviatedDisplayName("t");
        def1.setDisplayName("test");
        def1.setValue(new NumberValue(13));

        AbstractParameter param = new AbstractParameter("TEST", uid());
        param.setDataSourceType(DerivedDataSourceType.getInstance());
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
