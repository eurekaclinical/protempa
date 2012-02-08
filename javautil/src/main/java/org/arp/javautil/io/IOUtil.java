/*
 * #%L
 * JavaUtil
 * %%
 * Copyright (C) 2012 Emory University
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
package org.arp.javautil.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Convenience routines for reading files, resources, readers, and input
 * streams.
 * 
 * @author Andrew Post
 */
public final class IOUtil {
    
    private static class LazyLoggerHolder {

        private static Logger instance = 
                Logger.getLogger(IOUtil.class.getPackage().getName());
    }

    private IOUtil() {
    }

    /**
     * Reads everything from the given <code>Reader</code> into a
     * <code>String</code>. No buffering is implemented over what is already
     * provided by the given <code>Reader</code>.
     * 
     * @param reader
     *            a <code>Reader</code>.
     * @return a <code>String</code>.
     * @throws IOException
     */
    public static String readAllAsString(Reader reader) throws IOException {
        StringBuilder buf = new StringBuilder();
        int ch;
        while ((ch = reader.read()) > -1) {
            buf.append((char) ch);
        }
        return buf.toString();
    }

    /**
     * Reads everything from the given text file into a <code>String</code>.
     * 
     * @param fileName
     *            a file name <code>String</code>
     * @return a <code>String</code>.
     * @throws IOException
     */
    public static String readFileAsString(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        try {
            return readAllAsString(reader);
        } finally {
            reader.close();
        }
    }

    /**
     * Reads everything from the given text file into a <code>String</code>.
     *
     * @param file
     *            a {@link File}.
     * @return a {@link String}.
     * @throws IOException
     */
    public static String readFileAsString(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            return readAllAsString(reader);
        } finally {
            reader.close();
        }
    }

    /**
     * Reads everything from the given resource into a <code>String</code>.
     * 
     * @param classObj
     *            the <code>Class</code> from which
     *            <code>Class.getResourceAsStream</code> is invoked, cannot be
     *            <code>null</code>.
     * @param resourceName
     *            a resource name <code>String</code>.
     * @return a <code>String</code>, or <code>null</code> if
     *         <code>resourceName</code> is <code>null</code>.
     * @throws IOException
     */
    public static String readResourceAsString(Class<?> classObj,
            String resourceName) throws IOException {
        if (classObj == null) {
            throw new IllegalArgumentException("classObj cannot be null");
        }
        if (resourceName == null) {
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                classObj.getResourceAsStream(resourceName)));
        try {
            return readAllAsString(reader);
        } finally {
            reader.close();
        }
    }

    /**
     * Loads properties from the given resource into the given properties
     * object.
     * 
     * @param properties
     *            the {@link Properties}, using the given class'
     *            <code>getResourceAsStream</code> method.
     * @param classObj
     *            the {@link Class} whose <code>getResourceAsStream</code>
     *            method is called, cannot be <code>null</code>.
     * @param resourceName
     *            a resource {@link String}, visible from <code>classObj</code>'s
     *            class loader. If <code>null</code>, this method does
     *            nothing.
     * @throws IOException
     *             if an error occurred reading the resource.
     */
    public static void readPropertiesFromResource(Properties properties,
            Class<?> classObj, String resourceName) throws IOException {
        if (classObj == null) {
            throw new IllegalArgumentException("classObj cannot be null");
        }
        if (resourceName != null) {
            InputStream inputStream = classObj.getResourceAsStream(resourceName);
            if (inputStream != null) {
                try {
                    properties.load(inputStream);
                } finally {
                    inputStream.close();
                }
            } else {
                throw new IOException(resourceName + " not found.");
            }
        }
    }

    /**
     * Creates a <code>Properties</code> objects and loads the data in the
     * given resource, using the given class' <code>getResourceAsStream</code>
     * method.
     * 
     * @param classObj
     *            the {@link Class} whose <code>getResourceAsStream</code>
     *            method is called, cannot be <code>null</code>.
     * @param resourceName
     *            a resource {@link String}, visible from <code>classObj</code>'s
     *            class loader. If <code>null</code>, this methods returns an
     *            empty {@link Properties} object.
     * @return a <code>Properties</code> object.
     * @throws IOException
     */
    public static Properties createPropertiesFromResource(Class<?> classObj,
            String resourceName) throws IOException {
        Properties result = new Properties();
        readPropertiesFromResource(result, classObj, resourceName);
        return result;
    }

    public static String getUserInput(String prompt) throws IOException {
        BufferedReader userIn = new BufferedReader(new InputStreamReader(
                System.in));
        System.out.print(prompt);
        try {
            return userIn.readLine();
        } finally {
            userIn.close();
        }
    }

    public static Properties loadPropertiesFromResource(Class<?> cls,
            String resource) throws IOException {
        return createPropertiesFromResource(cls, resource);
    }

    public static Properties[] loadPropertiesFromResources(Class<?> cls,
            String[] resources) throws IOException {
        if (resources == null) {
            return null;
        }
        Properties[] result = new Properties[resources.length];
        for (int i = 0; i < resources.length; i++) {
            result[i] = loadPropertiesFromResource(cls, resources[i]);
        }
        return result;
    }
    
    /**
     * Finds a resource with a given name, using this class' class loader.
     * Functions identically to 
     * {@link Class#getResourceAsStream(java.lang.String)}, except it throws
     * an {@link IllegalArgumentException} if the specified resource name is
     * <code>null</code>, and it throws an {@link IOException} if no resource
     * with the specified name is found.
     * 
     * @param name name {@link String} of the desired resource. Cannot be
     * <code>null</code>.
     * @return an {@link InputStream}.
     * @throws IOException if no resource with this name is found.
     */
    public static InputStream getResourceAsStream(String name) 
            throws IOException {
        return getResourceAsStream(name, null);
    }
    
    /**
     * Finds a resource with a given name using the class loader of the
     * specified class. Functions identically to 
     * {@link Class#getResourceAsStream(java.lang.String)}, except it throws
     * an {@link IllegalArgumentException} if the specified resource name is
     * <code>null</code>, and it throws an {@link IOException} if no resource
     * with the specified name is found.
     * 
     * @param name name {@link String} of the desired resource. Cannot be
     * <code>null</code>.
     * @param cls the {@link Class<?>} whose loader to use.
     * @return an {@link InputStream}.
     * @throws IOException if no resource with this name is found.
     */
    public static InputStream getResourceAsStream(String name, Class<?> cls) 
            throws IOException {
        if (cls == null) {
            cls = IOUtil.class;
        }
        if (name == null) {
            throw new IllegalArgumentException("resource cannot be null");
        }
        InputStream result = cls.getResourceAsStream(name);
        if (result == null) {
            throw new IOException("Could not open resource " + name + 
                    " using " + cls.getName() + "'s class loader");
        }
        return result;
    }
    
    /**
     * Gets the logger for this package.
     * 
     * @return a <code>Logger</code> object.
     */
    static Logger logger() {
        return LazyLoggerHolder.instance;
    }
}
