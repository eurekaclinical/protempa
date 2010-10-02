package org.protempa.query.handler.table;

import java.util.List;
import java.util.Map;
import org.protempa.KnowledgeSource;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.UniqueIdentifier;
import org.protempa.proposition.value.Value;

public class PropertyColumnSpec implements TableColumnSpec {

    private final String propertyName;
    private final String[] columnNames;

    public PropertyColumnSpec(String propertyName) {
        if (propertyName == null) {
            throw new IllegalArgumentException("propertyName cannot be null");
        }
        this.propertyName = propertyName;
        this.columnNames = new String[]{this.propertyName};
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public String[] columnNames(String propId, KnowledgeSource knowledgeSource) {
        return this.columnNames.clone();
    }

    @Override
    public String[] columnValues(String key, Proposition proposition, Map<Proposition, List<Proposition>> derivations, Map<UniqueIdentifier, Proposition> references, KnowledgeSource knowledgeSource) {
        Value propertyValue = proposition.getProperty(this.propertyName);
        String result;
        if (propertyValue != null) {
            result = propertyValue.getFormatted();
        } else {
            result = Util.NULL_COLUMN;
        }
        return new String[]{result};
    }
}
