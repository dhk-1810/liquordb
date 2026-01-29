package com.liquordb.enums;

public enum Role {
    ADMIN,
    USER;

    public final String AUTHORITY_PREFIX = "ROLE_";
    public String getAuthority() {
        return AUTHORITY_PREFIX + this.name();
    }

}
