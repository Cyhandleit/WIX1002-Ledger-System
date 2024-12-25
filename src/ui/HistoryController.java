package ui;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class HistoryController implements Initializable {

    @FXML
    private TableView<Transaction> transactionTable;
    @FXML
    private TableColumn<Transaction, String> dateColumn;
    @FXML
    private TableColumn<Transaction, String> descriptionColumn;
    @FXML
    private TableColumn<Transaction, Double> debitColumn;
    @FXML
    private TableColumn<Transaction, Double> creditColumn;
    @FXML
    private TableColumn<Transaction, Double> balanceColumn;

    private int userId; // Store the user ID of the logged-in user

    // Method to set the user ID (called from the MenuController)
    public void setUserId(int userId) {
        this.userId = userId;
        loadTransactionHistory();
    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        debitColumn.setCellValueFactory(new PropertyValueFactory<>("debit"));
        creditColumn.setCellValueFactory(new PropertyValueFactory<>("credit"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
    }

    private void loadTransactionHistory() {
        try (Connection connection = ledgerDB.getConnection()) {
            String query = "SELECT date, description, amount FROM transactions WHERE user_id = ? ORDER BY date";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    double runningBalance = 0.0;
                    while (rs.next()) {
                        String date = rs.getString("date");
                        String description = rs.getString("description");
                        double amount = rs.getDouble("amount");
                        double debit = amount > 0 ? amount : 0;
                        double credit = amount < 0 ? -amount : 0;
                        runningBalance += amount;
                        transactionTable.getItems().add(new Transaction(date, description, debit, credit, runningBalance));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void openSpendingDistribution(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SpendingDistributionPage.fxml"));
            Parent root = loader.load();

            SpendingDistributionController controller = loader.getController();
            controller.setUserId(userId);
            controller.loadSpendingDistribution();

            Stage stage = new Stage();
            stage.setTitle("Spending Distribution");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Transaction {
        private final String date;
        private final String description;
        private final double debit;
        private final double credit;
        private final double balance;

        public Transaction(String date, String description, double debit, double credit, double balance) {
            this.date = date;
            this.description = description;
            this.debit = debit;
            this.credit = credit;
            this.balance = balance;
        }

        public String getDate() {
            return date;
        }

        public String getDescription() {
            return description;
        }

        public double getDebit() {
            return debit;
        }

        public double getCredit() {
            return credit;
        }

        public double getBalance() {
            return balance;
        }
    }
}