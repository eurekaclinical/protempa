package org.protempa.query.handler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
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
public class TabDelimQueryResultsHandler extends WriterQueryResultsHandler implements QueryResultsHandler {

    public TabDelimQueryResultsHandler(Writer out) {
        super(out);
    }

    public TabDelimQueryResultsHandler(OutputStream out) {
        super(out);
    }

    public TabDelimQueryResultsHandler(String fileName) throws IOException {
        super(fileName);
    }

    public TabDelimQueryResultsHandler(File file) throws IOException {
        super(file);
    }
	
	@Override
	public void handleQueryResult(String key, List<Proposition> propositions) throws FinderException {
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

		private void doWritePropId(Proposition proposition) throws IOException {
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
