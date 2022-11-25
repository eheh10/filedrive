package com.db.table;

import com.db.DbConnector;
import com.db.dto.FileDto;
import com.db.dto.UserDto;
import com.db.exception.InputNullParameterException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Users {
    private static final DbConnector CONNECTOR = DbConnector.connection();
    private static final PreparedStatement REGISTER_USER = CONNECTOR.preparedSql("INSERT INTO users(id,password) VALUES (?,?)");
    private static final PreparedStatement FIND_ID = CONNECTOR.preparedSql("SELECT id FROM users WHERE id=?");
    private static final PreparedStatement FIND_USER_BY_NUM = CONNECTOR.preparedSql("SELECT * FROM users WHERE num=?");
    private static final PreparedStatement FIND_USER_BY_ID_PWD = CONNECTOR.preparedSql("SELECT * FROM users WHERE id=? and password=?");
    private static final PreparedStatement UPDATE_CAPACITY = CONNECTOR.preparedSql("UPDATE users SET usage_capacity=? WHERE id=? and password=?");
    private static ResultSet resultSet = null;

    public void insert(UserDto userDto) {
        if (userDto == null) {
            throw new InputNullParameterException();
        }

        try {
            REGISTER_USER.setString(1,userDto.getId());
            REGISTER_USER.setString(2,userDto.getPwd());

            REGISTER_USER.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean alreadyRegisteredId(String id) {
        if (id == null) {
            throw new InputNullParameterException();
        }

        try {
            FIND_ID.setString(1,id);

            resultSet = FIND_ID.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserDto find_BY_NUM(int num) {
        try {
            FIND_USER_BY_NUM.setInt(1,num);

            FIND_USER_BY_NUM.executeQuery();

            resultSet = FIND_USER_BY_ID_PWD.executeQuery();
            if (!resultSet.next()) {
                return null;
            }

            String id = resultSet.getString("id");
            String pwd = resultSet.getString("password");
            int usageCapacity = resultSet.getInt("usage_capacity");

            UserDto foundUser = UserDto.builder()
                    .num(num)
                    .id(id)
                    .pwd(pwd)
                    .usageCapacity(usageCapacity)
                    .build();

            return foundUser;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserDto find_BY_ID_PWD(String id, String pwd) {
        if (id == null || pwd == null) {
            throw new InputNullParameterException();
        }

        try {
            FIND_USER_BY_ID_PWD.setString(1,id);
            FIND_USER_BY_ID_PWD.setString(2,pwd);

            resultSet = FIND_USER_BY_ID_PWD.executeQuery();

            if (!resultSet.next()) {
                return null;
            }

            int num = resultSet.getInt("num");
            int usageCapacity = resultSet.getInt("usage_capacity");

            UserDto foundUser = UserDto.builder()
                    .num(num)
                    .id(id)
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

        String id = userDto.getId();
        String pwd = userDto.getPwd();
        int usedCapacity = userDto.getUsageCapacity();
        int sumCapacity = usedCapacity + fileDto.getSize();

        try {
            UPDATE_CAPACITY.setInt(1,sumCapacity);
            UPDATE_CAPACITY.setString(2,id);
            UPDATE_CAPACITY.setString(3,pwd);

            UPDATE_CAPACITY.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
