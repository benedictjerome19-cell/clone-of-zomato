package com.yourname.yournameeats.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility for hashing and verifying passwords using bcrypt.
 */
public class PasswordUtil {

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean verify(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
