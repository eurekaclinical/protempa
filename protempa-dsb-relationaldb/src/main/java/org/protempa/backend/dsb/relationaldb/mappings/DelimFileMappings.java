package org.protempa.backend.dsb.relationaldb.mappings;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Maps proposition IDs from the knowledge source to SQL. Looks for mapping
 * sources (typically files) in a specified resource location (typically a file
 * system directory).
 * 
 * @author Andrew Post
 */
public final class DelimFileMappings extends DefaultMappings {

    /**
     * Initializes the mapper. Accepts the resource location where the mapping
     * resources can be found and the class whose loader to use.
     * 
     * @param file
     *            where the mapping resources are found (as a {@link String}).
     *            Typically a file system directory.
     */
    DelimFileMappings(File file) throws IOException {
        super(file.getName(), new CSVSupport().read(new FileReader(file)));
    }

}
