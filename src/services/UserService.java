package services;

import models.User;
import utils.FileHandler;
import java.util.List;

public class UserService {

    /**
     * Updates an existing user's information.
     */
    public static boolean updateUser(User updatedUser) {
        List<User> users = FileHandler.loadAllUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(updatedUser.getId())) {
                users.set(i, updatedUser);
                FileHandler.saveAllUsers(users);
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes a user by ID.
     */
    public static boolean deleteUser(String id) {
        List<User> users = FileHandler.loadAllUsers();
        boolean removed = users.removeIf(u -> u.getId().equals(id));
        if (removed) {
            FileHandler.saveAllUsers(users);
        }
        return removed;
    }
}
