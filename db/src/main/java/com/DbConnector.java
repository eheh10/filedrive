package com;

import com.exception.ConnectionFailException;
import com.exception.InputNullParameterException;

import java.sql.*;

public class DbConnector {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/filedrive";
    private static final String DB_USER = "filedrive_user";
    private static final String DB_PASSWORD = "filedrive";
    private static Connection connection = null;

    private DbConnector(Connection connection) {
        if (connection == null) {
            throw new InputNullParameterException();
        }
        this.connection = connection;
    }

    public static DbConnector connection() {
        try {
            if (connection == null) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
            return new DbConnector(connection);
        } catch (SQLException e) {
            throw new ConnectionFailException(e.getStackTrace().toString());
        }
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
