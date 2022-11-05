package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "password";

    Connection connection;

    Database() {
        try{
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Couldn't access database.");
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
