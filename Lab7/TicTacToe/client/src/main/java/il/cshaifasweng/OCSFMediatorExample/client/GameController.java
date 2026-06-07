package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import il.cshaifasweng.OCSFMediatorExample.entities.GameOverMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.GameStartMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Move;
import il.cshaifasweng.OCSFMediatorExample.entities.MoveMessage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller for the 3x3 game board. It subscribes to EventBus and reacts to
 * the events posted by {@link SimpleClient}. The controller never talks to the
 * network directly except to send the local player's move; everything it
 * displays is driven by what the (authoritative) server broadcasts back.
 */
public class GameController {

    @FXML private Label statusLabel;
    @FXML private Button b00, b01, b02, b10, b11, b12, b20, b21, b22;

    private Button[][] cells;
    private char mySymbol = ' ';
    private boolean myTurn = false;
    private boolean gameOver = false;

    @FXML
    void initialize() {
        cells = new Button[][] {
                {b00, b01, b02},
                {b10, b11, b12},
                {b20, b21, b22}
        };
        clearBoard();
        setBoardEnabled(false);
        statusLabel.setText("Connecting...");
        EventBus.getDefault().register(this);
    }

    /** A board cell was clicked: send the move to the server (if it's our turn). */
    @FXML
    void cellClicked(ActionEvent event) {
        if (gameOver || !myTurn) {
            return;
        }
        Button source = (Button) event.getSource();
        if (!source.getText().isEmpty()) {
            return; // cell already taken
        }
        int[] pos = findCell(source);
        if (pos == null) {
            return;
        }
        try {
            SimpleClient.getClient().sendToServer(new Move(pos[0], pos[1]));
            // Lock the board until the server confirms and tells us the next turn.
            myTurn = false;
            setBoardEnabled(false);
            statusLabel.setText("Waiting for opponent's move...");
        } catch (IOException e) {
            statusLabel.setText("Failed to send move: " + e.getMessage());
        }
    }

    @Subscribe
    public void onGameStart(GameStartEvent event) {
        GameStartMessage message = event.getMessage();
        Platform.runLater(() -> {
            mySymbol = message.getSymbol();
            gameOver = false;
            myTurn = (mySymbol == message.getStarter());
            clearBoard();
            setBoardEnabled(myTurn);
            updateTurnStatus();
        });
    }

    @Subscribe
    public void onMove(MoveEvent event) {
        MoveMessage message = event.getMessage();
        Platform.runLater(() -> {
            cells[message.getRow()][message.getCol()].setText(String.valueOf(message.getSymbol()));
            if (!gameOver) {
                myTurn = (message.getNextTurn() == mySymbol);
                setBoardEnabled(myTurn);
                updateTurnStatus();
            }
        });
    }

    @Subscribe
    public void onGameOver(GameOverEvent event) {
        GameOverMessage message = event.getMessage();
        Platform.runLater(() -> {
            gameOver = true;
            myTurn = false;
            setBoardEnabled(false);
            if (message.isDraw()) {
                statusLabel.setText("It's a draw!");
            } else {
                statusLabel.setText(message.getWinner() == mySymbol ? "You win!" : "You lose.");
            }
        });
    }

    @Subscribe
    public void onWaiting(WaitingEvent event) {
        Platform.runLater(() -> statusLabel.setText("Waiting for another player to join..."));
    }

    @Subscribe
    public void onOpponentLeft(OpponentLeftEvent event) {
        Platform.runLater(() -> {
            gameOver = true;
            setBoardEnabled(false);
            statusLabel.setText("Opponent disconnected - you win!");
        });
    }

    private void updateTurnStatus() {
        if (gameOver) {
            return;
        }
        statusLabel.setText("You are " + mySymbol + ". " + (myTurn ? "Your turn." : "Opponent's turn."));
    }

    private void setBoardEnabled(boolean enabled) {
        for (Button[] row : cells) {
            for (Button button : row) {
                button.setDisable(!enabled);
            }
        }
    }

    private void clearBoard() {
        for (Button[] row : cells) {
            for (Button button : row) {
                button.setText("");
            }
        }
    }

    private int[] findCell(Button button) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (cells[r][c] == button) {
                    return new int[] {r, c};
                }
            }
        }
        return null;
    }
}
