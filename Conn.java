package project2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conn implements AutoCloseable {
    private Connection connection;

    public Conn() {
        try {
            // MySQL connection URL, username, and password
            String url = "jdbc:mysql://localhost:3306/Inspira";
            String username = "root";
            String password = "Kasi@#root12";

            // Attempt to establish a connection
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            // Optionally, you can rethrow the exception if needed
            // throw new RuntimeException("Database connection failed", e);
        }
    }

    // Getter for the connection object
    public Connection getConnection() {
        return connection;
    }

    // Implement AutoCloseable interface for proper resource management
    @Override
    public void close() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Database connection closed.");
                }
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    // Main method for testing the connection
    public static void main(String[] args) {
        try (Conn dbConnection = new Conn()) {
            // You can test the connection here
            if (dbConnection.getConnection() != null) {
                System.out.println("Connection is valid: " + dbConnection.getConnection());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
