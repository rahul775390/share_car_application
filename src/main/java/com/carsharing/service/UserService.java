package com.carsharing.service;

import com.carsharing.dao.UserDAO;
import com.carsharing.model.User;
import com.carsharing.util.PasswordHasher;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    // Constructor for dependency injection (useful for tests)
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Registers a new user with password hashing and parameter validations.
     * @param user User entity to be saved.
     * @param plainPassword Plain-text password input.
     * @return true if successfully saved, false otherwise.
     */
    public boolean registerUser(User user, String plainPassword) {
        if (user == null || plainPassword == null || plainPassword.trim().isEmpty()) {
            return false;
        }
        
        // Check if email already registered
        if (userDAO.findByEmail(user.getEmail()) != null) {
            return false;
        }

        // Validate basic inputs
        if (user.getName() == null || user.getName().trim().isEmpty() ||
            user.getEmail() == null || user.getEmail().trim().isEmpty() ||
            user.getPhone() == null || user.getPhone().trim().isEmpty() ||
            user.getCity() == null || user.getCity().trim().isEmpty()) {
            return false;
        }

        // Hash the password and save
        String hashedPassword = PasswordHasher.hashPassword(plainPassword);
        user.setPasswordHash(hashedPassword);
        user.setRole("USER"); // Default role
        user.setStatus("ACTIVE"); // Default status

        return userDAO.insert(user);
    }

    /**
     * Authenticates a user based on email and password.
     * @param email User email.
     * @param plainPassword Plain-text password.
     * @return User object if credentials match and account is active, null otherwise.
     */
    public User loginUser(String email, String plainPassword) {
        if (email == null || plainPassword == null) {
            return null;
        }

        User user = userDAO.findByEmail(email);
        if (user == null) {
            return null;
        }

        // Check password matching
        if (!PasswordHasher.checkPassword(plainPassword, user.getPasswordHash())) {
            return null;
        }

        // Check if account status is ACTIVE
        if (!"ACTIVE".equals(user.getStatus())) {
            return null;
        }

        return user;
    }

    /**
     * Retrieves a user by their ID.
     */
    public User getUserById(int id) {
        return userDAO.findById(id);
    }

    /**
     * Updates user profile fields.
     */
    public boolean updateProfile(int id, String name, String phone, String city) {
        User user = userDAO.findById(id);
        if (user == null || 
            name == null || name.trim().isEmpty() || 
            phone == null || phone.trim().isEmpty() ||
            city == null || city.trim().isEmpty()) {
            return false;
        }
        user.setName(name);
        user.setPhone(phone);
        user.setCity(city);
        return userDAO.update(user);
    }
}
