package restaurant.management.system;

import java.sql.*;

public class Conn {
    Connection c;
    Statement s;

    public Conn() {
        try {
            // Database connection details
            String url = "jdbc:mysql://localhost:3306/rms";  // Ensure this matches your actual database name
            String user = "root";  // MySQL username
            String password = "Shreeya@1234h";  // MySQL password

            // Establishing the connection
            c = DriverManager.getConnection(url, user, password);
            s = c.createStatement();

            // Sample query to check connection
            String sql = "SELECT item_name FROM items"; // Modify to any valid table you have
            ResultSet rs = s.executeQuery(sql);

            // Printing the first result for verification
            if (rs.next()) {
                String itemName = rs.getString(1);
                System.out.println("Connected successfully. Sample data: " + itemName);
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    // Main method for testing the connection
    public static void main(String[] args) {
        new Conn();
    }
}
