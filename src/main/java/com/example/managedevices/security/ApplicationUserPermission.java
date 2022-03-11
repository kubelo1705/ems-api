package com.example.managedevices.security;

public enum ApplicationUserPermission {
    READ("read"),
    WRITE("write");

    private final String permission;

    ApplicationUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
