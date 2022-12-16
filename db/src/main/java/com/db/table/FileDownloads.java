package com.db.table;

import com.db.connector.DbConnector;
import com.db.connector.ManualCommitDbConnector;
import com.db.dto.FileDownloadDto;
import com.db.exception.InputNullParameterException;
import com.db.exception.VersionUpdatedException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FileDownloads {
    private static final DbConnector CONNECTOR = DbConnector.getInstance();
    private static final ManualCommitDbConnector MANUAL_CONNECTOR = ManualCommitDbConnector.getInstance();
    private static final PreparedStatement INSERT_DOWNLOAD = CONNECTOR.preparedSql("INSERT INTO downloads(uid,user_uid,download_date,count) VALUES (?,?,?,?)");
    private static final PreparedStatement UPDATE_COUNT = MANUAL_CONNECTOR.preparedSql("UPDATE downloads SET count=?,version=? WHERE uid=? and version=?");
    private static final PreparedStatement SEARCH_DOWNLOAD = CONNECTOR.preparedSql("SELECT * FROM downloads WHERE user_uid=? and download_date=?");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
    private static ResultSet resultSet = null;

    public void insert(FileDownloadDto fileDownloadDto) {
        if (fileDownloadDto == null) {
            throw new InputNullParameterException();
        }

        Date formattedDate = Date.valueOf(fileDownloadDto.getDownloadDate().format(DATE_FORMATTER));

        try {
            INSERT_DOWNLOAD.setString(1, fileDownloadDto.getUid());
            INSERT_DOWNLOAD.setString(2, fileDownloadDto.getUserUid());
            INSERT_DOWNLOAD.setDate(3, formattedDate);
            INSERT_DOWNLOAD.setInt(4, fileDownloadDto.getCount());

            INSERT_DOWNLOAD.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void countDownload(FileDownloadDto fileDownloadDto, int numberOfFiles) {
        if (fileDownloadDto == null) {
            throw new InputNullParameterException();
        }

        try {
            UPDATE_COUNT.setInt(1, fileDownloadDto.getCount()+numberOfFiles);
            UPDATE_COUNT.setInt(2, fileDownloadDto.getVersion()+1);
            UPDATE_COUNT.setString(3, fileDownloadDto.getUid());
            UPDATE_COUNT.setInt(4, fileDownloadDto.getVersion());

            int rows = UPDATE_COUNT.executeUpdate();

            if (rows == 0) {
                MANUAL_CONNECTOR.rollback();
                throw new VersionUpdatedException();
            }

            MANUAL_CONNECTOR.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public FileDownloadDto searchDownload(String userUid, LocalDate downloadDate) {
        if (userUid == null || downloadDate == null) {
            throw new InputNullParameterException(
                    "userUid: "+userUid+"\n"+
                    "downloadDate: "+downloadDate+"\n"
            );
        }

        Date formattedDate = Date.valueOf(downloadDate.format(DATE_FORMATTER));

        try {
            SEARCH_DOWNLOAD.setString(1, userUid);
            SEARCH_DOWNLOAD.setDate(2, formattedDate);

            resultSet = SEARCH_DOWNLOAD.executeQuery();
            if (!resultSet.next()) {
                return null;
            }

            return FileDownloadDto.builder()
                    .uid(resultSet.getString("uid"))
                    .userUid(resultSet.getString("user_uid"))
                    .downloadDate(resultSet.getDate("download_date").toLocalDate())
                    .count(resultSet.getInt("count"))
                    .version(resultSet.getInt("version"))
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
