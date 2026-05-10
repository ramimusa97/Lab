package org.openjfx.lab2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class UserLoader {

    // Reads users.txt and returns an ArrayList containing only valid users
    public static ArrayList<User> loadUsers(String filename) {
        ArrayList<User> users = new ArrayList<>();
        try {
            Scanner reader = new Scanner(new File(filename));
            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\s+");
                if (parts.length < 2) continue;
                try {
                    users.add(new User(parts[0], parts[1]));
                } catch (IllegalArgumentException e) {
                    // Skip invalid entries silently
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Warning: users.txt not found — " + e.getMessage());
        }
        return users;
    }
}
