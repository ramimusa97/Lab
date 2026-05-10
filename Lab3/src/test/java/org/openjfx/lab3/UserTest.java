package org.openjfx.lab3;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    // ── valid construction ────────────────────────────────────────────────────

    @Test
    void validUser_createsSuccessfully() {
        User u = new User("user@example.com", "Pass1@bc");
        assertEquals("user@example.com", u.getUsername());
        assertEquals("Pass1@bc", u.getPassword());
    }

    @Test
    void validUser_passwordExactlyMinLength() {
        // 8 chars, has letter + digit + special → valid
        assertDoesNotThrow(() -> new User("a@b.com", "Aa1!aaaa"));
    }

    @Test
    void validUser_passwordExactlyMaxLength() {
        // 12 chars, has letter + digit + special → valid
        assertDoesNotThrow(() -> new User("a@b.com", "Aa1!aaaaaaaa"));
    }

    @Test
    void validUser_allSpecialCharsAllowed() {
        // Each of !@#$%^&*() is permitted
        for (char sc : new char[]{'!','@','#','$','%','^','&','*','(',')'}) {
            String pass = "Abc1" + sc + "xxx";  // 8 chars
            assertDoesNotThrow(() -> new User("a@b.com", pass),
                    "Special char should be allowed: " + sc);
        }
    }

    // ── username validation ───────────────────────────────────────────────────

    @Test
    void username_tooLong_throws() {
        // 51-char local part → over MAX_EMAIL_LENGTH (50)
        String longEmail = "a".repeat(45) + "@b.com"; // 52 chars total
        assertThrows(IllegalArgumentException.class,
                () -> new User(longEmail, "Pass1@bc"));
    }

    @Test
    void username_exactlyMaxLength_valid() {
        // exactly 50 chars total and valid format
        String email = "a".repeat(42) + "@b.com"; // 49 chars — valid
        assertDoesNotThrow(() -> new User(email, "Pass1@bc"));
    }

    @Test
    void username_noAtSign_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("notanemail", "Pass1@bc"));
    }

    @Test
    void username_noDomain_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("user@", "Pass1@bc"));
    }

    @Test
    void username_noTld_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("user@domain", "Pass1@bc"));
    }

    @Test
    void username_tldOneLetter_throws() {
        // TLD must be 2+ letters
        assertThrows(IllegalArgumentException.class,
                () -> new User("user@domain.c", "Pass1@bc"));
    }

    @Test
    void username_tldTwoLetters_valid() {
        assertDoesNotThrow(() -> new User("user@domain.co", "Pass1@bc"));
    }

    @Test
    void username_empty_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("", "Pass1@bc"));
    }

    @Test
    void username_domainStartsWithDot_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("user@.domain.com", "Pass1@bc"));
    }

    // ── password validation ───────────────────────────────────────────────────

    @Test
    void password_tooShort_throws() {
        // 7 chars
        assertThrows(IllegalArgumentException.class,
                () -> new User("a@b.com", "Ab1!aaa"));
    }

    @Test
    void password_tooLong_throws() {
        // 13 chars
        assertThrows(IllegalArgumentException.class,
                () -> new User("a@b.com", "Ab1!aaaaaaaaa"));
    }

    @Test
    void password_noLetter_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("a@b.com", "12345678"));  // no letter, no special
    }

    @Test
    void password_noDigit_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("a@b.com", "Abcdefg!"));  // no digit
    }

    @Test
    void password_noSpecialChar_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("a@b.com", "Abcdefg1"));  // no special
    }

    @Test
    void password_illegalChar_throws() {
        // '-' is not in the allowed set
        assertThrows(IllegalArgumentException.class,
                () -> new User("a@b.com", "Pass1-bc"));
    }

    @Test
    void password_empty_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("a@b.com", ""));
    }

    // ── toString ─────────────────────────────────────────────────────────────

    @Test
    void toString_returnsUsernameThenPassword() {
        User u = new User("a@b.com", "Pass1@bc");
        assertEquals("a@b.com Pass1@bc", u.toString());
    }
}
