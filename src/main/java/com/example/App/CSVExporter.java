package com.example.App;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class CSVExporter {

    public static void exportTableToCSV(String tableName, String filePath) {
        try (Connection connection = ledgerDB.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tableName);
             FileWriter fileWriter = new FileWriter(filePath)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Write header
            for (int i = 1; i <= columnCount; i++) {
                fileWriter.append(metaData.getColumnName(i));
                if (i < columnCount) {
                    fileWriter.append(",");
                }
            }
            fileWriter.append("\n");

            // Write data
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    fileWriter.append(resultSet.getString(i));
                    if (i < columnCount) {
                        fileWriter.append(",");
                    }
                }
                fileWriter.append("\n");
            }

            System.out.println("Data exported successfully from table " + tableName + " to " + filePath);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        exportTableToCSV("users", "users.csv");
        exportTableToCSV("accounts", "accounts.csv");
        exportTableToCSV("transactions", "transactions.csv");
        exportTableToCSV("loans", "loans.csv");
        exportTableToCSV("savings", "savings.csv");
    }
}
