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
    private final int version;

    private FileDownloadDto(String uid, String userUid, LocalDate downloadDate, int count, int version) {
        if (uid==null || userUid ==null || downloadDate ==null) {
            throw new InputNullParameterException(
                    "uid: "+uid+"\n"+
                    "userUid: "+userUid+"\n"+
                    "downloadDate: "+downloadDate+"\n"
            );
        }

        if (count < 0 || version < 0) {
            throw new MustBePositiveNumberException();
        }

        this.uid = uid;
        this.userUid = userUid;
        this.downloadDate = downloadDate;
        this.count = count;
        this.version = version;
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

    public int getVersion() {
        return version;
    }
}
