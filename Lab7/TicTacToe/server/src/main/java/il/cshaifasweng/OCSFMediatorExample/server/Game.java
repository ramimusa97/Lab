package il.cshaifasweng.OCSFMediatorExample.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import il.cshaifasweng.OCSFMediatorExample.entities.GameOverMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.GameStartMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Move;
import il.cshaifasweng.OCSFMediatorExample.entities.MoveMessage;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;

/**
 * Represents a single Tic-Tac-Toe match between two players.
 * The server is authoritative: it owns the board, validates every move,
 * decides whose turn it is, detects a win or a draw, and pushes the result
 * to both clients. Each client merely renders what the server reports.
 */
public class Game {

    private final ConnectionToClient xPlayer;
    private final ConnectionToClient oPlayer;
    private final char[][] board = new char[3][3];
    private char currentTurn;     // 'X' or 'O'
    private boolean over = false;

    /**
     * Creates a match between the two given connections and randomly:
     *  - assigns the X/O symbols to the two players, and
     *  - chooses which symbol moves first.
     */
    public Game(ConnectionToClient first, ConnectionToClient second) {
        Random random = new Random();
        boolean firstIsX = random.nextBoolean();
        this.xPlayer = firstIsX ? first : second;
        this.oPlayer = firstIsX ? second : first;
        this.currentTurn = random.nextBoolean() ? 'X' : 'O';

        for (char[] row : board) {
            Arrays.fill(row, ' ');
        }

        // Tag each connection with its game and symbol so the server can
        // route later moves and disconnects to the right match.
        xPlayer.setInfo("game", this);
        xPlayer.setInfo("symbol", 'X');
        oPlayer.setInfo("game", this);
        oPlayer.setInfo("symbol", 'O');
    }

    /** Notifies both players that the game has started and who plays first. */
    public void start() throws IOException {
        xPlayer.sendToClient(new GameStartMessage('X', currentTurn));
        oPlayer.sendToClient(new GameStartMessage('O', currentTurn));
    }

    /**
     * Processes a move sent by {@code client}. Invalid moves (wrong turn,
     * occupied or out-of-range cell, or a finished game) are silently ignored.
     * On a valid move both players are told what happened and, if the game
     * ended, the final result.
     */
    public synchronized void handleMove(ConnectionToClient client, Move move) throws IOException {
        if (over) {
            return;
        }
        char symbol = (client == xPlayer) ? 'X' : 'O';
        if (symbol != currentTurn) {
            return; // not this player's turn
        }
        int row = move.getRow();
        int col = move.getCol();
        if (row < 0 || row > 2 || col < 0 || col > 2 || board[row][col] != ' ') {
            return; // illegal cell
        }

        board[row][col] = symbol;
        boolean win = isWinningMove(symbol);
        boolean draw = !win && isBoardFull();
        char nextTurn = (symbol == 'X') ? 'O' : 'X';

        // Broadcast the move to both players (server is the single source of truth).
        MoveMessage moveMessage = new MoveMessage(row, col, symbol, (win || draw) ? symbol : nextTurn);
        xPlayer.sendToClient(moveMessage);
        oPlayer.sendToClient(moveMessage);

        if (win) {
            over = true;
            GameOverMessage result = new GameOverMessage(false, symbol);
            xPlayer.sendToClient(result);
            oPlayer.sendToClient(result);
        } else if (draw) {
            over = true;
            GameOverMessage result = new GameOverMessage(true, ' ');
            xPlayer.sendToClient(result);
            oPlayer.sendToClient(result);
        } else {
            currentTurn = nextTurn;
        }
    }

    /**
     * Called when one of the players drops the connection mid-game.
     * The remaining player is notified and the game is closed.
     */
    public synchronized void handleDisconnect(ConnectionToClient client) throws IOException {
        if (over) {
            return;
        }
        over = true;
        ConnectionToClient opponent = (client == xPlayer) ? oPlayer : xPlayer;
        if (opponent != null) {
            opponent.sendToClient("opponent disconnected");
        }
    }

    public char getStarter() {
        return currentTurn;
    }

    public boolean isOver() {
        return over;
    }

    private boolean isWinningMove(char s) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == s && board[i][1] == s && board[i][2] == s) {
                return true; // row
            }
            if (board[0][i] == s && board[1][i] == s && board[2][i] == s) {
                return true; // column
            }
        }
        if (board[0][0] == s && board[1][1] == s && board[2][2] == s) {
            return true; // main diagonal
        }
        if (board[0][2] == s && board[1][1] == s && board[2][0] == s) {
            return true; // anti-diagonal
        }
        return false;
    }

    private boolean isBoardFull() {
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == ' ') {
                    return false;
                }
            }
        }
        return true;
    }
}
