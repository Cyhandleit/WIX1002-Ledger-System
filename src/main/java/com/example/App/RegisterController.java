package com.example.App;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label statusLabel;
    @FXML
    private Button registerButton;

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
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
    
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }
    
        if (!isValidUsername(username)) {
            statusLabel.setText("Username must be alphanumeric and cannot contain special characters.");
            return;
        }
    
        if (!isValidEmail(email)) {
            statusLabel.setText("Invalid email format!");
            return;
        }
    
        if (!isValidPassword(password)) {
            statusLabel.setText("Password must be at least 8 characters long and contain at least one special character.");
            return;
        }
    
        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match.");
            return;
        }
    
        try (Connection connection = ledgerDB.getConnection()) {
            String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.executeUpdate();
         }
    
            // Initialize account with default values
            initializeAccount(connection, username);

            statusLabel.setText("Registration successful!");
            switchToCoverPage(event);

         } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry error code for MySQL
                statusLabel.setText("Username or email already exists.");
            } else {
                statusLabel.setText("Registration failed: " + e.getMessage());
            }
            e.printStackTrace();
        } catch (IOException e) {
            statusLabel.setText("Registration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeAccount(Connection connection, String username) throws SQLException {
        String query = "INSERT INTO accounts (user_id, balance, savings) VALUES ((SELECT user_id FROM users WHERE username = ?), 0.0, 0.0)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@(gmail\\.com|hotmail\\.com|yahoo\\.com)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        return pattern.matcher(password).matches();
    }

    private boolean isValidUsername(String username) {
        String usernameRegex = "^[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(usernameRegex);
        return pattern.matcher(username).matches();
    }
}