package com.db.table;

import com.db.DbConnector;
import com.db.dto.FileDto;
import com.db.dto.UserDto;
import com.db.exception.InputNullParameterException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Users {
    private static final DbConnector CONNECTOR = DbConnector.getInstance();
    private static final PreparedStatement REGISTER_USER = CONNECTOR.preparedSql("INSERT INTO users(uid,name,password) VALUES (?,?,?)");
    private static final PreparedStatement SEARCH_NAME = CONNECTOR.preparedSql("SELECT name FROM users WHERE name=?");
    private static final PreparedStatement SEARCH_USER_BY_UID = CONNECTOR.preparedSql("SELECT * FROM users WHERE uid=?");
    private static final PreparedStatement SEARCH_USER_BY_NAME_PWD = CONNECTOR.preparedSql("SELECT * FROM users WHERE name=? and password=?");
    private static final PreparedStatement UPDATE_CAPACITY = CONNECTOR.preparedSql("UPDATE users SET usage_capacity=? WHERE uid=?");
    private static ResultSet resultSet = null;

    public void insert(UserDto userDto) {
        if (userDto == null) {
            throw new InputNullParameterException();
        }

        try {
            REGISTER_USER.setString(1, UUID.randomUUID().toString());
            REGISTER_USER.setString(2,userDto.getName());
            REGISTER_USER.setString(3,userDto.getPwd());

            REGISTER_USER.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAlreadyRegisteredName(String name) {
        if (name == null) {
            throw new InputNullParameterException();
        }

        try {
            SEARCH_NAME.setString(1,name);

            resultSet = SEARCH_NAME.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserDto searchByUid(String uid) {
        try {
            SEARCH_USER_BY_UID.setString(1,uid);

            SEARCH_USER_BY_UID.executeQuery();

            resultSet = SEARCH_USER_BY_UID.executeQuery();
            if (!resultSet.next()) {
                return null;
            }

            String name = resultSet.getString("name");
            String pwd = resultSet.getString("password");
            int usageCapacity = resultSet.getInt("usage_capacity");

            UserDto foundUser = UserDto.builder()
                    .uid(uid)
                    .name(name)
                    .pwd(pwd)
                    .usageCapacity(usageCapacity)
                    .build();

            return foundUser;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserDto searchByNamePwd(String name, String pwd) {
        if (name == null || pwd == null) {
            throw new InputNullParameterException();
        }

        try {
            SEARCH_USER_BY_NAME_PWD.setString(1,name);
            SEARCH_USER_BY_NAME_PWD.setString(2,pwd);

            resultSet = SEARCH_USER_BY_NAME_PWD.executeQuery();

            if (!resultSet.next()) {
                return null;
            }

            String uid = resultSet.getString("uid");
            int usageCapacity = resultSet.getInt("usage_capacity");

            UserDto foundUser = UserDto.builder()
                    .uid(uid)
                    .name(name)
                    .pwd(pwd)
                    .usageCapacity(usageCapacity)
                    .build();

            return foundUser;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void useStorageCapacity(UserDto userDto, FileDto fileDto) {
        if (userDto == null || fileDto == null) {
            throw new InputNullParameterException();
        }

        int usedCapacity = userDto.getUsageCapacity();
        int sumCapacity = usedCapacity + fileDto.getSize();

        try {
            UPDATE_CAPACITY.setInt(1,sumCapacity);
            UPDATE_CAPACITY.setString(2,userDto.getUid());

            UPDATE_CAPACITY.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
