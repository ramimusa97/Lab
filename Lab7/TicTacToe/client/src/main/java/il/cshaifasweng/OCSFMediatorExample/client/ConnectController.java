package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller for the connection screen. The player enters the server host and
 * port, then connects. On success the client sends "join" (registering for
 * matchmaking) and the board view is shown.
 */
public class ConnectController {

    @FXML
    private TextField hostField;

    @FXML
    private TextField portField;

    @FXML
    private Label statusLabel;

    @FXML
    void initialize() {
        hostField.setText("localhost");
        portField.setText("3000");
        statusLabel.setText("");
    }

    @FXML
    void connect(ActionEvent event) {
        String host = hostField.getText().trim();
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException e) {
            statusLabel.setText("Port must be a number.");
            return;
        }

        try {
            SimpleClient client = SimpleClient.getClient(host, port);
            client.setHost(host);
            client.setPort(port);
            if (!client.isConnected()) {
                client.openConnection();
            }
            client.sendToServer("join");
            App.setRoot("game");
        } catch (IOException e) {
            statusLabel.setText("Could not connect: " + e.getMessage());
        }
    }
}
