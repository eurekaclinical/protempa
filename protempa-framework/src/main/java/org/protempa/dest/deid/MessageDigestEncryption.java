package org.protempa.dest.deid;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;

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
    private final MessageDigest messageDigest;

    public MessageDigestEncryption(MessageDigestDeidConfig deidConfig) throws EncryptionInitException {
        if (deidConfig == null) {
            throw new IllegalArgumentException("deidConfig cannot be null");
        }
        this.deidConfig = deidConfig;
        String algorithm = this.deidConfig.getAlgorithm();
        try {
            messageDigest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            throw new EncryptionInitException(ex);
        }
    }

    @Override
    public String encrypt(String keyId, String inData) {
        if (keyId == null) {
            throw new IllegalArgumentException("keyId cannot be null");
        }

        if (inData == null) {
            return null;
        }
        synchronized (this.messageDigest) {
            byte[] salt = this.deidConfig.getSalt(keyId);
            if (salt != null) {
                this.messageDigest.update(salt);
            }
            try {
                byte[] digested = this.messageDigest.digest(inData.getBytes("UTF-8"));
                return Base64.encodeBase64String(digested);
            } catch (UnsupportedEncodingException ex) {
                throw new AssertionError("UTF-8 should be supported but is not");
            }
        }
    }

}
