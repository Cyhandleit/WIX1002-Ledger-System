package com.example.App;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;

public class SpendingTrendsController {

    @FXML
    private BarChart<String, Number> spendingTrendsChart;

    private int userId; // Store the user ID of the logged-in user

    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("SpendingTrendsController setUserId: " + userId);
    }

    @FXML
    public void loadSpendingTrends() {
        System.out.println("Loading spending trends for userId: " + userId);
        try (Connection connection = ledgerDB.getConnection()) {
            String query = "SELECT date, SUM(amount) AS total_spending FROM transactions WHERE user_id = ? AND amount < 0 GROUP BY date ORDER BY date";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName("Spending Trends");
                    while (rs.next()) {
                        String date = rs.getString("date");
                        double totalSpending = rs.getDouble("total_spending");
                        XYChart.Data<String, Number> data = new XYChart.Data<>(date, -totalSpending);
                        series.getData().add(data);
                        addTooltip(data);
                    }
                    spendingTrendsChart.getData().add(series);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void addTooltip(XYChart.Data<String, Number> data) {
        Tooltip tooltip = new Tooltip(String.format("%s: %.2f", data.getXValue(), data.getYValue().doubleValue()));
        Tooltip.install(data.getNode(), tooltip);
    }
}