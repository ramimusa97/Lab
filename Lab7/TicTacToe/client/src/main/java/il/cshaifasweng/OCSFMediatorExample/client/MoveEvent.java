package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.MoveMessage;

/** EventBus event: posted when the server broadcasts a move (by either player). */
public class MoveEvent {
    private final MoveMessage message;

    public MoveEvent(MoveMessage message) {
        this.message = message;
    }

    public MoveMessage getMessage() {
        return message;
    }
}
