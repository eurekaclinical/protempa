package org.protempa;

import java.util.UUID;

import org.protempa.proposition.DerivedSourceId;
import org.protempa.proposition.DerivedUniqueId;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Segment;
import org.protempa.proposition.Sequence;
import org.protempa.proposition.UniqueId;

/**
 * @author Andrew Post
 */
final class SegmentTestParameters {

    private SegmentTestParameters() {
    }

    static Segment<PrimitiveParameter> getLength1PrimitiveParameterSegment() {
        PrimitiveParameter p1 = new PrimitiveParameter("TEST", uid());
        p1.setDataSourceType(DataSourceBackendDataSourceType.getInstance("TEST"));
        p1.setTimestamp(1L);
        Sequence<PrimitiveParameter> seq = new Sequence<PrimitiveParameter>(
                "TEST", 1);
        seq.add(p1);
        return new SimpleSegment<PrimitiveParameter>(seq);
    }

    static Segment<PrimitiveParameter> getLength2PrimitiveParameterSegment() {
        PrimitiveParameter p1 = new PrimitiveParameter("TEST", uid());
        p1.setDataSourceType(DataSourceBackendDataSourceType.getInstance("TEST"));
        p1.setTimestamp(1L);
        PrimitiveParameter p2 = new PrimitiveParameter("TEST", uid());
        p2.setDataSourceType(DataSourceBackendDataSourceType.getInstance("TEST"));
        p2.setTimestamp(2L);
        Sequence<PrimitiveParameter> seq = new Sequence<PrimitiveParameter>(
                "TEST", 2);
        seq.add(p1);
        seq.add(p2);
        return new SimpleSegment<PrimitiveParameter>(seq);
    }
    
    private static UniqueId uid() {
        return new UniqueId(
                DerivedSourceId.getInstance(),
                new DerivedUniqueId(UUID.randomUUID().toString()));
    }
}
