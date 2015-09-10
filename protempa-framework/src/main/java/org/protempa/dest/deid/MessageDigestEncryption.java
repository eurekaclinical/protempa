package org.protempa.dest.deid;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
public class MessageDigestEncryption implements Encryption {
    private final MessageDigestDeidConfig deidConfig;

    public MessageDigestEncryption(MessageDigestDeidConfig deidConfig) {
        if (deidConfig == null) {
            throw new IllegalArgumentException("deidConfig cannot be null");
        }
        this.deidConfig = deidConfig;
    }

    @Override
    public String encrypt(String keyId, String inData) throws EncryptException {
        if (keyId == null) {
            throw new IllegalArgumentException("keyId cannot be null");
        }
        String algorithm = this.deidConfig.getAlgorithm();
        try {
            return encrypt(keyId, MessageDigest.getInstance(algorithm), inData);
        } catch (NoSuchAlgorithmException ex) {
            throw new EncryptException(ex);
        }
    }
    
    private String encrypt(String keyId, MessageDigest digest, String inData) {
        assert keyId != null : "keyId cannot be null";
        assert digest != null : "digest cannot be null";
        if (inData == null) {
            return null;
        }
        byte[] salt = this.deidConfig.getSalt(keyId);
        if (salt != null) {
            digest.update(salt);
        }
        digest.update(inData.getBytes());
        StringBuilder hexBuilder = new StringBuilder();
        for (byte b : digest.digest()) {
            hexBuilder.append(Integer.toHexString(b & 0x00FF));
        }
        return hexBuilder.toString();
    }

}
