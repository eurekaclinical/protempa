package org.protempa.bp.commons.dsb.relationaldb;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.protempa.proposition.value.ValueType;

/**
 * For specifying properties of a relational database entity.
 * 
 * @author Andrew Post
 */
public final class PropertySpec implements Serializable {
    private static final long serialVersionUID = -1917547963157896382L;
    private final String name;
    private final Map<String, String> codeToPropIdMap;
    private final ColumnSpec codeSpec;
    private final ValueType valueType;
    private final JDBCValueFormat jdbcValueFormat;

    /**
     * Instantiates a property specification with a name, path, value type,
     * and optional mappings from values in the database to values as
     * specified in a knowledge source. With this constructor, the default
     * mechanism of reading values of this property from the database will be
     * used, which is to read the property value using 
     * {@link java.sql.ResultSet#getString(int)} and parse the string using
     * {@link org.protempa.proposition.value.ValueFactory#parse(java.lang.String) }.
     * 
     * @param name the property's name. Cannot be <code>null</code>.
     * @param codeToPropIdMap an optional {@link Map<String,String>()} map
     * from values in the database to values in a knowledge source.
     * @param codeSpec a {@link ColumnSpec} path through the database from
     * the corresponding entity's main table to the table and column where this
     * property is located.
     * @param valueType the {@link ValueType} type of values of this property.
     */
    public PropertySpec(String name,
            Map<String, String> codeToPropIdMap,
            ColumnSpec codeSpec,
            ValueType valueType) {
        this(name, codeToPropIdMap, codeSpec, valueType, null);
    }
    
    /**
     * Instantiates a property specification with a name, path, value type,
     * optional mappings from values in the database to values as
     * specified in a knowledge source, and a custom formatter for reading
     * values of this property from a result set.
     * 
     * @param name the property's name. Cannot be <code>null</code>.
     * @param codeToPropIdMap an optional {@link Map<String,String>()} map
     * from values in the database to values in a knowledge source.
     * @param codeSpec a {@link ColumnSpec} path through the database from
     * the corresponding entity's main table to the table and column where this
     * property is located.
     * @param valueType the {@link ValueType} type of values of this property.
     * @param jdbcValueFormat a {@link JDBCValueFormat} for reading values of
     * this property from a result set (may be null).
     */
    public PropertySpec(String name,
            Map<String, String> codeToPropIdMap,
            ColumnSpec codeSpec,
            ValueType valueType, 
            JDBCValueFormat jdbcValueFormat) {
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        if (codeSpec == null)
            throw new IllegalArgumentException("codeSpec cannot be null");
        if (valueType == null)
            throw new IllegalArgumentException("valueType cannot be null");
        this.name = name.intern();
        if (codeToPropIdMap != null) {
            this.codeToPropIdMap = new HashMap<String,String>(codeToPropIdMap);
        } else {
            this.codeToPropIdMap = Collections.emptyMap();
        }
        this.codeSpec = codeSpec;
        this.valueType = valueType;
        this.jdbcValueFormat = jdbcValueFormat;
    }

    /**
     * Returns the property's name. Guaranteed not <code>null</code>.
     *
     * @return a {@link String}.
     */
    public String getName() {
        return this.name;
    }

    public boolean codeToPropIdMapContainsKey(String key) {
        return this.codeToPropIdMap.containsKey(key);
    }

    public String propositionIdFor(String key) {
        return this.codeToPropIdMap.get(key);
    }

    /**
     * Gets the path through the database from this entity's main table to
     * the table and column where this property is located.
     *
     * @return a {@link ColumnSpec}.
     */
    public ColumnSpec getSpec() {
        return this.codeSpec;
    }

    /**
     * Returns the type of this property's values.
     *
     * @return a {@link ValueType}.
     */
    public ValueType getValueType() {
        return this.valueType;
    }
    
    /**
     * Returns a custom formatter that reads values of this property from a
     * {@link java.sql.ResultSet}. If not <code>null</code>, this formatter 
     * will override the default mechanism of reading property values, which is 
     * to read the property value using 
     * {@link java.sql.ResultSet#getString(int)} and parse the string using
     * {@link org.protempa.proposition.value.ValueFactory#parse(java.lang.String) }.
     * 
     * @return a {@link JDBCValueFormat}. Can be <code>null</code>.
     */
    public JDBCValueFormat getJDBCValueFormat() {
        return this.jdbcValueFormat;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
