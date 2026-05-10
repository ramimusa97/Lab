import java.io.*;
import java.util.*;

public class UsersApp {

    public static void main(String[] args) throws IOException {
        ArrayList<User> users = new ArrayList<>();

        File readFile = new File("users.txt");
        Scanner reader = new Scanner(readFile);

        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            // Split on whitespace to separate username and password
            String[] parts = trimmed.split("\\s+");
            if (parts.length < 2) {
                System.out.println(line + " - Invalid line format");
                continue;
            }

            String username = parts[0];
            String password = parts[1];

            // Attempt to create a User; print the error message if validation fails
            try {
                User user = new User(username, password);
                users.add(user);
            } catch (IllegalArgumentException e) {
                System.out.println(line + " - " + e.getMessage());
            }
        }

        reader.close();

        // Sort valid users alphabetically by username
        Collections.sort(users, (u1, u2) -> u1.getUsername().compareTo(u2.getUsername()));

        // Write sorted valid users to file
        PrintWriter writer = new PrintWriter(new FileWriter("Valid_users.txt"));
        for (User user : users) {
            writer.println(user);
        }
        writer.close();
    }
}
