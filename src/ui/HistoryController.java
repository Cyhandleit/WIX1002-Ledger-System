package ui;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class HistoryController implements Initializable {

    @FXML
    private TableView<Transaction> transactionTable;
    @FXML
    private TableColumn<Transaction, String> dateColumn;
    @FXML
    private TableColumn<Transaction, String> timeColumn;
    @FXML
    private TableColumn<Transaction, String> descriptionColumn;
    @FXML
    private TableColumn<Transaction, Double> debitColumn;
    @FXML
    private TableColumn<Transaction, Double> creditColumn;
    @FXML
    private TableColumn<Transaction, Double> balanceColumn;

    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<String> transactionTypeComboBox;
    @FXML
    private TextField minAmountField;
    @FXML
    private TextField maxAmountField;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private ComboBox<String> dateRangeComboBox;

    private int userId; // Store the user ID of the logged-in user

    // Method to set the user ID (called from the MenuController)
    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("HistoryController setUserId: " + userId);
        loadTransactionHistory();
    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        debitColumn.setCellValueFactory(new PropertyValueFactory<>("debit"));
        creditColumn.setCellValueFactory(new PropertyValueFactory<>("credit"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));

        transactionTypeComboBox.getItems().addAll("All", "Debit", "Credit", "Loan", "Savings");
        sortComboBox.getItems().addAll("Date Ascending", "Date Descending", "Amount Ascending", "Amount Descending");
        dateRangeComboBox.getItems().addAll("All Time", "Last Week", "Last Month", "Last Year");
    }

    @FXML
    private void applyFiltersAndSort() {
        loadTransactionHistory();
    }

    @FXML
    private void loadTransactionHistory() {
        transactionTable.getItems().clear();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String transactionType = transactionTypeComboBox.getValue();
        String minAmountStr = minAmountField.getText();
        String maxAmountStr = maxAmountField.getText();
        String sortOption = sortComboBox.getValue();
        String dateRange = dateRangeComboBox.getValue();

        String displayDate = "All Time";
        if (dateRange != null) {
            switch (dateRange) {
                case "Last Week":
                    startDate = LocalDate.now().minus(1, ChronoUnit.WEEKS);
                    endDate = LocalDate.now();
                    displayDate = "Last Week";
                    break;
                case "Last Month":
                    startDate = LocalDate.now().minus(1, ChronoUnit.MONTHS);
                    endDate = LocalDate.now();
                    displayDate = "Last Month";
                    break;
                case "Last Year":
                    startDate = LocalDate.now().minus(1, ChronoUnit.YEARS);
                    endDate = LocalDate.now();
                    displayDate = "Last Year";
                    break;
                case "All Time":
                default:
                    startDate = null;
                    endDate = null;
                    break;
            }
        }

        String query = "SELECT date, time, description, amount FROM transactions WHERE user_id = ?";
        if (startDate != null) {
            query += " AND date >= '" + startDate + "'";
        }
        if (endDate != null) {
            query += " AND date <= '" + endDate + "'";
        }
        if (transactionType != null && !transactionType.equals("All")) {
            if (transactionType.equals("Debit")) {
                query += " AND amount > 0";
            } else if (transactionType.equals("Credit")) {
                query += " AND amount < 0";
            } else if (transactionType.equals("Loan")) {
                query += " AND description = 'Loan'";
            } else if (transactionType.equals("Savings")) {
                query += " AND description = 'Savings'";
            }
        }
        if (!minAmountStr.isEmpty()) {
            query += " AND amount >= " + Double.parseDouble(minAmountStr);
        }
        if (!maxAmountStr.isEmpty()) {
            query += " AND amount <= " + Double.parseDouble(maxAmountStr);
        }
        if (sortOption != null) {
            switch (sortOption) {
                case "Date Ascending":
                    query += " ORDER BY date ASC, time ASC";
                    break;
                case "Date Descending":
                    query += " ORDER BY date DESC, time DESC";
                    break;
                case "Amount Ascending":
                    query += " ORDER BY amount ASC";
                    break;
                case "Amount Descending":
                    query += " ORDER BY amount DESC";
                    break;
            }
        }

        try (Connection connection = ledgerDB.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                double runningBalance = 0.0;
                while (rs.next()) {
                    String date = rs.getString("date");
                    String time = rs.getString("time");
                    String description = rs.getString("description");
                    double amount = rs.getDouble("amount");
                    double debit = amount > 0 ? amount : 0;
                    double credit = amount < 0 ? -amount : 0;
                    runningBalance += amount;
                    transactionTable.getItems().add(new Transaction(date, time, displayDate, description, debit, credit, runningBalance));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static class Transaction {
        private final String date;
        private final String time;
        private final String displayDate;
        private final String description;
        private final double debit;
        private final double credit;
        private final double balance;

        public Transaction(String date, String time, String displayDate, String description, double debit, double credit, double balance) {
            this.date = date;
            this.time = time;
            this.displayDate = displayDate;
            this.description = description;
            this.debit = debit;
            this.credit = credit;
            this.balance = balance;
        }

        public String getDate() {
            return date;
        }

        public String getTime() {
            return time;
        }

        public String getDisplayDate() {
            return displayDate;
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