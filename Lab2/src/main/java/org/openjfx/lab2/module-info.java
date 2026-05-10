module org.openjfx.lab2 {
    requires javafx.controls;
    requires javafx.fxml;

    // Open package to JavaFX so FXML can access controllers via reflection
    opens org.openjfx.lab2 to javafx.fxml;
    exports org.openjfx.lab2;
}
