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
package org.arp.javautil.log;

/**
 * Utilities for elapsed time.
 * 
 * @author Michael Brown
 * 
 */
public class ElapsedTime {

    public static long getElapsedMilliseconds(long now, long then) {
        return now - then;
    }

    public static String getElapsedTime(long now, long then) {
        if (getElapsedSeconds(now, then) > 60F) {
            if (getElapsedMinutes(now, then) > 60)
                return String.format("%.2f hours", getElapsedHours(now, then));
            else
                return String.format("%.2f minutes",
                        getElapsedMinutes(now, then));
        } else
            return String.format("%.2f seconds", getElapsedSeconds(now, then));
    }

    public static float getElapsedHours(long now, long then) {
        return getElapsedMilliseconds(now, then) / 1000F / 60 / 60;
    }

    public static float getElapsedMinutes(long now, long then) {
        return getElapsedMilliseconds(now, then) / 1000F / 60;
    }

    public static float getElapsedSeconds(long now, long then) {
        return getElapsedMilliseconds(now, then) / 1000F;
    }
}
