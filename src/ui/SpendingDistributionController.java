package ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;

public class SpendingDistributionController {

    @FXML
    private PieChart spendingDistributionChart;

    private int userId; // Store the user ID of the logged-in user

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void loadSpendingDistribution() {
        try (Connection connection = ledgerDB.getConnection()) {
            String query = "SELECT description, SUM(amount) AS total_spending FROM transactions WHERE user_id = ? AND amount < 0 GROUP BY description";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String description = rs.getString("description");
                        double totalSpending = rs.getDouble("total_spending");
                        spendingDistributionChart.getData().add(new PieChart.Data(description, -totalSpending));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
