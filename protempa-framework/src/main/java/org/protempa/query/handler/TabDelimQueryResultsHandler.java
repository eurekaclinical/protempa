package org.protempa.query.handler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.arp.javautil.string.StringUtil;

import org.protempa.FinderException;
import org.protempa.ProtempaException;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.AbstractPropositionCheckedVisitor;
import org.protempa.proposition.Constant;
import org.protempa.proposition.Event;
import org.protempa.proposition.Parameter;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.UniqueIdentifier;

/**
 * An implementation of QueryResultsHandler providing functionality for
 * writing the results to an output stream in a tab-delimited format.
 * 
 * @author Michel Mansour
 *
 */
public class TabDelimQueryResultsHandler extends WriterQueryResultsHandler {

    private static final char COLUMN_DELIMITER = '\t';
    private final List<Comparator<Proposition>> comparator;
    private final TabDelimHandlerPropositionVisitor visitor;
    private final boolean includeDerived;

    /**
     * Instantiates this handler to write to a {@link Writer}. No sorting
     * will be performed.
     * 
     * @param out a {@link Writer}.
     */
    public TabDelimQueryResultsHandler(Writer out) {
        this(out, null);
    }

    public TabDelimQueryResultsHandler(Writer out, boolean includeDerived) {
        this(out, null, includeDerived);
    }

    /**
     * Instantiates this handler to write to a {@link Writer} with optional
     * sorting of propositions.
     *
     * @param out a {@link Writer}.
     * @param comparator a {@link List<? extends Comparator<Proposition>>}. Every key's
     * propositions will be sorted by the provided comparators in the order
     * they are given. A value of <code>null</code> or an empty array means no
     * sorting will be performed.
     */
    public TabDelimQueryResultsHandler(Writer out,
            List<? extends Comparator<Proposition>> comparator) {
        this(out, comparator, false);
    }

    public TabDelimQueryResultsHandler(Writer out,
            List<? extends Comparator<Proposition>> comparator,
            boolean includeDerived) {
        super(out);
        if (comparator == null) {
            this.comparator = Collections.emptyList();
        } else {
            this.comparator =
                    new ArrayList<Comparator<Proposition>>(comparator);
        }
        this.visitor = new TabDelimHandlerPropositionVisitor(this);
        this.includeDerived = includeDerived;
    }

    /**
     * Instantiates this handler to write to an {@link OutputStream}.
     * No sorting will be performed.
     *
     * @param out an {@link OutputStream}.
     */
    public TabDelimQueryResultsHandler(OutputStream out) {
        this(out, null);
    }

    public TabDelimQueryResultsHandler(OutputStream out, boolean includeDerived) {
        this(out, null, includeDerived);
    }

    /**
     * Instantiates this handler to write to an {@link OutputStream) with
     * optional sorting of propositions.
     *
     * @param out a {@link OutputStream}.
     * @param comparator a {@link List<? extends Comparator<Proposition>>}. Every key's
     * propositions will be sorted by the provided comparators in the order
     * they are given. A value of <code>null</code> or an empty array means no
     * sorting will be performed.
     */
    public TabDelimQueryResultsHandler(OutputStream out,
            List<? extends Comparator<Proposition>> comparator) {
        this(out, comparator, false);
    }

    public TabDelimQueryResultsHandler(OutputStream out,
            List<? extends Comparator<Proposition>> comparator,
            boolean includeDerived) {
        super(out);
        if (comparator == null) {
            this.comparator = Collections.emptyList();
        } else {
            this.comparator =
                    new ArrayList<Comparator<Proposition>>(comparator);
        }
        this.visitor = new TabDelimHandlerPropositionVisitor(this);
        this.includeDerived = includeDerived;
    }

    /**
     * Instantiates this handler to write to a file. No sorting will be
     * performed.
     *
     * @param fileName a file name {@link String}.
     * @throws IOException if an error occurred opening/creating the file.
     */
    public TabDelimQueryResultsHandler(String fileName) throws IOException {
        this(fileName, null);
    }

    public TabDelimQueryResultsHandler(String fileName, boolean includeDerived) throws IOException {
        this(fileName, null, includeDerived);
    }

