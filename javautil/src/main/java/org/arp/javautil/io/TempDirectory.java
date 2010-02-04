package org.arp.javautil.io;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 *
 * @author Andrew Post
 */
public class TempDirectory {
    private static final Object tmpDirectoryLock = new Object();

    private static int counter = -1; /* Protected by tmpDirectoryLock */
    private static String tmpdir;

    private static File generateFile(String prefix, String suffix, File dir)
        throws IOException {
        if (counter == -1) {
            counter = new Random().nextInt() & 0xffff;
        }
        counter++;
        return new File(dir, prefix + Integer.toString(counter) + suffix);
    }

   public static File create(String prefix, String suffix,
                                      File directory)
        throws IOException {
        if (prefix == null) throw new NullPointerException();
        if (prefix.length() < 3)
            throw new IllegalArgumentException("Prefix string too short");
        String s = (suffix == null) ? ".tmp" : suffix;
        synchronized (tmpDirectoryLock) {
            if (directory == null) {
                String tmpDir = getTempDir();
                directory = new File(tmpDir);
            }
            File f;
            do {
                f = generateFile(prefix, s, directory);
            } while (!f.mkdir());
            return f;
        }
    }


    public static File create(String prefix, String suffix)
        throws IOException {
        return create(prefix, suffix, null);
    }

    private static String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }
}
