package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

/**
 * Sent by the server to BOTH players after a valid move has been applied.
 * Describes which cell was marked and with which symbol, plus whose turn
 * it is next. Because the server is authoritative, both clients render the
 * board purely from these broadcasts, which keeps them perfectly in sync.
 */
public class MoveMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int row;
    private final int col;
    private final char symbol;
    private final char nextTurn;

    public MoveMessage(int row, int col, char symbol, char nextTurn) {
        this.row = row;
        this.col = col;
        this.symbol = symbol;
        this.nextTurn = nextTurn;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public char getSymbol() {
        return symbol;
    }

    public char getNextTurn() {
        return nextTurn;
    }

    @Override
    public String toString() {
        return "MoveMessage{row=" + row + ", col=" + col
                + ", symbol=" + symbol + ", nextTurn=" + nextTurn + "}";
    }
}
