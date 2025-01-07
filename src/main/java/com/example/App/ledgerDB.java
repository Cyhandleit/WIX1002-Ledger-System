package com.example.App;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class ledgerDB {

    private static final String URL = "jdbc:mysql://sql.freedb.tech:3306/freedb_LedgerDB";
    private static final String USER = "freedb_ledgerUsers";
    private static final String PASSWORD = "PD7*MgsTM!8x??d";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setupDatabase() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             BufferedReader reader = new BufferedReader(new InputStreamReader(
                     ledgerDB.class.getResourceAsStream("/setupDatabase.sql")))) {

            StringBuilder sql = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sql.append(line).append("\n");
            }

            statement.execute(sql.toString());
            System.out.println("Database setup completed successfully.");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}