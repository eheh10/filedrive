package com.db.dto;

import com.db.exception.InputNullParameterException;
import com.db.exception.MustBePositiveNumberException;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class FileDto {
    private final String uid;
    private final String name;
    private final String path;
    private final int size;

    private FileDto(String uid, String name, String path, int size) {
        if (uid == null || name == null || path == null) {
            throw new InputNullParameterException();
        }

        if (size < 0 ) {
            throw new MustBePositiveNumberException();
        }
        this.uid = uid;
        this.name = name;
        this.path = path;
        this.size = size;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getSize() {
        return size;
    }
}
