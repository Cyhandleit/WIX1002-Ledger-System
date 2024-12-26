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
    private void registerUser(ActionEvent event) {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        try (Connection connection = ledgerDB.getConnection()) {
            // Check if the username or email already exists
            String checkQuery = "SELECT user_id FROM users WHERE username = ? OR email = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, username);
                checkStmt.setString(2, email);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        statusLabel.setText("Username or email already exists.");
                        return;
                    }
                }
            }

            // Insert the new user into the users table
            String insertQuery = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, email);
                insertStmt.setString(3, password);
                insertStmt.executeUpdate();

                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        // Use the userId for further processing, e.g., initializing the account
                        initializeAccount(userId);
                    }
                }
            }

            statusLabel.setText("Registration successful!");
            switchToCoverPage(event); // Automatically switch to cover page after successful registration

        } catch (SQLException | IOException e) {
            statusLabel.setText("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeAccount(int userId) {
        try (Connection connection = ledgerDB.getConnection()) {
            String accountQuery = "INSERT INTO accounts (user_id, balance, loan) VALUES (?, 0.00, 0.00)";
            try (PreparedStatement accountStmt = connection.prepareStatement(accountQuery)) {
                accountStmt.setInt(1, userId);
                accountStmt.executeUpdate();
            }

            String savingsQuery = "INSERT INTO savings (user_id, amount, interest_rate) VALUES (?, 0.00, 0.00)";
            try (PreparedStatement savingsStmt = connection.prepareStatement(savingsQuery)) {
                savingsStmt.setInt(1, userId);
                savingsStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Failed to initialize account: " + e.getMessage());
            e.printStackTrace();
        }
    }
}