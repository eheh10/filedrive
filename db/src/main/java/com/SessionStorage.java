package com;

import com.exception.InputNullParameterException;
import com.exception.NotFoundSessionException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionStorage {
    public static final String SESSION_ID_NAME = "sessionId";
    private static final Map<String, Cookies> SESSIONS = new HashMap<>();

    public String createSession(Cookies cookies) {
        if (cookies == null) {
            throw new InputNullParameterException();
        }

        String sessionId = UUID.randomUUID().toString();
        SESSIONS.put(sessionId, cookies);

        return sessionId;
    }

    public boolean validSession(String sessionId) {
        if (sessionId == null) {
            throw new InputNullParameterException();
        }
        return SESSIONS.containsKey(sessionId);
    }

    public Cookies getCookie(String sessionId) {
        if (sessionId == null) {
            throw new InputNullParameterException();
        }

        if (!validSession(sessionId)) {
            throw new NotFoundSessionException();
        }

        return SESSIONS.get(sessionId);
    }
}
