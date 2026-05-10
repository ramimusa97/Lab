package org.openjfx.lab3;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WelcomeController {

    @FXML private Label welcomeLabel;

    public void setUsername(String username) {
        welcomeLabel.setText("Welcome, " + username + "!");
    }
}
