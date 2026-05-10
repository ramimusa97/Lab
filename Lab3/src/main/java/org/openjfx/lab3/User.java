package org.openjfx.lab3;

public class User {

    private String username;
    private String password;

    private static final int MAX_EMAIL_LENGTH = 50;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 12;

    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9._\\-+%]+@[a-zA-Z0-9][a-zA-Z0-9.\\-]*\\.[a-zA-Z]{2,}$";

    private static final String PASSWORD_VALID_CHARS_REGEX =
            "^[a-zA-Z0-9!@#$%^&*()]+$";

    public User(String username, String password) {
        validateUsername(username);
        validatePassword(password);
        this.username = username;
        this.password = password;
    }

    private static void validateUsername(String username) {
        if (username.length() > MAX_EMAIL_LENGTH)
            throw new IllegalArgumentException("Username is too long");
        if (!username.matches(EMAIL_REGEX))
            throw new IllegalArgumentException("Please enter a valid Email as username");
    }

    private static void validatePassword(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH)
            throw new IllegalArgumentException("Your password is too short");
        if (password.length() > MAX_PASSWORD_LENGTH)
            throw new IllegalArgumentException("Your password is too long");
        if (!password.matches(PASSWORD_VALID_CHARS_REGEX))
            throw new IllegalArgumentException("Please enter a valid password");
        boolean hasLetter = false, hasDigit = false, hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        if (!hasLetter || !hasDigit || !hasSpecial)
            throw new IllegalArgumentException("Please enter a valid password");
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }

    @Override
    public String toString() { return username + " " + password; }
}
