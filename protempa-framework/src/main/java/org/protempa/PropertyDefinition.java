package org.protempa;

import java.io.Serializable;
import org.protempa.proposition.value.ValueType;

/**
 *
 * @author Andrew Post
 */
public interface PropertyDefinition extends Serializable {
    String getName();
    ValueType getValueType();
}
