package org.openjfx.lab3;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UserLoaderTest {

    @TempDir
    Path tempDir;

    private ArrayList<User> load(String content) throws IOException {
        Path file = tempDir.resolve("users.txt");
        Files.writeString(file, content);
        return UserLoader.loadUsers(file.toAbsolutePath().toString());
    }

    // ── happy path ────────────────────────────────────────────────────────────

    @Test
    void loadsAllValidUsers() throws IOException {
        ArrayList<User> users = load(
                "user1@example.com Pass1@bcd\n" +
                "user2@example.com Pass2@bcd\n"
        );
        assertEquals(2, users.size());
        assertEquals("user1@example.com", users.get(0).getUsername());
        assertEquals("user2@example.com", users.get(1).getUsername());
    }

    @Test
    void skipsInvalidUsers_andKeepsValid() throws IOException {
        ArrayList<User> users = load(
                "user@example.com Pass1@bc\n" +
                "notanemail        Pass1@bc\n" +  // invalid username
                "user2@example.com short\n"       // password too short
        );
        assertEquals(1, users.size());
        assertEquals("user@example.com", users.get(0).getUsername());
    }

    // ── edge cases ────────────────────────────────────────────────────────────

    @Test
    void emptyFile_returnsEmptyList() throws IOException {
        assertEquals(0, load("").size());
    }

    @Test
    void blankLines_areSkipped() throws IOException {
        ArrayList<User> users = load(
                "\n\n" +
                "user@example.com Pass1@bc\n" +
                "\n"
        );
        assertEquals(1, users.size());
    }

    @Test
    void lineWithOneToken_isSkipped() throws IOException {
        ArrayList<User> users = load("user@example.com\n");
        assertEquals(0, users.size());
    }

    @Test
    void extraWhitespace_isTolerated() throws IOException {
        // Multiple spaces between fields
        ArrayList<User> users = load("user@example.com    Pass1@bc\n");
        assertEquals(1, users.size());
    }

    @Test
    void fileNotFound_returnsEmptyList() {
        // Should not throw — just warn and return empty list
        ArrayList<User> users = UserLoader.loadUsers("/nonexistent/path/users.txt");
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    void allInvalidEntries_returnsEmptyList() throws IOException {
        ArrayList<User> users = load(
                "bad bad\n" +
                "alsoBad\n" +
                "onlyone\n"
        );
        assertEquals(0, users.size());
    }

    @Test
    void extraTokensOnLine_firstTwoUsed() throws IOException {
        // Third token is ignored; if first two are valid a User is created
        ArrayList<User> users = load("user@example.com Pass1@bc extraIgnored\n");
        assertEquals(1, users.size());
    }
}
