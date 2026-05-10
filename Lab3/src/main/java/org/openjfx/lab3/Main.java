package org.openjfx.lab3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        ArrayList<User> users = UserLoader.loadUsers("users.txt");

        // Show the parameter screen first to collect n and t
        FXMLLoader loader = new FXMLLoader(getClass().getResource("parameter.fxml"));
        Scene scene = new Scene(loader.load());

        ParameterController controller = loader.getController();
        controller.init(stage, users);

        stage.setTitle("Login Setup");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(e -> System.exit(0));
    }

    public static void main(String[] args) {
        launch();
    }
}
