package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.GameOverMessage;

/** EventBus event: posted when the server reports the game is over (win/draw). */
public class GameOverEvent {
    private final GameOverMessage message;

    public GameOverEvent(GameOverMessage message) {
        this.message = message;
    }

    public GameOverMessage getMessage() {
        return message;
    }
}
