package application;

import java.io.*;
import java.util.*;

public class UserDataStore {
    private static final String USER_FILE = "users.csv";
    private static Map<String, User> users = new HashMap<>();  // Map for storing users by email or username

    static {
        loadUsers();
    }

    public static void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userDetails = line.split(",");
                if (userDetails.length == 3) {
                    String username = userDetails[0];
                    String email = userDetails[1];
                    String password = userDetails[2];
                    users.put(email, new User(username, email, password));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean registerUser(String username, String email, String password) {
        if (users.containsKey(email) || users.values().stream().anyMatch(user -> user.getUsername().equals(username))) {
            return false; // User already exists
        }
        User newUser = new User(username, email, password);
        users.put(email, newUser);
        saveUsers();
        return true;
    }

    public static boolean loginUser(String usernameOrEmail, String password) {
        User user = users.get(usernameOrEmail);
        if (user == null) {
            user = users.values().stream().filter(u -> u.getUsername().equals(usernameOrEmail)).findFirst().orElse(null);
        }
        return user != null && user.getPassword().equals(password);
    }

    private static void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
            for (User user : users.values()) {
                writer.write(user.getUsername() + "," + user.getEmail() + "," + user.getPassword());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User getUser(String email) {
        return users.get(email);
    }

    public static Map<String, User> getUsers() {
        return users;
    }
}

