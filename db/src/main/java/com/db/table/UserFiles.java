package com.db.table;

import com.db.DbConnector;
import com.db.dto.FileDto;
import com.db.dto.UserDto;
import com.db.exception.InputNullParameterException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserFiles {
    private static final DbConnector CONNECTOR = DbConnector.getInstance();
    private static final PreparedStatement INSERT_FILE = CONNECTOR.preparedSql("INSERT INTO files(name,path,size,user_num) VALUES (?,?,?,?)");
    private static final PreparedStatement SEARCH_FILES_BY_USER_NUM = CONNECTOR.preparedSql("SELECT * FROM files WHERE user_num=?");
    private static final PreparedStatement SEARCH_FILE = CONNECTOR.preparedSql("SELECT * FROM files WHERE name=? and user_num=?");
    private static ResultSet resultSet = null;

    public void insert(UserDto userDto, FileDto fileDto) {
        if (userDto == null || fileDto == null) {
            throw new InputNullParameterException();
        }

        try {
            INSERT_FILE.setString(1,fileDto.getName());
            INSERT_FILE.setString(2,fileDto.getPath());
            INSERT_FILE.setInt(3,fileDto.getSize());
            INSERT_FILE.setInt(4,userDto.getNum());

            INSERT_FILE.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<FileDto> filesOf(int userNum) {
        Set<FileDto> files = new HashSet<>();

        try {
            SEARCH_FILES_BY_USER_NUM.setInt(1,userNum);

            resultSet = SEARCH_FILES_BY_USER_NUM.executeQuery();

            while (resultSet.next()) {
                int num = resultSet.getInt("num");
                String name = resultSet.getString("name");
                String path = resultSet.getString("path");
                int size = resultSet.getInt("size");

                files.add(FileDto.builder()
                        .num(num)
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

    public FileDto findFile(String fileName, int userNum) {
        if (fileName == null ) {
            throw new InputNullParameterException();
        }

        try {
            SEARCH_FILE.setString(1,fileName);
            SEARCH_FILE.setInt(2,userNum);

            resultSet = SEARCH_FILE.executeQuery();
            if (!resultSet.next()) {
                return null;
            }

            return FileDto.builder()
                    .num(resultSet.getInt("num"))
                    .name(resultSet.getString("name"))
                    .path(resultSet.getString("path"))
                    .size(resultSet.getInt("size"))
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
