public class User {

    private String username;
    private String password;

    private static final int MAX_EMAIL_LENGTH = 50;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 12;

    // Email format: part1@part2.part3 — part1: letters/digits/._-+%, part2: starts with letter/digit allows .-,  part3: 2+ letters
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9._\\-+%]+@[a-zA-Z0-9][a-zA-Z0-9.\\-]*\\.[a-zA-Z]{2,}$";

    // Allowed password special chars: ! @ # $ % ^ & * ( )
    private static final String PASSWORD_VALID_CHARS_REGEX =
            "^[a-zA-Z0-9!@#$%^&*()]+$";

    // Constructor validates both fields and throws IllegalArgumentException with an appropriate message if invalid
    public User(String username, String password) {
        validateUsername(username);
        validatePassword(password);
        this.username = username;
        this.password = password;
    }

    // Checks length first then format, to give the most specific error message
    private static void validateUsername(String username) {
        if (username.length() > MAX_EMAIL_LENGTH) {
            throw new IllegalArgumentException("Username is too long, try something shorter");
        }
        if (!username.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Please enter a valid Email as username");
        }
    }

    // Checks length, then allowed chars, then that at least one letter, digit, and special char are present
    private static void validatePassword(String password) {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Your password is too short, add more characters");
        }
        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Your password is too long, try a shorter one");
        }
        if (!password.matches(PASSWORD_VALID_CHARS_REGEX)) {
            throw new IllegalArgumentException("Please enter a valid password");
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        if (!hasLetter || !hasDigit || !hasSpecial) {
            throw new IllegalArgumentException("Please enter a valid password");
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return username + " " + password;
    }
}
