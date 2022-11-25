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
    private static final DbConnector CONNECTOR = DbConnector.connection();
    private static final PreparedStatement INSERT_SESSION = CONNECTOR.preparedSql("INSERT INTO session_storage VALUES (?,?)");
    private static final PreparedStatement SEARCH_SESSION = CONNECTOR.preparedSql("SELECT user_num FROM session_storage WHERE id=?");
    private static ResultSet resultSet = null;

    public String createSession(UserDto userDto) {
        if (userDto == null) {
            throw new InputNullParameterException();
        }

        String sessionId = UUID.randomUUID().toString();
        int userNum = userDto.getNum();

        try {
            INSERT_SESSION.setString(1,sessionId);
            INSERT_SESSION.setInt(2,userNum);

            INSERT_SESSION.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return sessionId;
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

    public Integer getUserNum(String sessionId) {
        if (sessionId == null) {
            throw new InputNullParameterException();
        }

        try {
            SEARCH_SESSION.setString(1,sessionId);

            resultSet = SEARCH_SESSION.executeQuery();
            if (!resultSet.next()) {
                return null;
            }

            int userNum = resultSet.getInt("user_num");

            return userNum;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
