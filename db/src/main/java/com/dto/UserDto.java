package com.dto;

import com.exception.InputNullParameterException;
import com.exception.MustBePositiveNumberException;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class UserDto {
    private final int num;
    private final String id;
    private final String pwd;
    private final int usageCapacity;

    private UserDto(int num, String id, String pwd, int usageCapacity) {
        if (id == null || pwd == null) {
            throw new InputNullParameterException();
        }

        if (usageCapacity < 0) {
            throw new MustBePositiveNumberException();
        }

        this.num = num;
        this.id = id;
        this.pwd = pwd;
        this.usageCapacity = usageCapacity;
    }

    public int getNum() {
        return num;
    }

    public String getId() {
        return id;
    }

    public String getPwd() {
        return pwd;
    }

    public int getUsageCapacity() {
        return usageCapacity;
    }
}
