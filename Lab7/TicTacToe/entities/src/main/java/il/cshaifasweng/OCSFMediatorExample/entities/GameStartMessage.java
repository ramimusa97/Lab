package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

/**
 * Sent by the server to each player when a game starts.
 * Tells the player which symbol it was assigned ('X' or 'O')
 * and which symbol plays first (the starter). The client knows it
 * is its turn when {@code symbol == starter}.
 */
public class GameStartMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private final char symbol;
    private final char starter;

    public GameStartMessage(char symbol, char starter) {
        this.symbol = symbol;
        this.starter = starter;
    }

    public char getSymbol() {
        return symbol;
    }

    public char getStarter() {
        return starter;
    }

    @Override
    public String toString() {
        return "GameStartMessage{symbol=" + symbol + ", starter=" + starter + "}";
    }
}
