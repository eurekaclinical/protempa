package org.protempa;

import org.protempa.proposition.value.Value;

/**
 *
 * @author Andrew Post
 */
public interface ValueDataSourceConstraint extends DataSourceConstraint {
    Value getValue();
}
