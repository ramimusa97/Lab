package org.openjfx.lab2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load valid users from users.txt before showing the UI
        ArrayList<User> users = UserLoader.loadUsers("users.txt");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(loader.load());

        // Pass users list and stage reference to the login controller
        LoginController controller = loader.getController();
        controller.init(users, stage);

        stage.setTitle("Users Login");
        stage.setScene(scene);
        stage.show();

        // Clicking X terminates the program
        stage.setOnCloseRequest(e -> System.exit(0));
    }

    public static void main(String[] args) {
        launch();
    }
}
