package services;

import models.User;
import utils.FileHandler;
import java.util.List;

public class AuthenticationService {

    // Attempts to log a user in. Returns the User object if successful, null otherwise.
    public static User login(String username, String password) {
        List<User> users = FileHandler.loadAllUsers();
        for (User user : users) {
             if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                 return user; // Successful login
             }
        }
        return null; // Login failed
    }

    // Registers a new user. Returns true if successful.
    public static String registerUser(User newUser) {
        List<User> users = FileHandler.loadAllUsers();
        
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(newUser.getUsername())) {
                return "Username already exists.";
            }
            if (user.getContactNumber().equals(newUser.getContactNumber())) {
                return "Contact number already registered.";
            }
        }
        
        boolean saved = FileHandler.saveUser(newUser);
        return saved ? "Success" : "Failed to save user.";
    }
}
