package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

/**
 * Sent by the server to both players when the game ends.
 * If {@code draw} is true the board filled up with no winner; otherwise
 * {@code winner} holds the winning symbol ('X' or 'O'). Each client compares
 * the winner to its own symbol to decide whether it won or lost.
 */
public class GameOverMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private final boolean draw;
    private final char winner;

    public GameOverMessage(boolean draw, char winner) {
        this.draw = draw;
        this.winner = winner;
    }

    public boolean isDraw() {
        return draw;
    }

    public char getWinner() {
        return winner;
    }

    @Override
    public String toString() {
        return "GameOverMessage{draw=" + draw + ", winner=" + winner + "}";
    }
}
