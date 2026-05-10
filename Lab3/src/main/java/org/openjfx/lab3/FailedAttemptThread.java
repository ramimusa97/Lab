package org.openjfx.lab3;

import java.util.concurrent.locks.Lock;

// Thread A: increments the failed attempt count for the given email.
// If the count reaches maxAttempts, locks the user.
public class FailedAttemptThread extends Thread {

    private final String email;
    private final AuthState authState;
    private boolean justLocked = false;

    public FailedAttemptThread(String email, AuthState authState) {
        this.email = email;
        this.authState = authState;
    }

    @Override
    public void run() {
        UserStatus status = authState.getOrCreate(email);
        Lock lock = status.getLock();
        lock.lock();
        try {
            if (status.getLockedAt() != null) return; // already locked — ignore
            status.incrementFailed();
            if (status.getFailedAttempts() >= authState.getMaxAttempts()) {
                status.setLockedAt(System.currentTimeMillis());
                justLocked = true;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean isJustLocked() { return justLocked; }
}
