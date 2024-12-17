package ui;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    @FXML
    TextField nameTextField;
    @FXML
    TextField emailTextField;
    @FXML
    TextField passwordTextField;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchToCoverPage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("CoverPage.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    public void MenuPage(ActionEvent event) {
        String username = nameTextField.getText();
        String email = emailTextField.getText();
        String password = passwordTextField.getText();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuPage.fxml"));

            root = loader.load();

            MenuController menuController = loader.getController();
            menuController.displayName(username,email,password);

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load FXML file: " + e.getMessage());
            e.printStackTrace();
            // Optionally, show an error dialog to the user
            // Alert alert = new Alert(Alert.AlertType.ERROR);
            // alert.setTitle("Error");
            // alert.setHeaderText("Failed to load Menu Page");
            // alert.setContentText("An error occurred while loading the Menu Page. Please try again.");
            // alert.showAndWait();
        }
    }
}