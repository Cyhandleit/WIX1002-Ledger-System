package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DebitController {

    @FXML
    private TextField DebitAmount;
    @FXML
    private TextField DescTextField;

    private int userId; // Store the user ID of the logged-in user

    // Method to set the user ID (called from the LoginController or RegisterController)
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @FXML
    private void submit(ActionEvent event) {
        try {
            double debit = Double.parseDouble(DebitAmount.getText());
            String desc = DescTextField.getText();

            // Insert the transaction into the database
            try (Connection connection = ledgerDB.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(
                         "INSERT INTO transactions (user_id, amount, description) VALUES (?, ?, ?)")) {

                preparedStatement.setInt(1, userId); // Link the transaction to the logged-in user
                preparedStatement.setDouble(2, debit);
                preparedStatement.setString(3, desc);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Transaction saved successfully!");
                } else {
                    System.out.println("Failed to save transaction!");
                }

            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}