package org.protempa.dest;

import org.apache.commons.lang3.ArrayUtils;
import org.protempa.DataSource;
import org.protempa.KnowledgeSource;

/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2014 Emory University
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
/**
 *
 * @author Andrew Post
 */
public abstract class AbstractDestination implements Destination {

    private String id;

    protected AbstractDestination() {
        this.id = getClass().getName();
    }

    @Override
    public String getId() {
        return this.id;
    }

    /**
     * Returns this destination's display name for user interfaces. This
     * implementation returns the same string as {@link #getId() }.
     * 
     * @return a string.
     */
    @Override
    public String getDisplayName() {
        return getId();
    }

    @Override
    public boolean isGetStatisticsSupported() {
        return false;
    }

    @Override
    public Statistics getStatistics() throws StatisticsException {
        return null;
    }

    @Override
    public String[] getSupportedPropositionIds(DataSource dataSource, KnowledgeSource knowledgeSource) throws GetSupportedPropositionIdsException {
        return ArrayUtils.EMPTY_STRING_ARRAY;
    }

}
