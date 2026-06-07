package il.cshaifasweng.OCSFMediatorExample.entities;

import java.io.Serializable;

/**
 * A move made by a player, sent from the client to the server.
 * Identifies the cell (row, col) the player wants to mark.
 */
public class Move implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int row;
    private final int col;

    public Move(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        return "Move{row=" + row + ", col=" + col + "}";
    }
}
