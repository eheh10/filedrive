package com.table;

import com.dto.UserDto;
import com.exception.AlreadyRegisteredException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserTable {
    private static final Map<String,UserDto> USERS = new HashMap();

    public UserTable() {
        UserDto userDto = UserDto.builder()
                .id("asdf")
                .pwd("asdf")
                .build();
        USERS.put(userDto.getId(),userDto);
    }

    private boolean IsRegisteredId(String id) {
        return USERS.containsKey(id);
    }

    public void register(UserDto userDto) {
        if (IsRegisteredId(userDto.getId())) {
            throw new AlreadyRegisteredException();
        }
        USERS.put(userDto.getId(), userDto);
    }

    public boolean IsUnregisteredUser(UserDto userDto) {
        String id = userDto.getId();

        if (!IsRegisteredId(id)) {
            return true;
        }

        UserDto registeredUser = USERS.get(id);

        return !Objects.equals(userDto,registeredUser);
    }
}
