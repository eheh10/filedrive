package com.db.table;

import com.db.DbConnector;
import com.db.dto.FileDto;
import com.db.dto.UserDto;
import com.db.exception.InputNullParameterException;
import com.db.exception.MustBePositiveNumberException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserFiles {
    private static final DbConnector CONNECTOR = DbConnector.getInstance();
    private static final PreparedStatement INSERT_FILE = CONNECTOR.preparedSql("INSERT INTO files(uid,name,path,size,user_uid) VALUES (?,?,?,?,?)");
    private static final PreparedStatement SEARCH_FILES_BY_USER_NUM = CONNECTOR.preparedSql("SELECT * FROM files WHERE user_uid=?");
    private static final PreparedStatement SEARCH_FILE = CONNECTOR.preparedSql("SELECT * FROM files WHERE name=? and user_uid=?");
    private static ResultSet resultSet = null;

    public void insert(UserDto userDto, FileDto fileDto) {
        if (userDto == null || fileDto == null) {
            throw new InputNullParameterException(
                    "userDto: "+userDto+"\n"+
                    "fileDto: "+fileDto+"\n"
            );
        }

        if (fileDto.getSize() < 0) {
            throw new MustBePositiveNumberException();
        }

        try {
            INSERT_FILE.setString(1, fileDto.getUid());
            INSERT_FILE.setString(2,fileDto.getName());
            INSERT_FILE.setString(3,fileDto.getPath());
            INSERT_FILE.setInt(4,fileDto.getSize());
            INSERT_FILE.setString(5,userDto.getUid());

            INSERT_FILE.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<FileDto> filesOf(String userUid) {
        Set<FileDto> files = new HashSet<>();

        try {
            SEARCH_FILES_BY_USER_NUM.setString(1,userUid);

            resultSet = SEARCH_FILES_BY_USER_NUM.executeQuery();

            while (resultSet.next()) {
                String uid = resultSet.getString("uid");
                String name = resultSet.getString("name");
                String path = resultSet.getString("path");
                int size = resultSet.getInt("size");

                files.add(FileDto.builder()
                        .uid(uid)
                        .name(name)
                        .path(path)
                        .size(size)
                        .build()
                );
            }

            return Collections.unmodifiableSet(files);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public FileDto searchFile(String fileName, String userUid) {
        if (fileName == null || userUid == null) {
            throw new InputNullParameterException(
                    "fileName: "+fileName+"\n"+
                    "userUid: "+userUid+"\n"
            );
        }

        try {
            SEARCH_FILE.setString(1,fileName);
            SEARCH_FILE.setString(2,userUid);

            resultSet = SEARCH_FILE.executeQuery();
            if (!resultSet.next()) {
                return null;
            }

            return FileDto.builder()
                    .uid(resultSet.getString("uid"))
                    .name(resultSet.getString("name"))
                    .path(resultSet.getString("path"))
                    .size(resultSet.getInt("size"))
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
