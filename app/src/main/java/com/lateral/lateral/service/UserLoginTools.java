package com.lateral.lateral.service;

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
    public static byte[] hashPassword( final char[] password, final byte[] salt, final int iterations, final int keyLength ) {

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
            PBEKeySpec spec = new PBEKeySpec( password, salt, iterations, keyLength );
            SecretKey key = skf.generateSecret( spec );
            byte[] res = key.getEncoded( );
            return res;

        } catch( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException( e );
        }
    }

    // Source: https://stackoverflow.com/questions/20536566/creating-a-random-string-with-a-z-and-0-9-in-java
    public static String randomString(int length){
        String ALPHANUMERIC = "abcdefghijklmnopwrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String string = new String();
        Random rnd = new Random();

        while(string.length() < length){
            int index = (int) (rnd.nextFloat() * ALPHANUMERIC.length());
            string += ALPHANUMERIC.charAt(index);
        }

        return string;
    }
}
