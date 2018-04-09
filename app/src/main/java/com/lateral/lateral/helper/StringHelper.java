/*
 * Copyright (c) 2018 Team 13. CMPUT301. University of Alberta - All Rights Reserved.
 * You may use, distribute, or modify this code under terms and conditions of the Code of Student Behaviour at University of Alberta.
 * You can find a copy of the license in this project. Otherwise, please contact cjmerkos@ualberta.ca
 */

package com.lateral.lateral.helper;

/**
 * Helper class for strings
 */
public class StringHelper {

    /**
     * Modify string to make it safe for JSON input
     * @param string input string
     * @return safe string
     */
    public static String makeJsonSafe(String string){
        StringBuilder builder = new StringBuilder();
        for(char c : string.toCharArray()) {
            switch(c){
                case '\"':
                case '\\':
                    builder.append( "\\" );
                    // Fall though, no break
                default:
                    builder.append(c);
            }
        }

        return builder.toString();
    }
}
