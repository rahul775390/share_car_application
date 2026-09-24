package com.carsharing.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    /**
     * Hashes a plain-text password using BCrypt.
     * @param plainTextPassword Plain text password.
     * @return Hashed password string.
     */
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    /**
     * Checks if a plain-text password matches a hashed password.
     * @param plainTextPassword Plain text password.
     * @param hashedPassword Hashed password stored in database.
     * @return true if matches, false otherwise.
     */
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
