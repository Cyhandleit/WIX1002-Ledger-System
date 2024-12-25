package ui;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    @FXML
    public void switchToCoverPage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("CoverPage.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void registerUser(ActionEvent event) {
        String username = nameTextField.getText();
        String email = emailTextField.getText();
        String password = passwordTextField.getText();

        System.out.println("Attempting to register with username: " + username + ", email: " + email);

        // Insert user data into the database
        try (Connection connection = ledgerDB.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO users (username, email, password) VALUES (?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("User registered successfully!");

                // Retrieve the generated user ID
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);

                        // Switch to the cover page for login
                        switchToCoverPage(event);
                    }
                }
            } else {
                System.out.println("User registration failed!");
            }

        } catch (SQLException | IOException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}