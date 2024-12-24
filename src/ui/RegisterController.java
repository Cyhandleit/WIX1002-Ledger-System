package ui;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

        // Insert user data into the database
        try (Connection connection = ledgerDB.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO users (username, email, password) VALUES (?, ?, ?)")) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User registered successfully!");
            } else {
                System.out.println("User registration failed!");
                return; // Exit the method if registration fails
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            return; // Exit the method if there's a database error
        }

        // Switch to the menu page
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuPage.fxml"));

            root = loader.load();

            MenuController menuController = loader.getController();
            menuController.displayName(username, email, password);
            System.out.println("Register Successfully!");

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load FXML file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}