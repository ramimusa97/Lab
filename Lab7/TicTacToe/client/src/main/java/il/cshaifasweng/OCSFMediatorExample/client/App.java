package il.cshaifasweng.OCSFMediatorExample.client;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX application class. Shows the connection screen first; once the player
 * connects, the {@link ConnectController} swaps in the game board scene via
 * {@link #setRoot(String)}.
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 420, 520);
        stage.setTitle("Tic-Tac-Toe");
        stage.setScene(scene);
        stage.show();
    }

    /** Replaces the current scene's root with another FXML view. */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    @Override
    public void stop() throws Exception {
        SimpleClient client = SimpleClient.getClient();
        if (client.isConnected()) {
            client.closeConnection();
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}
