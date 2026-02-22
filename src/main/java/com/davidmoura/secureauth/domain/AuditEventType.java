package com.davidmoura.secureauth.domain;

public enum AuditEventType {
    LOGIN_SUCCESS,
    LOGIN_FAILURE,
    LOGOUT,
    TOKEN_REFRESHED,
    TOKEN_REUSE_DETECTED,
    USER_REGISTERED,
    ROLE_GRANTED,
    ROLE_REVOKED
}