package com.db;

import com.db.exception.InputNullParameterException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbConnector {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/filedrive";
    private static final String DB_USER = "filedrive_user";
    private static final String DB_PASSWORD = "filedrive";

    private static final DbConnector INSTANCE = new DbConnector(createConnection());

    private final Connection connection;

    private DbConnector(Connection connection) {
        if (connection == null) {
            throw new InputNullParameterException();
        }
        this.connection = connection;
    }

    private static Connection createConnection() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            throw new ConnectionFailException(e.getStackTrace().toString());
        }
    }

    public static DbConnector getInstance() {
        return INSTANCE;
    }

    public PreparedStatement preparedSql(String sql) {
        if (sql == null) {
            throw new InputNullParameterException();
        }

        try {
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        if (connection == null) {
            return;
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
