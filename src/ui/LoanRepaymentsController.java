package ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tooltip;

public class LoanRepaymentsController {

    @FXML
    private PieChart loanRepaymentsChart;

    @FXML
    private PieChart overdueRepaymentsChart;

    private int userId; // Store the user ID of the logged-in user

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void loadLoanRepayments() {
        try (Connection connection = ledgerDB.getConnection()) {
            String query = "SELECT total_repayment, (total_repayment - monthly_installment * repayment_period) AS remaining_amount FROM loans WHERE user_id = ? AND total_repayment > 0";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    loanRepaymentsChart.getData().clear();
                    while (rs.next()) {
                        double totalRepayment = rs.getDouble("total_repayment");
                        double remainingAmount = rs.getDouble("remaining_amount");
                        double paidAmount = totalRepayment - remainingAmount;

                        PieChart.Data paidData = new PieChart.Data("Paid Amount", paidAmount);
                        PieChart.Data remainingData = new PieChart.Data("Remaining Amount", remainingAmount);

                        loanRepaymentsChart.getData().add(paidData);
                        loanRepaymentsChart.getData().add(remainingData);

                        addTooltip(paidData, totalRepayment);
                        addTooltip(remainingData, totalRepayment);
                    }
                }
            }

            String overdueQuery = "SELECT COUNT(*) AS overdue_count FROM loans WHERE user_id = ? AND due_date < CURDATE() AND (total_repayment - monthly_installment * repayment_period) > 0";
            try (PreparedStatement overdueStmt = connection.prepareStatement(overdueQuery)) {
                overdueStmt.setInt(1, userId);
                try (ResultSet overdueRs = overdueStmt.executeQuery()) {
                    overdueRepaymentsChart.getData().clear();
                    if (overdueRs.next()) {
                        int overdueCount = overdueRs.getInt("overdue_count");
                        PieChart.Data overdueData = new PieChart.Data("Overdue Loans", overdueCount);
                        PieChart.Data onTimeData = new PieChart.Data("On-time Loans", 1 - overdueCount);

                        overdueRepaymentsChart.getData().add(overdueData);
                        overdueRepaymentsChart.getData().add(onTimeData);

                        addTooltip(overdueData, overdueCount + (1 - overdueCount));
                        addTooltip(onTimeData, overdueCount + (1 - overdueCount));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addTooltip(PieChart.Data data, double total) {
        double percentage = (data.getPieValue() / total) * 100;
        Tooltip tooltip = new Tooltip(String.format("%s: %.2f (%.2f%%)", data.getName(), data.getPieValue(), percentage));
        Tooltip.install(data.getNode(), tooltip);
    }
}