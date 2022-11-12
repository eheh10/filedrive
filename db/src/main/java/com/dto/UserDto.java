package com.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class UserDto {
    private final String id;
    private final String pwd;

    public String getId() {
        return id;
    }

    public String getPwd() {
        return pwd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDto userDto = (UserDto) o;

        if (!Objects.equals(id, userDto.id)) return false;
        return Objects.equals(pwd, userDto.pwd);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + pwd.hashCode();
        return result;
    }
}
