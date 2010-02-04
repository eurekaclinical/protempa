package org.protempa.bp.commons;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.InvalidPropertyNameException;

/**
 *
 * @author Andrew Post
 */
class CommonsUtil {
    private static class LazyResourceBundleHolder {
		private static ResourceBundle instance = ResourceBundle
				.getBundle("Messages");
	}

	/**
	 * Gets the messages for this project's resource bundle.
	 *
	 * @return a {@link ResourceBundle}.
	 */
	static ResourceBundle resourceBundle() {
		return LazyResourceBundleHolder.instance;
	}

    static void initializeBackendProperties(Object backend,
            BackendInstanceSpec backendInstanceSpec) {
        assert backend != null : "backend cannot be null";
        assert backendInstanceSpec != null 
                : "backendInstanceSpec cannot be null";
        for (Method method : backend.getClass().getMethods()) {
            if (method.isAnnotationPresent(BackendProperty.class))
                try {
                    String setterName = method.getName();
                    String propertyName = setterName.substring(3);
                    char[] propertyNameArr = propertyName.toCharArray();
                    propertyNameArr[0] =
                            Character.toLowerCase(propertyNameArr[0]);
                    propertyName = String.valueOf(propertyNameArr);
                    method.invoke(backend,
                            new Object[] {
                        backendInstanceSpec.getProperty(propertyName)});
                } catch (IllegalAccessException ex) {
                    throw new AssertionError(ex);
                } catch (InvalidPropertyNameException ex) {
                    throw new AssertionError(ex);
                } catch (InvocationTargetException ex) {
                    throw new AssertionError(ex);
                }
        }
    }

    private static class LazyLoggerHolder {
		private static Logger instance = Logger.getLogger(CommonsUtil.class
				.getPackage().getName());
	}

	/**
	 * Gets the logger for this package.
	 *
	 * @return a {@link Logger} object.
	 */
	static Logger logger() {
		return LazyLoggerHolder.instance;
	}
}
