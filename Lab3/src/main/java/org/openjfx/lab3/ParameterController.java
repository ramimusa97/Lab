package org.openjfx.lab3;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

// Controller for the parameter input screen shown before login.
// Collects n (max failed attempts) and t (lockout duration in seconds).
public class ParameterController {

    @FXML private TextField nField;
    @FXML private TextField tField;
    @FXML private Label errorLabel;

    private Stage stage;
    private ArrayList<User> users;

    public void init(Stage stage, ArrayList<User> users) {
        this.stage = stage;
        this.users = users;
    }

    @FXML
    private void onStartButtonClick() throws IOException {
        int n, t;
        try {
            n = Integer.parseInt(nField.getText().trim());
            t = Integer.parseInt(tField.getText().trim());
            if (n <= 0 || t <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            errorLabel.setText("Please enter positive integers for both fields.");
            errorLabel.setVisible(true);
            return;
        }

        AuthState authState = new AuthState(n, t, users);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(loader.load());

        LoginController controller = loader.getController();
        controller.init(authState, stage);

        stage.setTitle("Users Login");
        stage.setScene(scene);
    }
}
