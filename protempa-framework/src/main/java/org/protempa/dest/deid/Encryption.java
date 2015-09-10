package org.protempa.dest.deid;

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
public interface Encryption {

    /**
     * Encrypts the provided string. This method applies the
     * DES/ECB/PKCS5Padding cipher and outputs the encrypted data as a hex
     * string. If an exception is thrown, this instance may no longer be
     * usable.
     *
     * @param str the string to encrypt.
     * @return the encrypted data as a hex string, or <code>null</code> if the
     * provided string is <code>null</code>.
     *
     * @throws EncryptException if the provided string cannot be
     * encrypted.
     */
    String encrypt(String keyId, String str) throws EncryptException;
    
}
