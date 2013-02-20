/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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
 * The units for a proposition's temporal or spatial position, represented as
 * the distance from some origin. Each unit has a "length" in an implied "base"
 * unit. For example, for units representing absolute time, the base unit could
 * be milliseconds since the epoch, and each unit would have a length in
 * milliseconds. It extends {@link Serializable} because
 * {@link org.protempa.proposition.Proposition}s are
 * serializable.
 * 
 * @author Andrew Post
 */
public interface Granularity extends Comparable<Granularity>, Serializable {

    /**
     * Returns the granularity's plural name.
     *
     * @return a {@link String}
     */
    String getPluralName();

    /**
     * Returns the granularity's singular name.
     *
     * @return a {@link String}
     */
    String getName();

    /**
     * Returns a shorter version of the granularity's name.
     *
     * @return a {@link String}
     */
    String getAbbrevatedName();

    Format getLongFormat();

    Format getMediumFormat();

    Format getShortFormat();

    long earliest(long pos);

    long latest(long pos);

    long minimumDistance(long position, long distance, Unit distanceUnit);

    long maximumDistance(long position, long distance, Unit distanceUnit);

    /**
     * Calculates the distance between two positions.
     *
     * @param start
     * @param startGranularity
     * @param finish
     * @param finishGranularity
     * @return
     */
    long distance(long start, long finish, Granularity finishGranularity,
            Unit distanceUnit);

    Unit getCorrespondingUnit();
}
