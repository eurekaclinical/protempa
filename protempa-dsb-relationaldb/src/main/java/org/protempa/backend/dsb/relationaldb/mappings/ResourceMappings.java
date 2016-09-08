package org.protempa.backend.dsb.relationaldb.mappings;

import java.io.IOException;
import java.io.InputStreamReader;
import org.arp.javautil.io.IOUtil;

/**
 * Maps proposition IDs from the knowledge source to SQL. Looks for mapping
 * sources (typically files) in a specified resource location (typically a file
 * system directory).
 * 
 * @author Andrew Post
 */
public final class ResourceMappings extends DefaultMappings {

    /**
     * Initializes the mapper. Accepts the resource location where the mapping
     * resources can be found and the class whose loader to use.
     * 
     * @param resourcePrefix
     *            where the mapping resources are found (as a {@link String}).
     *            Typically a file system directory.
     * @param cls
     *            the {@link Class} whose resource loader to use
     */
    ResourceMappings(String resource, Class<?> cls) throws IOException {
        super(resource, new CSVSupport().read(new InputStreamReader(IOUtil.getResourceAsStream(resource, cls))));
    }

}
