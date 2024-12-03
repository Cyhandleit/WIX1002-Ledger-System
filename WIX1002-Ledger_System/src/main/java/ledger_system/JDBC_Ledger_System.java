// package ledger_system;

// import java.sql.*;

//     public class JDBC_Ledger_System {

//         public static void main(String[] args) {
//             // Database connection parameters
//             String url = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12748872";
//             String username = "sql12748872";
//             String password = "ZkCKfez9Yj";
    
//             // SQL query to insert data
//             String sql = "INSERT INTO employees (id, name, salary) VALUES (?, ?, ?)";
    
//             // Establish a connection to the database
//             try (Connection connection = DriverManager.getConnection(url, username, password);
//                  PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
    
//                 // Set the parameters for the SQL query
//                 preparedStatement.setInt(1, 2);  // id
//                 preparedStatement.setString(2, "Daniel");  // name
//                 preparedStatement.setDouble(3, 6200.00);  // salary
    
//                 // Execute the insert query
//                 int rowsAffected = preparedStatement.executeUpdate();
    
//                 // Check if the insert was successful
//                 if (rowsAffected > 0) {
//                     System.out.println("Data inserted successfully!");
//                 } else {
//                     System.out.println("Failed to insert data.");
//                 }
    
//             } catch (SQLException e) {
//                 e.printStackTrace();
//             }
//         }
// }