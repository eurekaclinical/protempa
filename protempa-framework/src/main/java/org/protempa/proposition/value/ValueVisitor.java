package org.protempa.proposition.value;

/**
 * Interface for classes that do processing on propositions.
 * 
 * @author Andrew Post
 * 
 */
public interface ValueVisitor {

    /**
     * Processes a nominal value.
     *
     * @param nominalValue
     *            a {@link NominalValue}. Cannot be <code>null</code>.
     */
    void visit(NominalValue nominalValue);

    /**
     * Processes an ordinal value.
     *
     * @param ordinalValue
     *            an {@link OrdinalValue}. Cannot be <code>null</code>.
     */
    void visit(OrdinalValue ordinalValue);

    /**
     * Processes a boolean value.
     *
     * @param booleanValue
     *            a {@link BooleanValue}. Cannot be <code>null</code>.
     */
    void visit(BooleanValue booleanValue);

    /**
     * Processes a list of values.
     *
     * @param listValue
     *            a {@link ListValue}. Cannot be <code>null</code>.
     */
    void visit(ListValue<? extends Value> listValue);

    /**
     * Processes a number value.
     *
     * @param numberValue
     *            a {@link NumberValue}. Cannot be <code>null</code>.
     */
    void visit(NumberValue numberValue);

    /**
     * Processes an inequality number value.
     *
     * @param inequalityNumberValue
     *            an {@link InequalityNumberValue}.
     *            Cannot be <code>null</code>.
     */
    void visit(InequalityNumberValue inequalityNumberValue);
    
    /**
     * Processes a date value.
     *
     * @param dateValue
     *            an {@link DateValue}.
     *            Cannot be <code>null</code>.
     */
    void visit(DateValue dateValue);
}
