package ui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CoverPageController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public void switchToRegisterPage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("RegisterPage.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchToCoverPage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("CoverPage.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchToLoginPage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("LoginPage.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
}