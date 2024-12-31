package ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class TransactionUtils {

    public static void recordTransaction(int userId, double amount, String description) {
        if (hasOverdueLoan(userId)) {
            System.out.println("Transaction denied: User has overdue loans.");
            return;
        }

        LocalDate date = LocalDate.now();

        try (Connection connection = ledgerDB.getConnection()) {
            connection.setAutoCommit(false); // Start transaction

            // Insert the transaction into the transactions table
            try (PreparedStatement transactionStmt = connection.prepareStatement(
                    "INSERT INTO transactions (user_id, amount, description, date) VALUES (?, ?, ?, ?)")) {
                transactionStmt.setInt(1, userId);
                transactionStmt.setDouble(2, amount);
                transactionStmt.setString(3, description);
                transactionStmt.setDate(4, java.sql.Date.valueOf(date));
                transactionStmt.executeUpdate();
            }

            // Update the accounts table based on the transaction type
            String updateQuery = "";
            if (description.equalsIgnoreCase("Debit")) {
                updateQuery = "UPDATE accounts SET balance = balance + ? WHERE user_id = ?";
            } else if (description.equalsIgnoreCase("Credit")) {
                updateQuery = "UPDATE accounts SET balance = balance - ? WHERE user_id = ?";
            } else if (description.equalsIgnoreCase("Loan")) {
                updateQuery = "UPDATE accounts SET balance = balance + ?, loan = loan + ? WHERE user_id = ?";
            } else if (description.equalsIgnoreCase("Savings")) {
                updateQuery = "UPDATE accounts SET savings = savings + ? WHERE user_id = ?";
            } else {
                updateQuery = "UPDATE accounts SET balance = balance + ? WHERE user_id = ?";
            }

            try (PreparedStatement accountStmt = connection.prepareStatement(updateQuery)) {
                if (description.equalsIgnoreCase("Loan")) {
                    accountStmt.setDouble(1, amount);
                    accountStmt.setDouble(2, amount);
                    accountStmt.setInt(3, userId);
                } else {
                    accountStmt.setDouble(1, amount);
                    accountStmt.setInt(2, userId);
                }
                accountStmt.executeUpdate();
            }

            connection.commit(); // Commit transaction
            System.out.println("Transaction recorded successfully.");
        } catch (SQLException e) {
            System.err.println("Transaction failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean hasOverdueLoan(int userId) {
        try (Connection connection = ledgerDB.getConnection()) {
            String query = "SELECT COUNT(*) AS overdue_count FROM loans WHERE user_id = ? AND due_date < CURDATE() AND total_repayment > 0";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("overdue_count") > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}