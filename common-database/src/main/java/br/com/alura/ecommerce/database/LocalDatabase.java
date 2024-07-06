package br.com.alura.ecommerce.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LocalDatabase {

    private final Connection connection;

    public LocalDatabase(String name) throws SQLException {
        String url = "jdbc:sqlite:target/" + name + ".db";
        this.connection = DriverManager.getConnection(url);
    }

    public void createIfNotExists(String table) throws SQLException {
        try (var stmt = connection.createStatement()) {
            stmt.execute("create table if not exists " + table);
        }
    }

    public boolean update(String statement, String ...params) throws SQLException {
        try (var stmt = connection.prepareStatement(statement)) {
            prepare(params, stmt);
            stmt.execute();
            return true;
        }
    }

    private static void prepare(String[] params, PreparedStatement stmt) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setString(i + 1, params[i]);
        }
    }

    public ResultSet query(String query, String ...params) throws SQLException {
        try (var stmt = connection.prepareStatement(query)) {
            prepare(params, stmt);
            return stmt.executeQuery();
        }
    }

    public void close() throws SQLException {
        connection.close();
    }
}
