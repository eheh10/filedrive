package com.db.dto;

import com.db.exception.InputNullParameterException;
import com.db.exception.MustBePositiveNumberException;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class UserDto {
    private final String uid;
    private final String name;
    private final String pwd;
    private final int usageCapacity;

    private UserDto(String uid, String name, String pwd, int usageCapacity) {
        if (uid == null || name == null || pwd == null) {
            throw new InputNullParameterException();
        }

        if (usageCapacity < 0) {
            throw new MustBePositiveNumberException();
        }

        this.uid = uid;
        this.name = name;
        this.pwd = pwd;
        this.usageCapacity = usageCapacity;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getPwd() {
        return pwd;
    }

    public int getUsageCapacity() {
        return usageCapacity;
    }
}
