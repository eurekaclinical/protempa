package org.protempa.bp.commons.dsb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import org.protempa.backend.BackendInstanceSpec;
import org.protempa.backend.InvalidPropertyNameException;

/**
 *
 * @author Andrew Post
 */
class DSBUtil {
    private DSBUtil() {

    }

    private static class LazyLoggerHolder {
		private static Logger instance = Logger.getLogger(DSBUtil.class
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

    static void initializeAdaptorFields(Class annotationCls, Object adaptor,
            BackendInstanceSpec backendInstanceSpec) {
        assert annotationCls != null : "annotationCls cannot be null";
        assert adaptor != null : "adaptor cannot be null";
        assert backendInstanceSpec != null
                : "backendInstanceSpec cannot be null";
        for (Method method : adaptor.getClass().getMethods()) {
            if (method.isAnnotationPresent(annotationCls)) {
                try {
                    String setterName = method.getName();
                    String propertyName = setterName.substring(3);
                    char[] propertyNameArr = propertyName.toCharArray();
                    propertyNameArr[0] =
                            Character.toLowerCase(propertyNameArr[0]);
                    propertyName = String.valueOf(propertyNameArr);
                    method.invoke(adaptor,
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
    }
}
