package org.openjfx.lab3;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Tracks failed login attempts and lockout time for a single user (email).
// All compound operations must be guarded by getLock() at the call site.
public class UserStatus {

    private final Lock lock = new ReentrantLock();

    private int failedAttempts = 0;
    private Long lockedAt = null; // null = not locked; otherwise epoch millis of lock time

    public Lock getLock() { return lock; }

    public void incrementFailed() { failedAttempts++; }
    public int getFailedAttempts() { return failedAttempts; }

    public void setLockedAt(long timestamp) { lockedAt = timestamp; }
    public Long getLockedAt() { return lockedAt; }

    public void reset() {
        failedAttempts = 0;
        lockedAt = null;
    }
}
