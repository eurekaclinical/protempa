package org.protempa.query.handler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.protempa.FinderException;
import org.protempa.ProtempaException;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.AbstractPropositionCheckedVisitor;
import org.protempa.proposition.ConstantParameter;
import org.protempa.proposition.Event;
import org.protempa.proposition.Parameter;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.TemporalProposition;

/**
 * An implementation of QueryResultsHandler providing functionality for
 * writing the results to an output stream in a tab-delimited format.
 * 
 * @author Michel Mansour
 *
 */
public class TabDelimQueryResultsHandler extends WriterQueryResultsHandler
        implements QueryResultsHandler {

    private final List<Comparator<Proposition>> comparator;

    /**
     * Instantiates this handler to write to a {@link Writer}. No sorting
     * will be performed.
     * 
     * @param out a {@link Writer}.
     */
    public TabDelimQueryResultsHandler(Writer out) {
        super(out);
        this.comparator = Collections.emptyList();
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
        super(out);
        if (comparator == null) {
            this.comparator = Collections.emptyList();
        } else {
            this.comparator = 
                new ArrayList<Comparator<Proposition>>(comparator);
        }
        
    }

    /**
     * Instantiates this handler to write to an {@link OutputStream}.
     * No sorting will be performed.
     *
     * @param out an {@link OutputStream}.
     */
    public TabDelimQueryResultsHandler(OutputStream out) {
        super(out);
        this.comparator = Collections.emptyList();
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
        super(out);
        if (comparator == null) {
            this.comparator = Collections.emptyList();
        } else {
            this.comparator =
                new ArrayList<Comparator<Proposition>>(comparator);
        }
    }

    /**
     * Instantiates this handler to write to a file. No sorting will be
     * performed.
     *
     * @param fileName a file name {@link String}.
     * @throws IOException if an error occurred opening/creating the file.
     */
    public TabDelimQueryResultsHandler(String fileName) throws IOException {
        super(fileName);
        this.comparator = Collections.emptyList();
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
        super(fileName);
        if (comparator == null) {
            this.comparator = Collections.emptyList();
        } else {
            this.comparator =
                    new ArrayList<Comparator<Proposition>>(comparator);
        }
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
        super(file);
        this.comparator = Collections.emptyList();
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
        super(file);
        if (comparator == null) {
            this.comparator = Collections.emptyList();
        } else {
            this.comparator =
                    new ArrayList<Comparator<Proposition>>(comparator);
        }
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
    public void handleQueryResult(String key, List<Proposition> propositions)
            throws FinderException {
        for (Comparator<Proposition> c : this.comparator)
            Collections.sort(propositions, c);
        try {
            new TabDelimHandlerPropositionVisitor(key, this)
                    .visit(propositions);
        } catch (TabDelimHandlerProtempaException pe) {
            throw new FinderException(pe);
        } catch (ProtempaException pe) {
            throw new AssertionError(pe);
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

        private final String keyId;
        private final TabDelimQueryResultsHandler writer;

        TabDelimHandlerPropositionVisitor(String keyId,
                TabDelimQueryResultsHandler writer) {
            this.keyId = keyId;
            this.writer = writer;
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
                this.writer.write('\t');
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
        public void visit(ConstantParameter constantParameter)
                throws TabDelimHandlerProtempaException {
            try {
                doWriteKeyId();
                doWritePropId(constantParameter);
                doWriteValue(constantParameter);
                this.writer.write('\t');
                this.writer.newLine();
            } catch (IOException ioe) {
                throw new TabDelimHandlerProtempaException(ioe);
            }
        }

        private void doWriteKeyId() throws IOException {
            this.writer.write(this.keyId);
            this.writer.write('\t');
        }

        private void doWritePropId(Proposition proposition)
                throws IOException {
            this.writer.write(proposition.getId());
            this.writer.write('\t');
        }

        private void doWriteValue(Parameter parameter) throws IOException {
            this.writer.write(parameter.getValueFormatted());
            this.writer.write('\t');
        }

        private void doWriteTime(TemporalProposition proposition)
                throws IOException {
            this.writer.write(proposition.getStartFormattedShort());
            this.writer.write('\t');
            this.writer.write(proposition.getFinishFormattedShort());
        }
    }
}
