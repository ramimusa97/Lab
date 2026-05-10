package org.openjfx.lab3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.*;

class UserStatusTest {

    private UserStatus status;

    @BeforeEach
    void setUp() {
        status = new UserStatus();
    }

    // ── initial state ─────────────────────────────────────────────────────────

    @Test
    void initialFailedAttempts_isZero() {
        assertEquals(0, status.getFailedAttempts());
    }

    @Test
    void initialLockedAt_isNull() {
        assertNull(status.getLockedAt());
    }

    // ── incrementFailed ───────────────────────────────────────────────────────

    @Test
    void incrementFailed_increasesCountByOne() {
        status.incrementFailed();
        assertEquals(1, status.getFailedAttempts());
    }

    @Test
    void incrementFailed_multipleTimesAccumulates() {
        status.incrementFailed();
        status.incrementFailed();
        status.incrementFailed();
        assertEquals(3, status.getFailedAttempts());
    }

    // ── setLockedAt ───────────────────────────────────────────────────────────

    @Test
    void setLockedAt_storesTimestamp() {
        status.setLockedAt(12345L);
        assertEquals(12345L, status.getLockedAt());
    }

    // ── reset ─────────────────────────────────────────────────────────────────

    @Test
    void reset_clearsFailedAttempts() {
        status.incrementFailed();
        status.incrementFailed();
        status.reset();
        assertEquals(0, status.getFailedAttempts());
    }

    @Test
    void reset_clearsLock() {
        status.setLockedAt(999L);
        status.reset();
        assertNull(status.getLockedAt());
    }

    @Test
    void reset_onFreshStatus_isIdempotent() {
        assertDoesNotThrow(() -> status.reset());
        assertEquals(0, status.getFailedAttempts());
        assertNull(status.getLockedAt());
    }

    // ── getLock returns a usable Lock ─────────────────────────────────────────

    @Test
    void getLock_returnsNonNull() {
        assertNotNull(status.getLock());
    }

    @Test
    void getLock_canBeAcquiredAndReleased() {
        Lock lock = status.getLock();
        lock.lock();
        try {
            status.incrementFailed();
        } finally {
            lock.unlock();
        }
        assertEquals(1, status.getFailedAttempts());
    }

    // ── thread safety via Lock ────────────────────────────────────────────────

    @Test
    void incrementFailed_isSafeUnderConcurrency() throws InterruptedException {
        int threadCount = 50;
        Lock lock = status.getLock();
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < threadCount; i++) {
            pool.submit(() -> {
                lock.lock();
                try {
                    status.incrementFailed();
                } finally {
                    lock.unlock();
                }
            });
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
        assertEquals(threadCount, status.getFailedAttempts());
    }
}
