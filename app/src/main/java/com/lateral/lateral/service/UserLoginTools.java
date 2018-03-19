package com.lateral.lateral.service;

import android.util.Log;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Created by cole on 12/03/18.
 */

public class UserLoginTools {

    // Source https://www.owasp.org/index.php/Hashing_Java
    public static String hashPassword( final String password, final String salt) {

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
            PBEKeySpec spec = new PBEKeySpec( password.toCharArray(), hexToBytes(salt), 5000, 256 );
            SecretKey key = skf.generateSecret( spec );
            byte[] res = key.getEncoded( );
            return bytesToHex(res);

        } catch( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException( e );
        }
    }

    // Source: https://stackoverflow.com/questions/5683206/how-to-create-an-array-of-20-random-bytes
    public static String randomBytes(int numBytes){
        byte[] bytes = new byte[numBytes];
        new Random().nextBytes(bytes);
        return bytesToHex(bytes);
    }

    // Source: https://stackoverflow.com/questions/2817752/java-code-to-convert-byte-to-hexadecimal
    private static String bytesToHex(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    // Source: https://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
    private static byte[] hexToBytes(String s){
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
