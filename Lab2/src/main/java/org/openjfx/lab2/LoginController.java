package org.openjfx.lab2;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private ArrayList<User> users;
    private Stage stage;

    // Called from Main to inject the users list and stage reference
    public void init(ArrayList<User> users, Stage stage) {
        this.users = users;
        this.stage = stage;
    }

    // Handles login button click — checks credentials and either switches screen or shows error
    @FXML
    private void onLoginButtonClick() throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                loadWelcomeScreen(username);
                return;
            }
        }

        // Show inline error — no pop-up
        errorLabel.setText("user or password do not match");
        errorLabel.setVisible(true);
    }

    // Loads welcome.fxml and replaces the current scene on the same stage
    private void loadWelcomeScreen(String username) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("welcome.fxml"));
        Scene scene = new Scene(loader.load());

        WelcomeController controller = loader.getController();
        controller.setUsername(username);

        stage.setScene(scene);
        stage.setTitle("Welcome");
    }
}
