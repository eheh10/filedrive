package com.db.dto;

import com.db.exception.InputNullParameterException;
import com.db.exception.MustBePositiveNumberException;
import lombok.Builder;
import lombok.ToString;



@ToString
public class UserDto {
    private final String uid;
    private final String name;
    private final String pwd;
    private final int usageCapacity;
    private final String googleUid;
    private final int version;

    @Builder
    private UserDto(String uid, String name, String pwd, int usageCapacity, String googleUid, int version) {
        if (uid == null || name == null ) {
            throw new InputNullParameterException(
                    "uid: "+uid+"" +
                    "name: "+name+""
            );
        }

        if (usageCapacity < 0 || version < 0) {
            throw new MustBePositiveNumberException(
                    "usageCapacity: "+usageCapacity+"\n" +
                    "version: "+version
            );
        }

        this.uid = uid;
        this.name = name;
        this.pwd = pwd;
        this.usageCapacity = usageCapacity;
        this.googleUid = googleUid;
        this.version = version;
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

    public String getGoogleUid() {
        return googleUid;
    }

    public int getVersion() {
        return version;
    }
}
