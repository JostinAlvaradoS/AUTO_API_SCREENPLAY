package com.sofka.automation.utils;

import net.serenitybdd.core.Serenity;

public class SessionManager {
    public static final String EVENT_ID = "eventId";
    public static final String EVENT_RESPONSE = "eventResponse";

    public static <T> void set(String key, T value) {
        Serenity.setSessionVariable(key).to(value);
    }

    public static <T> T get(String key) {
        return Serenity.sessionVariableCalled(key);
    }

    private SessionManager() {
        // Utility class
    }
}
