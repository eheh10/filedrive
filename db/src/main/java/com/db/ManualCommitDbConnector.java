package com.db;

import com.db.exception.InputNullParameterException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ManualCommitDbConnector {
    private static final ManualCommitDbConnector INSTANCE = new ManualCommitDbConnector(DbConnector.createConnection());
    private final Connection connection;

    private ManualCommitDbConnector(Connection connection) {
        if (connection == null) {
            throw new InputNullParameterException("Connection: " + connection);
        }

        this.connection = connection;
        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ManualCommitDbConnector getInstance() {
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

    public void commit() {
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
