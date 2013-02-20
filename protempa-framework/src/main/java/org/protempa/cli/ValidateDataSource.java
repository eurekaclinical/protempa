/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
