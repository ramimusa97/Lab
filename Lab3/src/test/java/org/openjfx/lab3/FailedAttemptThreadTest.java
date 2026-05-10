package org.openjfx.lab3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FailedAttemptThreadTest {

    private static final String EMAIL = "user@example.com";
    private AuthState authState;

    @BeforeEach
    void setUp() {
        authState = new AuthState(3, 30, new ArrayList<>());
    }

    private FailedAttemptThread runThread() throws InterruptedException {
        FailedAttemptThread t = new FailedAttemptThread(EMAIL, authState);
        t.start();
        t.join();
        return t;
    }

    // ── locking behaviour ─────────────────────────────────────────────────────

    @Test
    void firstFailure_doesNotLock() throws InterruptedException {
        FailedAttemptThread t = runThread();
        assertFalse(t.isJustLocked());
        assertNull(authState.getOrCreate(EMAIL).getLockedAt());
    }

    @Test
    void nMinusOneFailures_doesNotLock() throws InterruptedException {
        // n=3: two failures should not lock
        runThread();
        FailedAttemptThread t = runThread();
        assertFalse(t.isJustLocked());
        assertNull(authState.getOrCreate(EMAIL).getLockedAt());
    }

    @Test
    void nthFailure_locks() throws InterruptedException {
        runThread(); // 1
        runThread(); // 2
        FailedAttemptThread t = runThread(); // 3 → lock
        assertTrue(t.isJustLocked());
        assertNotNull(authState.getOrCreate(EMAIL).getLockedAt());
    }

    @Test
    void beyondNthFailure_alreadyLocked_isJustLockedFalse() throws InterruptedException {
        runThread(); runThread(); runThread(); // lock on 3rd
        FailedAttemptThread t = runThread(); // 4th
        // Already locked — isJustLocked should be false (locked before this call)
        assertFalse(t.isJustLocked());
    }

    @Test
    void failedAttemptCount_incrementsEachRun() throws InterruptedException {
        runThread();
        runThread();
        assertEquals(2, authState.getOrCreate(EMAIL).getFailedAttempts());
    }

    // ── different emails are independent ──────────────────────────────────────

    @Test
    void differentEmails_independentCounts() throws InterruptedException {
        String other = "other@example.com";
        FailedAttemptThread t1 = new FailedAttemptThread(EMAIL, authState);
        FailedAttemptThread t2 = new FailedAttemptThread(other, authState);
        t1.start(); t1.join();
        t2.start(); t2.join();

        assertEquals(1, authState.getOrCreate(EMAIL).getFailedAttempts());
        assertEquals(1, authState.getOrCreate(other).getFailedAttempts());
    }

    @Test
    void lockingOneEmail_doesNotLockAnother() throws InterruptedException {
        // Lock EMAIL via 3 failed attempts (n=3)
        runThread(); runThread(); runThread();

        // other email is completely untouched
        assertNull(authState.getOrCreate("other@example.com").getLockedAt());
    }

    // ── concurrency ───────────────────────────────────────────────────────────

    @Test
    void concurrentFailures_countIsExact() throws InterruptedException {
        // Use n=10 so none of the 5 threads hit the lockout threshold.
        // This purely tests that concurrent increments don't lose updates.
        AuthState highN = new AuthState(10, 30, new ArrayList<>());
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++)
            threads[i] = new FailedAttemptThread(EMAIL, highN);
        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        assertEquals(5, highN.getOrCreate(EMAIL).getFailedAttempts());
    }

    @Test
    void exactlyOneThread_setsLockOnNthFailure_underConcurrency() throws InterruptedException {
        // Pre-load 2 failures (n-1), then fire 3 concurrent threads:
        // exactly 1 should set isJustLocked = true (the one that crossed n).
        runThread(); runThread(); // count = 2

        FailedAttemptThread[] threads = {
                new FailedAttemptThread(EMAIL, authState),
                new FailedAttemptThread(EMAIL, authState),
                new FailedAttemptThread(EMAIL, authState)
        };
        for (FailedAttemptThread t : threads) t.start();
        for (FailedAttemptThread t : threads) t.join();

        long lockedCount = 0;
        for (FailedAttemptThread t : threads) if (t.isJustLocked()) lockedCount++;
        assertEquals(1, lockedCount, "Exactly one thread should have set the lock");
    }
}
