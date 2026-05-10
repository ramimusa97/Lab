package org.openjfx.lab3;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

// Shared authentication state passed between controllers and threads.
public class AuthState {

    private final int maxAttempts;
    private final int lockoutSeconds;
    private final ArrayList<User> users;
    private final ConcurrentHashMap<String, UserStatus> statusMap = new ConcurrentHashMap<>();

    public AuthState(int maxAttempts, int lockoutSeconds, ArrayList<User> users) {
        this.maxAttempts = maxAttempts;
        this.lockoutSeconds = lockoutSeconds;
        this.users = users;
    }

    public UserStatus getOrCreate(String email) {
        return statusMap.computeIfAbsent(email, k -> new UserStatus());
    }

    public int getMaxAttempts() { return maxAttempts; }
    public int getLockoutSeconds() { return lockoutSeconds; }
    public ArrayList<User> getUsers() { return users; }
}
