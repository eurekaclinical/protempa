package org.protempa.bp.commons;

import org.protempa.bp.commons.dsb.CommonsSchemaAdaptor;
import org.protempa.bp.commons.dsb.CommonsTerminologyAdaptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.protempa.AbstractDataSourceBackend;

/**
 *
 * @author Andrew Post
 */
public class AbstractCommonsDataSourceBackend
        extends AbstractDataSourceBackend implements CommonsDataSourceBackend  {

    public AbstractCommonsDataSourceBackend(CommonsSchemaAdaptor schemaAdaptor,
            CommonsTerminologyAdaptor terminologyAdaptor) {
        super(schemaAdaptor, terminologyAdaptor);
        if (terminologyAdaptor != null) {
            if (haveOverlappingProperties(schemaAdaptor, terminologyAdaptor)) {
                throw new IllegalArgumentException(
                        "the schema adaptor and the terminology adaptor may not have overlapping properties");
            }
        }
    }

    public AbstractCommonsDataSourceBackend(
            CommonsSchemaAdaptor schemaAdaptor) {
        super(schemaAdaptor);
    }

    private static boolean haveOverlappingProperties(
            CommonsSchemaAdaptor schemaAdaptor,
            CommonsTerminologyAdaptor terminologyAdaptor) {
        Method[] saFields = schemaAdaptor.getClass().getMethods();
        List<String> saAnnotatedFields = new ArrayList<String>();
        for (Method field : saFields) {
            if (field.isAnnotationPresent(SchemaAdaptorProperty.class)) {
                saAnnotatedFields.add(field.getName());
            }
        }
        Method[] taFields = terminologyAdaptor.getClass().getMethods();
        List<String> taAnnotatedFields = new ArrayList<String>();
        for (Method field : taFields) {
            if (field.isAnnotationPresent(TerminologyAdaptorProperty.class)) {
                taAnnotatedFields.add(field.getName());
            }
        }
        if (saAnnotatedFields.removeAll(taAnnotatedFields)) {
            return true;
        }
        return false;
    }
}
