package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class CreditLoanController {

    @FXML
    private TextField principalAmountField;
    @FXML
    private TextField interestRateField;
    @FXML
    private TextField repaymentPeriodField;
    @FXML
    private TextField repayAmountField;
    @FXML
    private Button applyButton;
    @FXML
    private Button repayButton;
    @FXML
    private Label statusLabel;

    private int userId; // Store the user ID of the logged-in user

    // Method to set the user ID (called from the MenuController)
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @FXML
    private void applyLoan(ActionEvent event) {
        try {
            double principal = Double.parseDouble(principalAmountField.getText());
            double interestRate = Double.parseDouble(interestRateField.getText());
            int repaymentPeriod = Integer.parseInt(repaymentPeriodField.getText());

            double totalRepayment = principal + (principal * interestRate / 100);
            double monthlyInstallment = totalRepayment / repaymentPeriod;
            LocalDate dueDate = LocalDate.now().plusMonths(repaymentPeriod); // Calculate the due date

            try (Connection connection = ledgerDB.getConnection()) {
                connection.setAutoCommit(false); // Start transaction

                // Insert the loan into the loans table
                String loanQuery = "INSERT INTO loans (user_id, principal, interest_rate, repayment_period, total_repayment, monthly_installment, due_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement loanStmt = connection.prepareStatement(loanQuery)) {
                    loanStmt.setInt(1, userId);
                    loanStmt.setDouble(2, principal);
                    loanStmt.setDouble(3, interestRate);
                    loanStmt.setInt(4, repaymentPeriod);
                    loanStmt.setDouble(5, totalRepayment);
                    loanStmt.setDouble(6, monthlyInstallment);
                    loanStmt.setDate(7, java.sql.Date.valueOf(dueDate)); // Set the due date
                    loanStmt.executeUpdate();
                }

                connection.commit(); // Commit transaction

                statusLabel.setText("Loan applied successfully!");
            } catch (SQLException e) {
                statusLabel.setText("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid input: " + e.getMessage());
        }
    }

    @FXML
    private void repayLoan(ActionEvent event) {
        try {
            double repayAmount = Double.parseDouble(repayAmountField.getText());

            try (Connection connection = ledgerDB.getConnection()) {
                connection.setAutoCommit(false); // Start transaction

                // Update the loan repayment in the loans table
                String loanQuery = "UPDATE loans SET total_repayment = total_repayment - ? WHERE user_id = ? AND total_repayment > 0";
                try (PreparedStatement loanStmt = connection.prepareStatement(loanQuery)) {
                    loanStmt.setDouble(1, repayAmount);
                    loanStmt.setInt(2, userId);
                    loanStmt.executeUpdate();
                }

                connection.commit(); // Commit transaction

                statusLabel.setText("Repayment successful!");
            } catch (SQLException e) {
                statusLabel.setText("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid input: " + e.getMessage());
        }
    }

    @FXML
    private void openLoanRepayments(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoanRepaymentsPage.fxml"));
            Parent root = loader.load();

            LoanRepaymentsController controller = loader.getController();
            controller.setUserId(userId);
            controller.loadLoanRepayments();

            Stage stage = new Stage();
            stage.setTitle("Loan Repayments");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean hasOutstandingLoan() {
        try (Connection connection = ledgerDB.getConnection()) {
            String query = "SELECT COUNT(*) AS loan_count FROM loans WHERE user_id = ? AND total_repayment > 0";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("loan_count") > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean canPerformDebitOrCredit() {
        return !hasOutstandingLoan();
    }
}
