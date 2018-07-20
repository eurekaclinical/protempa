package org.protempa.query;

/*
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2015 Emory University
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
public enum QueryMode {
    UPDATE, REPLACE, REPROCESS_RETRIEVE, REPROCESS_CREATE, REPROCESS_UPDATE, REPROCESS_DELETE;
    
    private static final QueryMode[] ETL_MODES = {UPDATE, REPLACE};
    private static final QueryMode[] REPROCESS_MODES = {REPROCESS_RETRIEVE, REPROCESS_CREATE, REPROCESS_UPDATE, REPROCESS_DELETE};
    
    public static QueryMode[] etlModes() {
        return ETL_MODES;
    }
    
    public static QueryMode[] reprocessModes() {
        return REPROCESS_MODES;
    }
}
