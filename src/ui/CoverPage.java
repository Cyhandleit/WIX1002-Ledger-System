package ui;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class CoverPage extends Application {

 public static void main(String[] args) {
        launch(args);
    }

@Override
public void start(Stage stage) throws Exception {
    
    try {
    Parent root = FXMLLoader.load(getClass().getResource("CoverPage.fxml"));
    Scene scene = new Scene(root);

    stage.setScene(scene);
    stage.show();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}