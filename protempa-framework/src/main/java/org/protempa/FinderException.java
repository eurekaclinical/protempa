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

/**
 * Thrown when a Protempa query fails. It always has a cause with a more 
 * specific error.
 *
 * @author Andrew Post
 *
 */
public final class FinderException extends ProtempaException {

    private static final long serialVersionUID = 7903820808353618290L;
    
    private final String queryId;

    public FinderException(String queryId, Throwable cause) {
        super("Query " + queryId + " failed", cause);
        if (cause == null) {
            throw new IllegalArgumentException("cause cannot be null");
        }
        if (queryId == null) {
            throw new IllegalArgumentException("queryId cannot be null");
        }
        this.queryId = queryId;
    }
    
    public String getQueryId() {
        return this.queryId;
    }
}
