package org.arp.javautil.serviceloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author Andrew Post
 */
public class ServiceLoader {

    public static <S> List<Class<? extends S>> load(Class<S> ifc)
            throws IOException, ClassNotFoundException {
        return load(ifc, null);
    }

    public static <S> List<Class<? extends S>> load(Class<S> ifc,
            ClassLoader ldr)
            throws IOException, ClassNotFoundException  {
        if (ldr == null)
            ldr = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> e = ldr.getResources("META-INF/services/" +
                ifc.getName());
        List<Class<? extends S>> services = new ArrayList<Class<? extends S>>();
        while (e.hasMoreElements()) {
            URL url = e.nextElement();
            InputStream is = url.openStream();
            try {
                BufferedReader r = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                while (true) {
                    String line = r.readLine();
                    if (line == null) {
                        break;
                    }
                    int comment = line.indexOf('#');
                    if (comment >= 0) {
                        line = line.substring(0, comment);
                    }
                    String name = line.trim();
                    if (name.length() == 0) {
                        continue;
                    }
                    Class<?> clz = Class.forName(name, true, ldr);
                    Class<? extends S> impl = clz.asSubclass(ifc);
                    
                    services.add(impl);
                }
            } finally {
                is.close();
            }
        }
        return services;
    }
}
