package com.db.dto;

import com.db.exception.InputNullParameterException;
import com.db.exception.MustBePositiveNumberException;
import lombok.Builder;
import lombok.ToString;

import java.time.LocalDate;

@Builder
@ToString
public class FileDownloadDto {
    private final String uid;
    private final String userUid;
    private final LocalDate downloadDate;
    private final int count;

    private FileDownloadDto(String uid, String userUid, LocalDate downloadDate, int count) {
        if (uid==null || userUid ==null || downloadDate ==null) {
            throw new InputNullParameterException();
        }

        if (count < 0) {
            throw new MustBePositiveNumberException();
        }

        this.uid = uid;
        this.userUid = userUid;
        this.downloadDate = downloadDate;
        this.count = count;
    }

    public String getUid() {
        return uid;
    }

    public String getUserUid() {
        return userUid;
    }

    public LocalDate getDownloadDate() {
        return downloadDate;
    }

    public int getCount() {
        return count;
    }
}