    /**
     * Instantiates this handler to write to a file with optional sorting
     * of propositions. If the file already exists, its contents will be
     * overwritten.
     *
     * @param fileName a file name {@link String}.
     * @param comparator a {@link List<? extends Comparator<Proposition>>}. Every key's
     * propositions will be sorted by the provided comparators in the order
     * they are given. A value of <code>null</code> or an empty array means no
     * sorting will be performed.
     * @throws IOException if an error occurred opening/creating the file.
     */
    public TabDelimQueryResultsHandler(String fileName,
            List<? extends Comparator<Proposition>> comparator)
            throws IOException {
        this(fileName, comparator, false);
    }

    public TabDelimQueryResultsHandler(String fileName,
            List<? extends Comparator<Proposition>> comparator,
            boolean includeDerived) throws IOException {
        super(fileName);
        if (comparator == null) {
            this.comparator = Collections.emptyList();
        } else {
            this.comparator =
                    new ArrayList<Comparator<Proposition>>(comparator);
        }
        this.visitor = new TabDelimHandlerPropositionVisitor(this);
        this.includeDerived = includeDerived;
    }

    /**
     * Instantiates this handler to write to a file. No sorting will be
     * performed. If the file already exists, its contents will be
     * overwritten.
     *
     * @param file a {@link File}.
     * @throws IOException if an error occurred opening/creating the file.
     */
    public TabDelimQueryResultsHandler(File file) throws IOException {
        this(file, null);
    }

    public TabDelimQueryResultsHandler(File file, boolean includeDerived) throws IOException {
        this(file, null, includeDerived);
    }

    /**
     * Instantiates this handler to write to a file with optional sorting.
     * If the file already exists, its contents will be overwritten.
     *
     * @param file a {@link File}.
     * @param comparator a {@link List<? extends Comparator<Proposition>>}. Every key's
     * propositions will be sorted by the provided comparators in the order
     * they are given. A value of <code>null</code> or an empty array means no
     * sorting will be performed.
     * @throws IOException if an error occurred opening/creating the file.
     */
    public TabDelimQueryResultsHandler(File file,
            List<? extends Comparator<Proposition>> comparator)
            throws IOException {
        this(file, comparator, false);
    }

    public TabDelimQueryResultsHandler(File file,
            List<? extends Comparator<Proposition>> comparator,
            boolean includeDerived) throws IOException {
        super(file);
        if (comparator == null) {
            this.comparator = Collections.emptyList();
        } else {
            this.comparator =
                    new ArrayList<Comparator<Proposition>>(comparator);
        }
        this.visitor = new TabDelimHandlerPropositionVisitor(this);
        this.includeDerived = includeDerived;
    }

    /**
     * Writes a keys worth of data in tab delimited format optionally sorted.
     *
     * @param key a key id {@link String}.
     * @param propositions a {@link List<Proposition>}.
     * @throws FinderException if an error occurred writing to the specified
     * file, output stream or writer.
     */
    @Override
    public void handleQueryResult(String key, List<Proposition> propositions,
            Map<Proposition, List<Proposition>> derivations,
            Map<UniqueIdentifier, Proposition> references)
            throws FinderException {
        Set<Proposition> propositionsAsSet = new HashSet<Proposition>();
        addDerived(propositions, derivations, propositionsAsSet);
        List<Proposition> propositionsCopy =
                new ArrayList<Proposition>(propositionsAsSet);
        for (Comparator<Proposition> c : this.comparator) {
            Collections.sort(propositionsCopy, c);
        }
        this.visitor.setKeyId(key);
        try {
            this.visitor.visit(propositionsCopy);
        } catch (TabDelimHandlerProtempaException pe) {
            throw new FinderException(pe);
        } catch (ProtempaException pe) {
            throw new AssertionError(pe);
        }
    }

