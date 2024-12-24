package ui;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class Main extends Application {

public static void main(String[] args) {
        launch(args);
    }

    private Stage stage;
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

public void changeScene(String MenuPagel) throws Exception{
    Parent pane = FXMLLoader.load(getClass().getResource("MenuPage.fxml"));
    stage.getScene().setRoot(pane);
}
}