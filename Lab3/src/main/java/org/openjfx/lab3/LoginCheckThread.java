package org.openjfx.lab3;

import java.util.concurrent.locks.Lock;

// Thread B: given correct credentials, checks whether the user is currently locked.
// If the lockout period has expired, resets the user's status automatically.
public class LoginCheckThread extends Thread {

    private final String email;
    private final AuthState authState;
    private boolean allowed = false;
    private long secondsRemaining = 0;

    public LoginCheckThread(String email, AuthState authState) {
        this.email = email;
        this.authState = authState;
        setName("LoginCheckThread-" + email);
    }

    @Override
    public void run() {
        UserStatus status = authState.getOrCreate(email);
        Lock lock = status.getLock();
        lock.lock();
        try {
            Long lockedAt = status.getLockedAt();
            if (lockedAt == null) {
                allowed = true;
            } else {
                long elapsedSeconds = (System.currentTimeMillis() - lockedAt) / 1000;
                if (elapsedSeconds >= authState.getLockoutSeconds()) {
                    // Lockout period expired — reset and allow
                    status.reset();
                    allowed = true;
                } else {
                    secondsRemaining = authState.getLockoutSeconds() - elapsedSeconds;
                    allowed = false;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean isAllowed() { return allowed; }
    public long getSecondsRemaining() { return secondsRemaining; }
}