    private void addDerived(List<Proposition> propositions,
            Map<Proposition, List<Proposition>> derivations,
            Set<Proposition> propositionsAsSet) {
        for (Proposition prop : propositions) {
            boolean added = propositionsAsSet.add(prop);
            if (added && this.includeDerived) {
                List<Proposition> derivedProps = derivations.get(prop);
                if (derivedProps != null) {
                    addDerived(derivedProps, derivations, propositionsAsSet);
                }
            }
        }
    }

    private final static class TabDelimHandlerProtempaException
            extends ProtempaException {

        private static final long serialVersionUID = 2008992530872178708L;
        private IOException ioe;

        TabDelimHandlerProtempaException(IOException cause) {
            super(cause);
            assert cause != null : "cause cannot be null";
            this.ioe = cause;
        }

        TabDelimHandlerProtempaException(String message, IOException cause) {
            super(message, cause);
            assert cause != null : "cause cannot be null";
            this.ioe = cause;
        }

        IOException getIOException() {
            return this.ioe;
        }
    }

    private final static class TabDelimHandlerPropositionVisitor
            extends AbstractPropositionCheckedVisitor {

        private String keyId;
        private final TabDelimQueryResultsHandler writer;

        TabDelimHandlerPropositionVisitor(
                TabDelimQueryResultsHandler writer) {
            this.writer = writer;
        }

        void setKeyId(String keyId) {
            this.keyId = keyId;
        }

        String getKeyId() {
            return this.keyId;
        }

        @Override
        public void visit(AbstractParameter abstractParameter)
                throws TabDelimHandlerProtempaException {
            try {
                doWriteKeyId();
                doWritePropId(abstractParameter);
                doWriteValue(abstractParameter);
                doWriteTime(abstractParameter);
                this.writer.newLine();
            } catch (IOException ioe) {
                throw new TabDelimHandlerProtempaException(ioe);
            }
        }

        @Override
        public void visit(Event event)
                throws TabDelimHandlerProtempaException {
            try {
                doWriteKeyId();
                doWritePropId(event);
                this.writer.write(COLUMN_DELIMITER);
                doWriteTime(event);
                this.writer.newLine();
            } catch (IOException ioe) {
                throw new TabDelimHandlerProtempaException(ioe);
            }
        }

        @Override
        public void visit(PrimitiveParameter primitiveParameter)
                throws TabDelimHandlerProtempaException {
            try {
                doWriteKeyId();
                doWritePropId(primitiveParameter);
                doWriteValue(primitiveParameter);
                doWriteTime(primitiveParameter);
                this.writer.newLine();
            } catch (IOException ioe) {
                throw new TabDelimHandlerProtempaException(ioe);
            }
        }

        @Override
        public void visit(Constant constant)
                throws TabDelimHandlerProtempaException {
            try {
                doWriteKeyId();
                doWritePropId(constant);
                this.writer.write(COLUMN_DELIMITER);
                this.writer.newLine();
            } catch (IOException ioe) {
                throw new TabDelimHandlerProtempaException(ioe);
            }
        }

        private void doWriteKeyId() throws IOException {
            StringUtil.escapeDelimitedColumn(this.keyId,
                    COLUMN_DELIMITER, this.writer);
            this.writer.write(COLUMN_DELIMITER);
        }

        private void doWritePropId(Proposition proposition)
                throws IOException {
            StringUtil.escapeDelimitedColumn(proposition.getId(),
                    COLUMN_DELIMITER, this.writer);
            this.writer.write(COLUMN_DELIMITER);
        }

        private void doWriteValue(Parameter parameter) throws IOException {
            StringUtil.escapeDelimitedColumn(
                    parameter.getValueFormatted(), COLUMN_DELIMITER,
                    this.writer);
            this.writer.write(COLUMN_DELIMITER);
        }

        private void doWriteTime(TemporalProposition proposition)
                throws IOException {
            StringUtil.escapeDelimitedColumn(
                    proposition.getStartFormattedShort(),
                    COLUMN_DELIMITER, this.writer);
            this.writer.write(COLUMN_DELIMITER);
            String finish = proposition.getFinishFormattedShort();
            if (!finish.isEmpty()) {
                StringUtil.escapeDelimitedColumn(finish, COLUMN_DELIMITER,
                        this.writer);
            }
        }
    }
}
