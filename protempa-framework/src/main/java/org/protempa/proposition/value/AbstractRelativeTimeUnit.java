package org.protempa.proposition.value;

/**
 * Base class for relative time units.
 * 
 * @author Andrew Post
 */
public abstract class AbstractRelativeTimeUnit extends AbstractTimeUnit {

    AbstractRelativeTimeUnit(String name, String pluralName,
            String abbreviation, String shortFormat, String mediumFormat,
            String longFormat, long length, int calUnits) {
        super(name, pluralName, abbreviation, shortFormat,
                mediumFormat, longFormat, length, calUnits);
    }

    @Override
    public long addToPosition(long position, int duration) {
        return position + duration * getLength();
    }

    public int length(int lengthInBaseUnits) {
        if (lengthInBaseUnits < 0) {
            throw new IllegalArgumentException(
                    "lengthInBaseUnits must be >= 0L");
        }
        return (int) (lengthInBaseUnits / getLength());
    }
}
