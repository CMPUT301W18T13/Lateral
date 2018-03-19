/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.service;

import android.util.Log;

import com.lateral.lateral.Constants;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


/**
 * Static utility class for computing hashed passwords
 */
public class UserLoginTools {

    /**
     * Hashes a password using the PBKDF2 Standard on SHA-512
     * Warning - changing this could make any stored passwords invalid!!
     * Source https://www.owasp.org/index.php/Hashing_Java
     * @param password The string password to hash
     * @param salt The stored salt associated with a password
     * @return The hex representation of the bytes of the hashed password
     */
    public static String hashPassword( final String password, final String salt) {

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec( password.toCharArray(), hexToBytes(salt), Constants.PBKDF2_ITERATIONS_COUNT, Constants.PBKDF2_KEY_LENGTH );
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            return bytesToHex(res);

        } catch( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Returns the hex representation of a specified number of bytes
     * Source: https://stackoverflow.com/questions/5683206/how-to-create-an-array-of-20-random-bytes
     * @param numBytes Number of bytes needed
     * @return Hex representation of random byte array
     */
    public static String randomBytes(int numBytes){
        byte[] bytes = new byte[numBytes];
        new Random().nextBytes(bytes);
        return bytesToHex(bytes);
    }

    /**
     * Converts a byte array to a String of its hex representation
     * Source: https://stackoverflow.com/questions/2817752/java-code-to-convert-byte-to-hexadecimal
     * @param bytes Byte array to be converted to hex string
     * @return Hex representation of the byte array
     */
    public static String bytesToHex(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * Converts a hex string to its corresponding byte array
     * Source: https://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
     * @param hex Hex string to be converted to byte array
     * @return Byte array of hex string
     */
    public static byte[] hexToBytes(String hex){
        int len = hex.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return bytes;
    }
}
