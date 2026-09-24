package com.carsharing.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordHasherTest {

    @Test
    public void testHashAndPasswordMatch() {
        String password = "mySecurePassword123";
        String hashed = PasswordHasher.hashPassword(password);
        
        assertNotNull(hashed);
        assertNotEquals(password, hashed);
        assertTrue(PasswordHasher.checkPassword(password, hashed));
    }

    @Test
    public void testInvalidPasswordNoMatch() {
        String password = "correct_password";
        String wrongPassword = "wrong_password";
        String hashed = PasswordHasher.hashPassword(password);

        assertFalse(PasswordHasher.checkPassword(wrongPassword, hashed));
    }

    @Test
    public void testNullInputs() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordHasher.hashPassword(null);
        });

        assertFalse(PasswordHasher.checkPassword(null, "hash"));
        assertFalse(PasswordHasher.checkPassword("password", null));
    }
}
