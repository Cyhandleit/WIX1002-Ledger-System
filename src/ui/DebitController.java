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
    // Method to set the user ID (called from the MenuController)
    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Method to set the SavingsController (called from the MenuController)
    public void setSavingsController(SavingsController savingsController) {
    }

    @FXML
    private void submit(ActionEvent event) {
        try {
            double debit = Double.parseDouble(DebitAmount.getText());
            String desc = DescTextField.getText();

            // Insert the transaction into the database
            try (Connection connection = ledgerDB.getConnection()) {
                connection.setAutoCommit(false); // Start transaction

                // Insert the transaction into the transactions table
                try (PreparedStatement transactionStmt = connection.prepareStatement(
                        "INSERT INTO transactions (user_id, amount, description) VALUES (?, ?, ?)")) {
                    transactionStmt.setInt(1, userId);
                    transactionStmt.setDouble(2, debit); // Record as positive amount
                    transactionStmt.setString(3, desc);
                    transactionStmt.executeUpdate();
                }

                connection.commit(); // Commit transaction

                System.out.println("Transaction saved successfully!");

            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (NumberFormatException e) {
            System.err.println("Invalid debit amount: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}