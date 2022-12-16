package com.db.table;

import com.db.connector.DbConnector;
import com.db.connector.ManualCommitDbConnector;
import com.db.Sha256Encryption;
import com.db.dto.FileDto;
import com.db.dto.UserDto;
import com.db.exception.InputNullParameterException;
import com.db.exception.VersionUpdatedException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Users {
    private static final DbConnector CONNECTOR = DbConnector.getInstance();
    private static final ManualCommitDbConnector MANUAL_CONNECTOR = ManualCommitDbConnector.getInstance();
    private static final PreparedStatement REGISTER_USER = CONNECTOR.preparedSql("INSERT INTO users(uid,name,password,google_uid) VALUES (?,?,?,?)");
    private static final PreparedStatement SEARCH_NAME = CONNECTOR.preparedSql("SELECT name FROM users WHERE name=?");
    private static final PreparedStatement SEARCH_USER_BY_UID = CONNECTOR.preparedSql("SELECT * FROM users WHERE uid=?");
    private static final PreparedStatement SEARCH_USER_BY_NAME_PWD = CONNECTOR.preparedSql("SELECT * FROM users WHERE name=? and password=?");
    private static final PreparedStatement SEARCH_USER_BY_GOOGLE_UID = CONNECTOR.preparedSql("SELECT * FROM users WHERE google_uid=?");
    private static final PreparedStatement UPDATE_CAPACITY = MANUAL_CONNECTOR.preparedSql("UPDATE users SET usage_capacity=?,version=? WHERE uid=? and version=?");
    private static final Sha256Encryption ENCRYPTION = Sha256Encryption.getInstance();
    private static ResultSet resultSet = null;

    public void insert(UserDto userDto) {
        if (userDto == null) {
            throw new InputNullParameterException();
        }

        String cryptogramPwd = userDto.getPwd();

        if (cryptogramPwd != null) {
            cryptogramPwd = ENCRYPTION.encrypt(userDto.getPwd());
        };

        try {
            REGISTER_USER.setString(1,userDto.getUid());
            REGISTER_USER.setString(2,userDto.getName());
            REGISTER_USER.setString(3,cryptogramPwd);
            REGISTER_USER.setString(4, userDto.getGoogleUid());

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
            String googleUid = resultSet.getString("google_uid");
            int version = resultSet.getInt("version");

            UserDto foundUser = UserDto.builder()
                    .uid(uid)
                    .name(name)
                    .pwd(pwd)
                    .usageCapacity(usageCapacity)
                    .googleUid(googleUid)
                    .version(version)
                    .build();

            return foundUser;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserDto searchByNamePwd(String name, String pwd) {
        if (name == null || pwd == null) {
            throw new InputNullParameterException(
                    "name: "+name+"\n"+
                    "pwd: "+pwd+"\n"
            );
        }

        String cryptogramPwd = ENCRYPTION.encrypt(pwd);

        try {
            SEARCH_USER_BY_NAME_PWD.setString(1,name);
            SEARCH_USER_BY_NAME_PWD.setString(2,cryptogramPwd);

            resultSet = SEARCH_USER_BY_NAME_PWD.executeQuery();

            if (!resultSet.next()) {
                return null;
            }

            String uid = resultSet.getString("uid");
            String password = resultSet.getString("password");
            int usageCapacity = resultSet.getInt("usage_capacity");
            String googleUid = resultSet.getString("google_uid");
            int version = resultSet.getInt("version");

            UserDto foundUser = UserDto.builder()
                    .uid(uid)
                    .name(name)
                    .pwd(password)
                    .usageCapacity(usageCapacity)
                    .googleUid(googleUid)
                    .version(version)
                    .build();

            return foundUser;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void useStorageCapacity(UserDto userDto, FileDto fileDto) {
        if (userDto == null || fileDto == null) {
            throw new InputNullParameterException(
                    "userDto: "+userDto+"\n"+
                    "fileDto: "+fileDto+"\n"
            );
        }

        int usedCapacity = userDto.getUsageCapacity();
        int sumCapacity = usedCapacity + fileDto.getSize();

        try {
            UPDATE_CAPACITY.setInt(1,sumCapacity);
            UPDATE_CAPACITY.setInt(2,userDto.getVersion()+1);
            UPDATE_CAPACITY.setString(3,userDto.getUid());
            UPDATE_CAPACITY.setInt(4,userDto.getVersion());

            int rows = UPDATE_CAPACITY.executeUpdate();
            if (rows == 0) {
                MANUAL_CONNECTOR.rollback();
                throw new VersionUpdatedException();
            }

            MANUAL_CONNECTOR.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserDto searchByGoogleUid(String googleUid) {
        try {
            SEARCH_USER_BY_GOOGLE_UID.setString(1,googleUid);

            SEARCH_USER_BY_GOOGLE_UID.executeQuery();

            resultSet = SEARCH_USER_BY_GOOGLE_UID.executeQuery();
            if (!resultSet.next()) {
                return null;
            }

            String uid = resultSet.getString("uid");
            String name = resultSet.getString("name");
            String pwd = resultSet.getString("password");
            int usageCapacity = resultSet.getInt("usage_capacity");
            int version = resultSet.getInt("version");

            UserDto foundUser = UserDto.builder()
                    .uid(uid)
                    .name(name)
                    .pwd(pwd)
                    .usageCapacity(usageCapacity)
                    .googleUid(googleUid)
                    .version(version)
                    .build();

            return foundUser;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
