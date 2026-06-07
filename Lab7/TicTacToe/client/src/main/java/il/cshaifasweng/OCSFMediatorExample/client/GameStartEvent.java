package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.GameStartMessage;

/** EventBus event: posted when the server reports the game has started. */
public class GameStartEvent {
    private final GameStartMessage message;

    public GameStartEvent(GameStartMessage message) {
        this.message = message;
    }

    public GameStartMessage getMessage() {
        return message;
    }
}
