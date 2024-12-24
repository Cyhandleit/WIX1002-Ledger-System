package ui;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CreditController {

    @FXML
    private TextField CreditAmount;
    @FXML
    private TextField DescTextField;

    private int userId; // Store the user ID of the logged-in user

    // Method to set the user ID (called from the MenuController)
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @FXML
    private void submit(ActionEvent event) {
        Connection connection = null;
        try {
            double credit = Double.parseDouble(CreditAmount.getText());
            String desc = DescTextField.getText();

            // Insert the transaction into the database
            connection = ledgerDB.getConnection();
            connection.setAutoCommit(false); // Start transaction

            // Insert the transaction into the transactions table
            try (PreparedStatement transactionStmt = connection.prepareStatement(
                    "INSERT INTO transactions (user_id, amount, description) VALUES (?, ?, ?)")) {
                transactionStmt.setInt(1, userId);
                transactionStmt.setDouble(2, -credit); // Subtract the credit amount
                transactionStmt.setString(3, desc);
                transactionStmt.executeUpdate();
            }

            connection.commit(); // Commit transaction
            System.out.println("Transaction successfully recorded.");
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback(); // Rollback transaction on error
                } catch (SQLException rollbackEx) {
                    System.out.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            System.out.println("Transaction failed: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid credit amount: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException closeEx) {
                    System.out.println("Failed to close connection: " + closeEx.getMessage());
                }
            }
        }
    }
}