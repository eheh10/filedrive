package com.api.dto;

import com.api.exception.InputNullParameterException;
import lombok.Builder;
import lombok.ToString;

@ToString
public class UserInfoDto {
    private final String snsUid;
    private final String snsEmail;

    @Builder
    public UserInfoDto(String snsUid, String snsEmail) {
        if (snsUid==null || snsEmail==null) {
            throw new InputNullParameterException(
                    "snsUid: "+snsUid+"\n"+
                    "snsEmail: "+snsEmail+"\n"
            );
        }
        this.snsUid = snsUid;
        this.snsEmail = snsEmail;
    }

    public String getSnsUid() {
        return snsUid;
    }

    public String getSnsEmail() {
        return snsEmail;
    }
}
