package com.sofka.automation.utils;

import net.serenitybdd.core.Serenity;

public class SessionManager {
    public static final String EVENT_ID           = "eventId";
    public static final String EVENT_RESPONSE     = "eventResponse";

    public static final String WAITLIST_EVENT_ID    = "waitlistEventId";
    public static final String WAITLIST_ENTRY_ID    = "waitlistEntryId";
    public static final String WAITLIST_EMAIL       = "waitlistEmail";
    public static final String WAITLIST_SECOND_EMAIL = "waitlistSecondEmail";

    public static <T> void set(String key, T value) {
        Serenity.setSessionVariable(key).to(value);
    }

    public static <T> T get(String key) {
        return Serenity.sessionVariableCalled(key);
    }

    private SessionManager() {
        
    }
}
