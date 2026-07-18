package org.openjfx.lab2;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Lab 9 - Task 3: TestFX unit tests for the Lab 2 login application.
 */
@ExtendWith(ApplicationExtension.class)
public class AppTest {

    private static final String VALID_USERNAME = "B_Kernighan@mail.com";
    private static final String VALID_PASSWORD = "!a01011942";

    @Start
    private void start(Stage stage) throws IOException {
        // Same wiring as Main.start(), but without loading users from disk,
        // so the test does not depend on the working directory.
        ArrayList<User> users = new ArrayList<>();
        users.add(new User(VALID_USERNAME, VALID_PASSWORD));

        FXMLLoader loader = new FXMLLoader(Main.class.getResource("login.fxml"));
        Scene scene = new Scene(loader.load());

        LoginController controller = loader.getController();
        controller.init(users, stage);

        stage.setScene(scene);
        stage.show();
    }

    // 1) On startup, the username field is empty.
    @Test
    void test_username_field_is_empty(FxRobot robot) {
        FxAssert.verifyThat("#usernameField", TextInputControlMatchers.hasText(""));
    }

    // 2) On startup, the password field is empty.
    @Test
    void test_password_field_is_empty(FxRobot robot) {
        FxAssert.verifyThat("#passwordField", TextInputControlMatchers.hasText(""));
    }

    // 3) On startup, the login button shows the correct text.
    @Test
    void test_login_button_text(FxRobot robot) {
        FxAssert.verifyThat(".button", LabeledMatchers.hasText("login"));
    }

    // 4) On startup, the error label is hidden.
    @Test
    void test_error_label_hidden_on_startup(FxRobot robot) {
        FxAssert.verifyThat("#errorLabel", NodeMatchers.isInvisible());
    }

    // 5) Logging in with wrong credentials shows the inline error message.
    @Test
    void test_failed_login_shows_error(FxRobot robot) {
        robot.clickOn("#usernameField").write("wrong@mail.com");
        robot.clickOn("#passwordField").write("wrongPass1!");
        robot.clickOn(".button");

        FxAssert.verifyThat("#errorLabel", NodeMatchers.isVisible());
        FxAssert.verifyThat("#errorLabel",
                LabeledMatchers.hasText("user or password do not match"));
    }

    // 6) Logging in with valid credentials switches to the welcome screen
    //    and greets the user by name.
    @Test
    void test_successful_login_shows_welcome(FxRobot robot) {
        robot.clickOn("#usernameField").write(VALID_USERNAME);
        robot.clickOn("#passwordField").write(VALID_PASSWORD);
        robot.clickOn(".button");

        FxAssert.verifyThat("#welcomeLabel",
                LabeledMatchers.hasText("Welcome, " + VALID_USERNAME + "!"));
    }
}
