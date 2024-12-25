package ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

public class LoanRepaymentsController {

    @FXML
    private BarChart<String, Number> loanRepaymentsChart;

    private int userId; // Store the user ID of the logged-in user

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void loadLoanRepayments() {
        try (Connection connection = ledgerDB.getConnection()) {
            String query = "SELECT date, SUM(amount) AS total_repayment FROM transactions WHERE user_id = ? AND description LIKE '%loan%' GROUP BY date ORDER BY date";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName("Loan Repayments");
                    while (rs.next()) {
                        String date = rs.getString("date");
                        double totalRepayment = rs.getDouble("total_repayment");
                        series.getData().add(new XYChart.Data<>(date, totalRepayment));
                    }
                    loanRepaymentsChart.getData().add(series);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}