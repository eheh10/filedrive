package com.db.table;

import com.db.DbConnector;
import com.db.dto.UserDto;
import com.db.exception.InputNullParameterException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SessionStorage {
    public static final String SESSION_FIELD_NAME = "sessionId";
    private static final DbConnector CONNECTOR = DbConnector.getInstance();
    private static final PreparedStatement INSERT_SESSION = CONNECTOR.preparedSql("INSERT INTO session_storage VALUES (?,?)");
    private static final PreparedStatement SEARCH_SESSION = CONNECTOR.preparedSql("SELECT user_uid FROM session_storage WHERE uid=?");
    private static final PreparedStatement DELETE_SESSION = CONNECTOR.preparedSql("DELETE FROM session_storage WHERE uid=?");
    private static ResultSet resultSet = null;

    public String createSession(UserDto userDto) {
        if (userDto == null) {
            throw new InputNullParameterException();
        }

        String sessionUId = UUID.randomUUID().toString();

        try {
            INSERT_SESSION.setString(1,sessionUId);
            INSERT_SESSION.setString(2,userDto.getUid());

            INSERT_SESSION.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return sessionUId;
    }

    public boolean isUnregisteredSession(String sessionId) {
        if (sessionId == null) {
            throw new InputNullParameterException();
        }

        try {
            SEARCH_SESSION.setString(1,sessionId);

            resultSet = SEARCH_SESSION.executeQuery();
            return !resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUserUid(String sessionId) {
        if (sessionId == null) {
            throw new InputNullParameterException();
        }

        try {
            SEARCH_SESSION.setString(1,sessionId);

            resultSet = SEARCH_SESSION.executeQuery();
            if (!resultSet.next()) {
                return null;
            }

            return resultSet.getString("user_uid");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void expireSession(String sessionId) {
        if (sessionId == null) {
            throw new InputNullParameterException();
        }

        try {
            DELETE_SESSION.setString(1,sessionId);

            DELETE_SESSION.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
