import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

    public class jdbc_ledger_system {

        public static void main(String[] args) {
            // Database connection parameters
            String url = "jdbc:mysql://localhost:3306/mydb";
            String username = "root";
            String password = "cyh051205";
    
            // SQL query to insert data
            String sql = "INSERT INTO employees (id, name, salary) VALUES (?, ?, ?)";
    
            // Establish a connection to the database
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
    
                // Set the parameters for the SQL query
                preparedStatement.setInt(1, 6);  // id
                preparedStatement.setString(2, "defg");  // name
                preparedStatement.setDouble(3, 20205.00);  // salary
    
                // Execute the insert query
                int rowsAffected = preparedStatement.executeUpdate();
    
                // Check if the insert was successful
                if (rowsAffected > 0) {
                    System.out.println("Data inserted successfully!");
                } else {
                    System.out.println("Failed to insert data.");
                }
    
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
}