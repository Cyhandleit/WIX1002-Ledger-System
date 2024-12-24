package ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AccountController {

    @FXML
    private Label BalanceLabel;

    @FXML
    private Label SavingsLabel;

    @FXML
    private Label LoanLabel;

    private int userId; // Store the user ID of the logged-in user

    // Method to set the user ID (called from the MenuController)
    public void setUserId(int userId) {
        this.userId = userId;
        fetchAccountDetails(); // Fetch and display account details when user_id is set
    }

    @FXML
    public void fetchAccountDetails() {
        try (Connection connection = ledgerDB.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT balance, savings, loan FROM accounts WHERE user_id = ?")) {

            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                double savings = resultSet.getDouble("savings");
                double loan = resultSet.getDouble("loan");

                // Display the account details
                displayAccount(balance, savings, loan);
            } else {
                // If no account details are found, display default values
                displayAccount(0.00, 0.00, 0.00);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void displayAccount(double balance, double savings, double loan) {
        BalanceLabel.setText(String.format("%.2f", balance));
        SavingsLabel.setText(String.format("%.2f", savings));
        LoanLabel.setText(String.format("%.2f", loan));
    }
}