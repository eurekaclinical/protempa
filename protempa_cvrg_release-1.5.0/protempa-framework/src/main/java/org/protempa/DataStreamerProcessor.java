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

import java.util.ArrayList;
import java.util.List;

/**
 * Helper abstract base class for {@link DataStreamer} and 
 * {@link DataStreamerIterator}, which group data by key id. When these classes 
 * complete aggregating all data for a given key id, they call an associated 
 * {@link DataStreamerProcessor}'s 
 * {@link #fireKeyCompleted} method with the grouped data.
 * There is one method to implement, the {@link #fireKeyCompleted} method,
 * to allow retrieving the grouped data for further processing.
 * 
 * @author Andrew Post
 */
abstract class DataStreamerProcessor<E> {

    static enum ExecuteStatus {

        DID_FIRE,
        DID_NOT_FIRE_COMPATIBLE,
        DID_NOT_FIRE_WRONG_KEYID
    }
    private String currentKeyId;
    private List<E> data;
    private String keyId;

    /**
     * Constructs an instance of this class.
     */
    protected DataStreamerProcessor() {
        this.data = new ArrayList<E>();
    }

    /**
     * If non-<code>null</code>, will cause the associated {@link DataStreamer} 
     * or {@link DataStreamerIterator} to stop processing if it encounters a 
     * key id other than this one.
     * 
     * @return a key id {@link String}.
     */
    final String getKeyId() {
        return keyId;
    }

    /**
     * Provide a non-<code>null</code> key id to cause the associated 
     * {@link DataStreamer} or {@link DataStreamerIterator} to stop processing 
     * if it encounters a key id other than this one.
     * 
     * @param keyId a key id {@link String}.
     */
    final void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    final ExecuteStatus execute(DataStreamingEvent data) 
            throws DataSourceReadException {
        assert data != null : "data cannot be null";
        ExecuteStatus result;
        String kId = data.getKeyId();
        if ((this.currentKeyId != null && checkKeyId(this.currentKeyId))
                || (this.currentKeyId == null && checkKeyId(kId))) {
            if (this.currentKeyId != null && !this.currentKeyId.equals(kId)) {
                assert getKeyId() == null 
                        || this.currentKeyId.equals(getKeyId()) 
                        : "inconsistent keyIds " 
                        + getKeyId() + "; " 
                        + checkKeyId(this.currentKeyId);
                fireKeyCompleted(this.currentKeyId, this.data);
                this.data = new ArrayList<E>();
                result = ExecuteStatus.DID_FIRE;
            } else {
                result = ExecuteStatus.DID_NOT_FIRE_COMPATIBLE;
            }
            this.currentKeyId = kId;
            this.data.addAll(data.getData());
        } else {
            result = ExecuteStatus.DID_NOT_FIRE_WRONG_KEYID;
        }
        return result;
    }

    final boolean finish() throws DataSourceReadException {
        boolean result;
        if (this.currentKeyId != null && checkKeyId(this.currentKeyId)) {
            fireKeyCompleted(this.currentKeyId, this.data);
            result = true;
        } else {
            result = false;
        }
        this.currentKeyId = null;
        return result;
    }

    final boolean checkKeyId(String keyId) {
        return this.keyId == null || this.keyId.equals(keyId);
    }

    /**
     * Called when an associated @link DataStreamer} or 
     * {@link DataStreamerIterator} has finished assembling the data for a
     * key id. Implement this method to retrieve the assembled data for further
     * processing.
     * 
     * @param keyId the key id {@link String} of interest. Guaranteed not
     * <code>null</code>.
     * @param data the data of interest. Guaranteed not <code>null</code>.
     * @throws DataSourceReadException if an error occurs during data 
     * processing within this method.
     */
    protected abstract void fireKeyCompleted(String keyId, List<E> data)
            throws DataSourceReadException;
}
