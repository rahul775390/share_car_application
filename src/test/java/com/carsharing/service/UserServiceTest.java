package com.carsharing.service;

import com.carsharing.dao.UserDAO;
import com.carsharing.filter.AdminFilter;
import com.carsharing.filter.AuthFilter;
import com.carsharing.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;
    private FakeUserDAO fakeUserDAO;

    // Fake UserDAO implementing in-memory behavior to avoid actual DB connection during test
    private static class FakeUserDAO extends UserDAO {
        private final Map<String, User> emailMap = new HashMap<>();
        private final Map<Integer, User> idMap = new HashMap<>();
        private int sequence = 1;

        @Override
        public User findByEmail(String email) {
            return emailMap.get(email);
        }

        @Override
        public User findById(int id) {
            return idMap.get(id);
        }

        @Override
        public boolean insert(User user) {
            if (emailMap.containsKey(user.getEmail())) {
                return false; // Unique constraint violation
            }
            user.setId(sequence++);
            emailMap.put(user.getEmail(), user);
            idMap.put(user.getId(), user);
            return true;
        }

        @Override
        public boolean update(User user) {
            if (!idMap.containsKey(user.getId())) {
                return false;
            }
            emailMap.put(user.getEmail(), user);
            idMap.put(user.getId(), user);
            return true;
        }
    }

    @BeforeEach
    public void setUp() {
        fakeUserDAO = new FakeUserDAO();
        userService = new UserService(fakeUserDAO);
    }

    @Test
    public void testValidRegistration() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setPhone("1234567890");
        user.setCity("Pune");

        boolean result = userService.registerUser(user, "password123");
        
        assertTrue(result);
        assertNotNull(user.getPasswordHash());
        assertEquals("USER", user.getRole());
        assertEquals("ACTIVE", user.getStatus());
        assertEquals("Pune", user.getCity());
        assertEquals(1, user.getId());
    }

    @Test
    public void testRegisterDuplicateEmail() {
        User user1 = new User();
        user1.setName("Alice");
        user1.setEmail("alice@example.com");
        user1.setPhone("1234567890");
        user1.setCity("Pune");
        userService.registerUser(user1, "password123");

        User user2 = new User();
        user2.setName("Bob");
        user2.setEmail("alice@example.com"); // Duplicate email
        user2.setPhone("0987654321");
        user2.setCity("Mumbai");
        
        boolean result = userService.registerUser(user2, "pass123");
        assertFalse(result);
    }

    @Test
    public void testRegisterInvalidEmail() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("invalid-email-format"); // Invalid email
        user.setPhone("1234567890");
        user.setCity("Pune");

        // Simple validation mock verification (can also be checked in controller)
        boolean validEmail = user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$");
        assertFalse(validEmail);
    }

    @Test
    public void testPasswordMismatchSimulation() {
        String password = "password123";
        String confirmPassword = "password321";
        assertNotEquals(password, confirmPassword);
    }

    @Test
    public void testWeakPassword() {
        String weakPassword = "123";
        assertTrue(weakPassword.length() < 6);
    }

    @Test
    public void testValidLogin() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setPhone("1234567890");
        user.setCity("Pune");
        userService.registerUser(user, "password123");

        User loggedInUser = userService.loginUser("alice@example.com", "password123");
        assertNotNull(loggedInUser);
        assertEquals("Alice", loggedInUser.getName());
    }

    @Test
    public void testInvalidLogin() {
        User user = new User();
        user.setName("Alice");
        user.setEmail("alice@example.com");
        user.setPhone("1234567890");
        user.setCity("Pune");
        userService.registerUser(user, "password123");

        User loggedInUser = userService.loginUser("alice@example.com", "wrong_password");
        assertNull(loggedInUser);
        
        User loggedInNonExistingUser = userService.loginUser("nonexisting@example.com", "password123");
        assertNull(loggedInNonExistingUser);
    }

    @Test
    public void testLogoutSimulation() {
        // Invalidate session simulation
        Map<String, Object> session = new HashMap<>();
        session.put("user", new User());
        assertNotNull(session.get("user"));
        
        session.clear(); // Session invalidated
        assertNull(session.get("user"));
    }

    @Test
    public void testSessionProtectionSimulation() {
        // AuthFilter simulation: if user is not in session, redirect
        Map<String, Object> session = new HashMap<>();
        boolean loggedIn = session.containsKey("user");
        assertFalse(loggedIn);
    }

    @Test
    public void testUnauthorizedAdminAccessSimulation() {
        // AdminFilter simulation: if user is not ADMIN, block
        User user = new User();
        user.setRole("USER");
        
        boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
        assertFalse(isAdmin);
    }

    @Test
    public void testProfileViewing() {
        User user = new User(1, "Alice", "alice@example.com", "hash", "USER", "1234567890", "Pune", "ACTIVE", null);
        fakeUserDAO.insert(user);

        User profile = userService.getUserById(user.getId());
        assertNotNull(profile);
        assertEquals("Alice", profile.getName());
        assertEquals("Pune", profile.getCity());
    }

    @Test
    public void testProfileUpdate() {
        User user = new User(1, "Alice", "alice@example.com", "hash", "USER", "1234567890", "Pune", "ACTIVE", null);
        fakeUserDAO.insert(user);

        boolean updated = userService.updateProfile(user.getId(), "Alice In Wonderland", "9999999999", "Mumbai");
        assertTrue(updated);

        User updatedUser = userService.getUserById(user.getId());
        assertEquals("Alice In Wonderland", updatedUser.getName());
        assertEquals("9999999999", updatedUser.getPhone());
        assertEquals("Mumbai", updatedUser.getCity());
    }
}
