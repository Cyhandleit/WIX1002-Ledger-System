package ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class TransactionUtils {

    public static void recordTransaction(int userId, double amount, String description) {
        if (hasOverdueLoan(userId)) {
            System.out.println("Transaction denied: User has overdue loans.");
            return;
        }

        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        try (Connection connection = ledgerDB.getConnection()) {
            connection.setAutoCommit(false);

            // Insert transaction
            try (PreparedStatement transactionStmt = connection.prepareStatement(
                    "INSERT INTO transactions (user_id, amount, description, date, time) VALUES (?, ?, ?, ?, ?)")) {
                transactionStmt.setInt(1, userId);
                transactionStmt.setDouble(2, amount);
                transactionStmt.setString(3, description);
                transactionStmt.setDate(4, java.sql.Date.valueOf(date));
                transactionStmt.setTime(5, java.sql.Time.valueOf(time));
                transactionStmt.executeUpdate();
            }

            // Update account balance
            String updateQuery = "UPDATE accounts SET balance = balance + ? WHERE user_id = ?";
            try (PreparedStatement accountStmt = connection.prepareStatement(updateQuery)) {
                accountStmt.setDouble(1, amount);
                accountStmt.setInt(2, userId);
                accountStmt.executeUpdate();
            }

            connection.commit();
            System.out.println("Transaction recorded successfully.");
        } catch (SQLException e) {
            System.err.println("Transaction failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void recordDebitTransaction(int userId, double amount, String description) {
        recordTransaction(userId, amount, description);
    }

    public static void recordCreditTransaction(int userId, double amount, String description) {
        recordTransaction(userId, -amount, description);
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