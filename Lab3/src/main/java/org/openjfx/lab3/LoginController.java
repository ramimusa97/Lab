package org.openjfx.lab3;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    private AuthState authState;
    private Stage stage;

    public void init(AuthState authState, Stage stage) {
        this.authState = authState;
        this.stage = stage;
    }

    @FXML
    private void onLoginButtonClick() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        loginButton.setDisable(true);

        boolean credentialsCorrect = authState.getUsers().stream()
                .anyMatch(u -> u.getUsername().equals(username) && u.getPassword().equals(password));

        // Run thread work off the FX thread so the UI stays responsive
        new Thread(() -> {
            if (credentialsCorrect) {
                // Thread B: check if the user is currently locked
                LoginCheckThread checkThread = new LoginCheckThread(username, authState);
                checkThread.start();
                try { checkThread.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

                Platform.runLater(() -> {
                    if (checkThread.isAllowed()) {
                        try { loadWelcomeScreen(username); }
                        catch (IOException e) { e.printStackTrace(); }
                    } else {
                        showError("Account locked. Try again in " + checkThread.getSecondsRemaining() + " second(s).");
                        loginButton.setDisable(false);
                    }
                });

            } else {
                // Thread A: increment failed count and possibly lock this email
                FailedAttemptThread failThread = new FailedAttemptThread(username, authState);
                failThread.start();
                try { failThread.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

                Platform.runLater(() -> {
                    if (failThread.isJustLocked()) {
                        startLockoutCountdown(authState.getLockoutSeconds());
                    } else {
                        showError("Username or password do not match.");
                        loginButton.setDisable(false);
                    }
                });
            }
        }).start();
    }

    // Disables the form and shows a live countdown, then re-enables after t seconds.
    private void startLockoutCountdown(int seconds) {
        usernameField.setDisable(true);
        passwordField.setDisable(true);

        final int[] remaining = {seconds};
        errorLabel.setText("Too many failed attempts. Locked for " + remaining[0] + " second(s).");
        errorLabel.setVisible(true);

        Timeline countdown = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            remaining[0]--;
            if (remaining[0] > 0) {
                errorLabel.setText("Account locked. Try again in " + remaining[0] + " second(s).");
            } else {
                // Lockout expired — reset status and re-enable form
                authState.getOrCreate(usernameField.getText().trim()).reset();
                errorLabel.setVisible(false);
                usernameField.setDisable(false);
                passwordField.setDisable(false);
                loginButton.setDisable(false);
                passwordField.clear();
            }
        }));
        countdown.setCycleCount(seconds);
        countdown.play();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void loadWelcomeScreen(String username) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("welcome.fxml"));
        Scene scene = new Scene(loader.load());

        WelcomeController controller = loader.getController();
        controller.setUsername(username);

        stage.setScene(scene);
        stage.setTitle("Welcome");
    }
}
