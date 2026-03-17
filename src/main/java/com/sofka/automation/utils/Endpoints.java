package com.sofka.automation.utils;

public class Endpoints {
    public static final String CREATE_EVENT = "/admin/events";
    public static final String GET_EVENT = "/Events/{id}";
    public static final String GET_EVENTS_LIST = "/Events";
    public static final String UPDATE_EVENT = "/admin/events/{id}";
    public static final String DEACTIVATE_EVENT = "/admin/events/{id}/deactivate";

    private Endpoints() {
        // Utility class
    }
}
