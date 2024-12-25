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
        try (Connection connection = ledgerDB.getConnection()) {
            double credit = Double.parseDouble(CreditAmount.getText());
            String desc = DescTextField.getText();

            connection.setAutoCommit(false); // Start transaction

            // Insert the transaction into the transactions table
            try (PreparedStatement transactionStmt = connection.prepareStatement(
                    "INSERT INTO transactions (user_id, amount, description) VALUES (?, ?, ?)")) {
                transactionStmt.setInt(1, userId);
                transactionStmt.setDouble(2, -credit); // Record as negative amount
                transactionStmt.setString(3, desc);
                transactionStmt.executeUpdate();
            }

            connection.commit(); // Commit transaction
            System.out.println("Transaction successfully recorded.");
        } catch (SQLException e) {
            System.out.println("Transaction failed: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid credit amount: " + e.getMessage());
        }
    }
}