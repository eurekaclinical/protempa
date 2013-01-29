/*
 * #%L
 * Protempa Framework
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
package org.protempa.proposition.value;

import java.io.Serializable;
import java.text.Format;

/**
 * The units for a length. It extends {@link Serializable} so that it can be
 * stored as part of {@link org.protempa.proposition.Proposition}
 * s.
 * 
 * @author Andrew Post
 */
public interface Unit extends Serializable, Comparable<Unit> {

    /**
     * Returns the unit's singular name.
     *
     * @return a {@link String}
     */
    String getName();

    /**
     * Returns a short version of the unit's singular name.
     *
     * @return a {@link String}
     */
    String getAbbreviatedName();

    /**
     * Returns the unit's plural name.
     *
     * @return a {@link String}
     */
    String getPluralName();

    Format getShortFormat();

    Format getMediumFormat();

    Format getLongFormat();

    /**
     * Adds a length in these units to the given position.
     *
     * @param position
     *            a <code>long</code>
     * @param length
     *            an <code>int</code>
     * @return the new position <code>long</code>
     */
    long addToPosition(long position, int length);
}
