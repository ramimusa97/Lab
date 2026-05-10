package org.openjfx.lab3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LoginCheckThreadTest {

    private static final String EMAIL = "user@example.com";
    private AuthState authState;

    @BeforeEach
    void setUp() {
        // t=1 second so expiry tests only need a short sleep
        authState = new AuthState(3, 1, new ArrayList<>());
    }

    private LoginCheckThread runCheck() throws InterruptedException {
        LoginCheckThread t = new LoginCheckThread(EMAIL, authState);
        t.start();
        t.join();
        return t;
    }

    // ── user has no history ───────────────────────────────────────────────────

    @Test
    void noHistory_isAllowed() throws InterruptedException {
        assertTrue(runCheck().isAllowed());
    }

    @Test
    void noHistory_secondsRemaining_isZero() throws InterruptedException {
        assertEquals(0, runCheck().getSecondsRemaining());
    }

    // ── user has failures but is not locked ───────────────────────────────────

    @Test
    void failuresButNotLocked_isAllowed() throws InterruptedException {
        authState.getOrCreate(EMAIL).incrementFailed();
        authState.getOrCreate(EMAIL).incrementFailed();
        assertTrue(runCheck().isAllowed());
    }

    // ── user is locked, within lockout period ─────────────────────────────────

    @Test
    void locked_withinPeriod_isNotAllowed() throws InterruptedException {
        // t=60s so the lockout definitely hasn't expired
        AuthState longLock = new AuthState(3, 60, new ArrayList<>());
        longLock.getOrCreate(EMAIL).setLockedAt(System.currentTimeMillis());

        LoginCheckThread t = new LoginCheckThread(EMAIL, longLock);
        t.start();
        t.join();

        assertFalse(t.isAllowed());
        assertTrue(t.getSecondsRemaining() > 0 && t.getSecondsRemaining() <= 60);
    }

    // ── lockout period expires ────────────────────────────────────────────────

    @Test
    void locked_afterPeriodExpires_isAllowed() throws InterruptedException {
        authState.getOrCreate(EMAIL).setLockedAt(System.currentTimeMillis());
        Thread.sleep(1100); // wait just over t=1 second

        assertTrue(runCheck().isAllowed());
    }

    @Test
    void locked_afterExpiry_statusIsReset() throws InterruptedException {
        UserStatus status = authState.getOrCreate(EMAIL);
        status.incrementFailed();
        status.incrementFailed();
        status.incrementFailed();
        status.setLockedAt(System.currentTimeMillis());
        Thread.sleep(1100);

        runCheck();

        assertEquals(0, status.getFailedAttempts());
        assertNull(status.getLockedAt());
    }

    // ── different users don't interfere ──────────────────────────────────────

    @Test
    void otherUserLocked_doesNotBlockThisUser() throws InterruptedException {
        authState.getOrCreate("other@example.com").setLockedAt(System.currentTimeMillis());
        assertTrue(runCheck().isAllowed());
    }

    // ── second check after auto-unlock still works ────────────────────────────

    @Test
    void afterAutoUnlock_subsequentCheckStillAllowed() throws InterruptedException {
        authState.getOrCreate(EMAIL).setLockedAt(System.currentTimeMillis());
        Thread.sleep(1100);

        runCheck();                           // first check: unlocks
        assertTrue(runCheck().isAllowed()); // second check: still allowed
    }
}
