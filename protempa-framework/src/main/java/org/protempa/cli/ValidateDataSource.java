package org.protempa.cli;

import org.apache.commons.cli.CommandLine;
import org.protempa.DataSourceFailedValidationException;
import org.protempa.DataSourceValidationIncompleteException;
import org.protempa.Protempa;

/**
 * Validates the data source for consistency between the knowledge source
 * and the database mappings.
 * 
 * @author Andrew Post
 */
public class ValidateDataSource extends CLI {

    public ValidateDataSource() {
        super(System.getProperty("app.name"));
    }

    @Override
    public void execute(Protempa protempa, CommandLine commandLine)
            throws CLIException {
        try {
            System.out.println(
                    "Validating data source. This may take some time...");
            protempa.validateDataSource();
            System.out.println(
               "Data source validation completed with no validation failures.");
        } catch (DataSourceFailedValidationException ex) {
            printException(ex);
        } catch (DataSourceValidationIncompleteException ex) {
            printException(ex);
        }
    }

    public static void main(String[] args) {
        ValidateDataSource validator = new ValidateDataSource();
        validator.processOptionsAndArgs(args);
        validator.initializeExecuteAndClose();
    }
}
