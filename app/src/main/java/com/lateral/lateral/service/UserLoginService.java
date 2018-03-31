/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.service;

import android.content.Intent;
import android.util.Log;
import android.content.Context;

import com.lateral.lateral.Constants;
import com.lateral.lateral.activity.MainActivity;
import com.lateral.lateral.model.User;
import com.lateral.lateral.service.implementation.DefaultUserService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import static com.lateral.lateral.Constants.PASSWORD_CHAR_MINIMUM;
import static com.lateral.lateral.Constants.USERNAME_CHAR_LIMIT;
import static com.lateral.lateral.Constants.USERNAME_CHAR_MINIMUM;
import static com.lateral.lateral.Constants.USER_FILE_NAME;


/**
 * Static utility class for computing hashed passwords
 */
public class UserLoginService {

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

    /**
     * Attempts to load a user from the stored user info file on the disk.
     * @return The user ID if the token matches; null otherwise;
     */
    public static String loadUserFromToken(Context context) {
        try {
            FileInputStream fis = context.openFileInput(USER_FILE_NAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));

            String userId = in.readLine();
            String savedToken = in.readLine();
            DefaultUserService defaultUserService = new DefaultUserService();

            User user = defaultUserService.getById(userId);

            if (user != null){
                String pulledToken = user.getToken();
                if (pulledToken.equals(savedToken)){
                    return userId;
                }
                return null;
            }
            return null;

        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            Log.e("loadUserFromFile", "Error when trying to load file: " + e.getMessage());
            throw new RuntimeException();
        }
    }

    /**
     * Saves a user's ID and token to a file.
     */
    public static void saveUserToken(User user, Context context){
        try {
            FileOutputStream fos = context.openFileOutput(USER_FILE_NAME,
                    Context.MODE_PRIVATE);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fos));

            String newToken = randomBytes(32);
            user.setToken(newToken);

            DefaultUserService defaultUserService = new DefaultUserService();
            defaultUserService.update(user);

            out.write(user.getId());
            out.newLine();
            out.write(user.getToken());
            out.flush();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }
    }

    /**
     * Starts the main activity, passing through the User's ID
     * @param userId User ID to login
     * @param context Context to login from
     */
    public static void login(String userId, Context context){
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        mainActivityIntent.putExtra("userId", userId);
        context.startActivity(mainActivityIntent);
    }

    /**
     * Verifies that the username matches complexity requirements
     * @param username Username to verify
     * @return True if requirements met; false otherwise
     */
    public static boolean isUsernameValid(String username){
        return username.length() >= USERNAME_CHAR_MINIMUM
                && username.length() <= USERNAME_CHAR_LIMIT
                && !username.contains("@")
                && !username.contains(" ");
    }

    /**
     * Verifies that the username isn't used by another user
     * @param username Username to verify
     * @return True if requirement met; false otherwise
     */
    public static boolean isUsernameTaken(String username){
        DefaultUserService defaultUserService = new DefaultUserService();
        User user = defaultUserService.getUserByUsername(username);
        return user == null;
    }

    /**
     * Verifies that the phone number matches a certain format
     * @param phoneNumber Phone number to verify
     * @return True if requirements met; false otherwise
     */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        /*
        Regex matches on any 1 or digit country code, area code in or out of brackets
        and the rest of the digits separated by ".", "-", " ", or nothing
        Note: doesn't work on non-Canadian/American phone number formats
         */
        return phoneNumber.matches("^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]?\\d{3}[\\s.-]?\\d{4}$");
    }

    /**
     * Verifies that the email matches a certain format
     * @param email Email to verify
     * @return True if requirements met; false otherwise
     */
    public static boolean isEmailValid(String email) {
        /*
        Regex matches on any string of the form "username@website.tld",
         */
        return email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    /**
     * Verifies that the password meets certain complexity requirements
     * @param password Password to verify
     * @return True if requirements met; false otherwise
     */
    public static boolean isPasswordValid(String password) {
        return password.length() >= PASSWORD_CHAR_MINIMUM;
    }

}
