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
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.codec.binary.Base64;

/**
 * Utility class for encrypting and decrypting strings. Uses the
 * DES/ECB/PKCS5Padding cipher transformation, which supports a key size of up
 * to 56 bytes.
 *
 * @author Andrew Post
 */
public class CipherEncryption implements Encryption {

    private Cipher encryptCipher;
    private Cipher decryptCipher;
    private final CipherDeidConfig deidConfig;
    private final KeyGenerator keygen;

    public CipherEncryption(CipherDeidConfig deidConfig) throws EncryptionInitException {
        if (deidConfig == null) {
            throw new IllegalArgumentException("deidConfig cannot be null");
        }
        this.deidConfig = deidConfig;
        try {
            this.keygen = KeyGenerator.getInstance(this.deidConfig.getKeyAlgorithm());
        } catch (NoSuchAlgorithmException ex) {
            throw new EncryptionInitException(ex);
        }
;
    }

    private void initCiphers() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        synchronized (this.keygen) {
            if (this.encryptCipher == null) {
                Key key = this.keygen.generateKey();
                this.encryptCipher = Cipher.getInstance(this.deidConfig.getCipherAlgorithm());
                this.encryptCipher.init(Cipher.ENCRYPT_MODE, key);
                this.decryptCipher = Cipher.getInstance(this.deidConfig.getCipherAlgorithm());
                this.decryptCipher.init(Cipher.DECRYPT_MODE, key);
            }
        }
    }

    /**
     * Encrypts the provided string. This method applies the
     * DES/ECB/PKCS5Padding cipher and outputs the encrypted data as a hex
     * string. If an exception is thrown, this instance may no longer be usable.
     *
     * @param str the string to encrypt.
     * @return the encrypted data as a hex string, or <code>null</code> if the
     * provided string is <code>null</code>.
     *
     * @throws IllegalBlockSizeException if the provided string cannot be
     * encrypted.
     */
    @Override
    public String encrypt(String keyId, String str) throws EncryptException {
        if (str == null) {
            return null;
        }
        try {
            initCiphers();
            byte[] cleartext = str.getBytes("UTF-8");
            byte[] ciphertext = this.encryptCipher.doFinal(cleartext);
            return Base64.encodeBase64String(ciphertext);
        } catch (InvalidKeyException ex) {
            throw new AssertionError(ex);
        } catch (BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException ex) {
            throw new EncryptException("Could not encrypt string", ex);
        } catch (UnsupportedEncodingException ex) {
            throw new AssertionError("UTF-8 should be supported but is not");
        }
    }

    /**
     * Decrypts the provided hex string. This method converts a hex string to a
     * byte array and passes the array to a decryption cipher. If an exception
     * is thrown, this instance may no longer be usable.
     *
     * @param str the hex string to encrypt.
     * @return the encrypted version of the string, or <code>null</code> if the
     * provided string is <code>null</code>.
     *
     * @throws BadPaddingException if the provided string lacks padding.
     * @throws IllegalBlockSizeException if the provided string otherwise cannot
     * be decrypted.
     */
    String decrypt(String str) throws DecryptException {
        if (str == null) {
            return null;
        }
        try {
            initCiphers();
            byte[] ciphertext = Base64.decodeBase64(str);
            byte[] cleartext = this.decryptCipher.doFinal(ciphertext);
            return new String(cleartext);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
            throw new DecryptException(ex);
        } catch (InvalidKeyException ex) {
            throw new AssertionError(ex);
        }

    }

}
