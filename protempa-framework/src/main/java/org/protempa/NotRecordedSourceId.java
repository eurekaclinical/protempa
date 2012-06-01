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
package org.protempa;

import java.io.Serializable;

/**
 *
 * @author Andrew Post
 */
public class NotRecordedSourceId implements SourceId, Serializable {
    private static final long serialVersionUID = -1176266763391956808L;
    
    private static final NotRecordedSourceId SINGLETON = new NotRecordedSourceId();
    
    public static NotRecordedSourceId getInstance() {
        return SINGLETON;
    }
    
    private NotRecordedSourceId() {
        
    }

    @Override
    public String getStringRepresentation() {
        return "Source not recorded";
    }
    
}
