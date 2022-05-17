package com.revature;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ConnectionFactory {
    private static Connection connection = null;
    private ConnectionFactory() {

    }
    public static Connection getConnection(){
        if (connection == null) {
            ResourceBundle bundle = ResourceBundle.getBundle("com/revature/dbConfig");
            String url = bundle.getString("url");
            String username = bundle.getString("username");
            String pass = bundle.getString("password");
            try {
                connection = DriverManager.getConnection(url, username, System.getenv(pass));
                System.out.println("Connection established");
            } catch (SQLException e) {
                System.out.println("Failed to connect...");
                e.printStackTrace();
            }
        }
        return connection;
    }
    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed");
        } catch (SQLException e) {
            System.out.println("Failed to close connection...");
            e.printStackTrace();
        }
    }
}
