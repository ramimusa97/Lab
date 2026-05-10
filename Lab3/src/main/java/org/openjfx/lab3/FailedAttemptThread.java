package org.openjfx.lab3;

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
        synchronized (status) {
            status.incrementFailed();
            if (status.getFailedAttempts() >= authState.getMaxAttempts()) {
                status.lock();
                justLocked = true;
            }
        }
    }

    public boolean isJustLocked() { return justLocked; }
}
