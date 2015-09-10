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
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

/**
 * Utility class for encrypting and decrypting strings. Uses the
 * DES/ECB/PKCS5Padding cipher transformation, which supports a key size of up
 * to 56 bytes.
 *
 * @author Andrew Post
 */
public class CipherEncryption implements Encryption {

    private static final String stringvector = "0123456789ABCDEF";
    private static final byte[] bytevector = {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F};
    private static final String pseudo[] = {"0", "1", "2",
        "3", "4", "5", "6", "7", "8",
        "9", "A", "B", "C", "D", "E",
        "F"};

    private final Object cipherLock;
    private Cipher encryptCipher;
    private Cipher decryptCipher;
    private final CipherDeidConfig deidConfig;

    public CipherEncryption(CipherDeidConfig deidConfig) {
        if (deidConfig == null) {
            throw new IllegalArgumentException("deidConfig cannot be null");
        }
        this.deidConfig = deidConfig;
        this.cipherLock = new Object();
    }

    private void initCiphers() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        synchronized (this.cipherLock) {
            if (this.encryptCipher == null) {
                KeyGenerator keygen = KeyGenerator.getInstance(this.deidConfig.getKeyAlgorithm());
                Key key = keygen.generateKey();
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
            byte[] cleartext = str.getBytes();
            byte[] ciphertext = this.encryptCipher.doFinal(cleartext);
            return byteArrayToHexString(ciphertext);
        } catch (InvalidKeyException ex) {
            throw new AssertionError(ex);
        } catch (BadPaddingException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException ex) {
            throw new EncryptException("Could not encrypt string", ex);
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
            byte[] ciphertext = hexStringToByteArray(str);
            byte[] cleartext = this.decryptCipher.doFinal(ciphertext);
            return new String(cleartext);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
            throw new DecryptException(ex);
        } catch (InvalidKeyException ex) {
            throw new AssertionError(ex);
        }

    }

    /**
     * Converts a byte[] array to readable string format.
     *
     * @param a byte array.
     * @return a hexadecimal string.
     */
    private static String byteArrayToHexString(byte in[]) {
        byte ch = 0x00;
        int i = 0;
        if (in == null || in.length <= 0) {
            return null;
        }

        StringBuffer out = new StringBuffer(in.length * 2);

        while (i < in.length) {
            ch = (byte) (in[i] & 0xF0); // Strip off high nibble
            ch = (byte) (ch >>> 4);
            // shift the bits down
            ch = (byte) (ch & 0x0F);
            //must do this is high order bit is on!
            out.append(pseudo[(int) ch]); // convert the nibble to a String Character

            ch = (byte) (in[i] & 0x0F); // Strip off low nibble 
            out.append(pseudo[(int) ch]); // convert the nibble to a String Character
            i++;
        }
        String rslt = new String(out);
        return rslt;
    }

    /**
     * Converts a hex string to a byte[] array.
     *
     * @param a string containing only hexadecimal characters.
     * @return a byte array.
     *
     */
    private static byte[] hexStringToByteArray(String hexstring) {

        int i = 0;
        if (hexstring == null || hexstring.length() <= 0) {
            return null;
        }

        byte[] out = new byte[hexstring.length() / 2];
        while (i < hexstring.length() - 1) {
            byte ch = 0x00;
            //Convert high nibble charater to a hex byte
            ch = (byte) (ch | bytevector[stringvector.indexOf(hexstring.charAt(i))]);
            ch = (byte) (ch << 4); //move this to the high bit

            //Convert the low nibble to a hexbyte
            ch = (byte) (ch | bytevector[stringvector.indexOf(hexstring.charAt(i + 1))]); //next hex value
            out[i / 2] = ch;
            i++;
            i++;
        }
        return out;
    }
}
