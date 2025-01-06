package com.example.App;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends Application {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        launch(args);
    }

    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;

        try {
            Parent root = FXMLLoader.load(getClass().getResource("CoverPage.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setWidth(600);
            stage.setHeight(400);
            stage.show();

            scheduleSavingsTransfer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scheduleSavingsTransfer() {
        LocalDate today = LocalDate.now();
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        long initialDelay = java.time.Duration.between(today.atStartOfDay(), lastDayOfMonth.atStartOfDay()).toMillis();

        scheduler.scheduleAtFixedRate(() -> {
            try (Connection connection = ledgerDB.getConnection()) {
                // Get all users with active savings
                String query = "SELECT user_id, savings_percentage FROM savings";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            int userId = rs.getInt("user_id");
                            double percentage = rs.getDouble("savings_percentage");

                            // Perform the savings transfer
                            try (PreparedStatement transferStmt = connection.prepareStatement(
                                    "UPDATE accounts SET savings = savings + (balance * ? / 100), " +
                                    "balance = balance - (balance * ? / 100) WHERE user_id = ?")) {
                                transferStmt.setDouble(1, percentage);
                                transferStmt.setDouble(2, percentage);
                                transferStmt.setInt(3, userId);
                                transferStmt.executeUpdate();
                            }

                            // Record the transaction
                            TransactionUtils.recordTransaction(userId, -(getBalance(userId) * percentage / 100), "Monthly Savings Transfer");
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error during scheduled savings transfer: " + e.getMessage());
            }
        }, initialDelay, TimeUnit.DAYS.toMillis(30), TimeUnit.MILLISECONDS);
    }

    private double getBalance(int userId) {
        double balance = 0.0;
        try (Connection connection = ledgerDB.getConnection()) {
            String query = "SELECT balance FROM accounts WHERE user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        balance = rs.getDouble("balance");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting balance: " + e.getMessage());
        }
        return balance;
    }

    public void changeScene(String MenuPagel) throws Exception {
        Parent pane = FXMLLoader.load(getClass().getResource("MenuPage.fxml"));
        stage.getScene().setRoot(pane);
    }
}