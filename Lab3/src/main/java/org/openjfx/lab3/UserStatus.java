package org.openjfx.lab3;

// Tracks failed login attempts and lockout time for a single user (email).
public class UserStatus {

    private int failedAttempts = 0;
    private Long lockedAt = null; // null means not locked; otherwise epoch millis of lock time

    public synchronized void incrementFailed() {
        failedAttempts++;
    }

    public synchronized int getFailedAttempts() {
        return failedAttempts;
    }

    public synchronized void lock() {
        lockedAt = System.currentTimeMillis();
    }

    public synchronized Long getLockedAt() {
        return lockedAt;
    }

    public synchronized void reset() {
        failedAttempts = 0;
        lockedAt = null;
    }
}
