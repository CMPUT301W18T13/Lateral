package com.lateral.lateral;

/**
 * Constants for use in the app
 */
public final class Constants {
    public static int USERNAME_CHAR_MINIMUM = 8;
    public static int USERNAME_CHAR_LIMIT = 16;
    public static int PASSWORD_CHAR_MINIMUM = 8;
    public static int PHONE_NUMBER_CHAR_SIZE = 10;

    public static int TITLE_CHAR_LIMIT = 30;
    public static int DESCRIPTION_CHAR_LIMIT = 300;
    public static int PHOTOGRAPH_MAX_BYTES = 65536;

    public static int MIN_BID_AMOUNT = 0;

    // Warning - changing these will make the currently stored passwords invalid!
    public static int PBKDF2_ITERATIONS_COUNT = 10000;
    public static int PBKDF2_KEY_LENGTH = 256;

    public static String USER_FILE_NAME = "user_profile.sav";
    public static int MAX_PHOTOS = 5;
}
