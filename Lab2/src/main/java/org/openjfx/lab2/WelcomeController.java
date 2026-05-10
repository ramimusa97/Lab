package org.openjfx.lab2;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WelcomeController {

    @FXML private Label welcomeLabel;

    // Sets the welcome message with the logged-in username
    public void setUsername(String username) {
        welcomeLabel.setText("Welcome, " + username + "!");
    }
}
