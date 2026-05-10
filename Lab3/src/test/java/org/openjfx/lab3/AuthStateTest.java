package org.openjfx.lab3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AuthStateTest {

    private AuthState authState;

    @BeforeEach
    void setUp() {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("user@example.com", "Pass1@bc"));
        authState = new AuthState(3, 30, users);
    }

    @Test
    void getMaxAttempts_returnsCorrectValue() {
        assertEquals(3, authState.getMaxAttempts());
    }

    @Test
    void getLockoutSeconds_returnsCorrectValue() {
        assertEquals(30, authState.getLockoutSeconds());
    }

    @Test
    void getUsers_returnsLoadedList() {
        assertEquals(1, authState.getUsers().size());
        assertEquals("user@example.com", authState.getUsers().get(0).getUsername());
    }

    @Test
    void getOrCreate_createsNewStatusForUnknownEmail() {
        UserStatus status = authState.getOrCreate("new@example.com");
        assertNotNull(status);
        assertEquals(0, status.getFailedAttempts());
    }

    @Test
    void getOrCreate_returnsSameInstanceForSameEmail() {
        UserStatus first = authState.getOrCreate("user@example.com");
        UserStatus second = authState.getOrCreate("user@example.com");
        assertSame(first, second);
    }

    @Test
    void getOrCreate_differentEmails_returnDifferentInstances() {
        UserStatus a = authState.getOrCreate("a@example.com");
        UserStatus b = authState.getOrCreate("b@example.com");
        assertNotSame(a, b);
    }
}
