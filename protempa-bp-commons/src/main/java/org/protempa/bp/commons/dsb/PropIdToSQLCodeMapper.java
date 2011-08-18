package org.protempa.bp.commons.dsb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.arp.javautil.io.IOUtil;
import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec;
import org.protempa.bp.commons.dsb.relationaldb.ColumnSpec.KnowledgeSourceIdToSqlCode;

/**
 *
 * @author Andrew Post
 */
public final class PropIdToSQLCodeMapper {
    private final String resourcePrefix;
    private final Class<?> cls;
    
    public PropIdToSQLCodeMapper(String resourcePrefix, Class<?> cls) {
        if (resourcePrefix == null) {
            throw new IllegalArgumentException(
                    "resourcePrefix cannot be null");
        }
        if (cls == null) {
            throw new IllegalArgumentException("cls cannot be null");
        }
        this.resourcePrefix = resourcePrefix;
        this.cls = cls;
    }
    
    public String[] readCodes(String resource, String sep, int colNum) 
            throws IOException {
        if (resource == null) {
            throw new IllegalArgumentException("resource cannot be null");
        }
        if (sep == null) {
            throw new IllegalArgumentException("sep cannot be null");
        }
        if (colNum < 0 || colNum > 1) {
            throw new IllegalArgumentException("Invalid colNum: " + colNum);
        }
        resource = this.resourcePrefix + resource;
        List<String> codes = new ArrayList<String>();
        DSBUtil.logger().log(Level.FINER, "Attempting to get resource: {0}", 
                resource);
        InputStream is = IOUtil.getResourceAsStream(resource, this.cls);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(sep);
                if (cols.length != 2) {
                    throw new AssertionError("Invalid mapping in " + resource + 
                            ": " + line);
                }
                codes.add(cols[colNum].trim());
            }
            br.close();
            br = null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ioe) {
                }
            }
        }
        return codes.toArray(new String[codes.size()]);
    }

    public KnowledgeSourceIdToSqlCode[] propertyNameOrPropIdToSqlCodeArray(
            String resource) throws IOException {
        if (resource == null) {
            throw new IllegalArgumentException("resource cannot be null");
        }
        resource = this.resourcePrefix + resource;
        DSBUtil.logger().log(Level.FINER, "Attempting to get resource: {0}", 
                resource);
        List<ColumnSpec.KnowledgeSourceIdToSqlCode> cvs =
                new ArrayList<ColumnSpec.KnowledgeSourceIdToSqlCode>(1000);
        InputStream is = IOUtil.getResourceAsStream(resource, this.cls);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        int i = 0;
        try {
            while ((line = br.readLine()) != null) {
                String[] cols = line.split("\t");
                if (cols.length != 2) {
                    throw new AssertionError("Invalid mapping in " + resource + 
                            ": " + line);
                }
                cvs.add(new ColumnSpec.KnowledgeSourceIdToSqlCode(cols[0], 
                        cols[1]));
                i++;
            }
            br.close();
            br = null;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ioe) {
                }
            }
        }
        return cvs.toArray(
                new ColumnSpec.KnowledgeSourceIdToSqlCode[cvs.size()]);
    }
}
