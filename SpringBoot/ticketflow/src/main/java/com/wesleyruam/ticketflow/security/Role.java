package com.wesleyruam.ticketflow.security;

import java.util.Set;

public enum Role {
    ADMIN(
        Permission.CREATE_USER,
        Permission.EDIT_USER,
        Permission.DELETE_USER,
        Permission.VIEW_ALL_USERS,
        Permission.CREATE_TICKET,
        Permission.EDIT_TICKET,
        Permission.DELETE_TICKET,
        Permission.VIEW_ALL_TICKETS,
        Permission.ADD_COMMENT,
        Permission.DELETE_COMMENT
    ),
    
    MANAGER(
        Permission.CREATE_TICKET,
        Permission.EDIT_TICKET,
        Permission.VIEW_ALL_TICKETS,
        Permission.ADD_COMMENT,
        Permission.DELETE_COMMENT
    ),
    
    USER(
        Permission.CREATE_TICKET,
        Permission.ADD_COMMENT
    );
    
    private final Set<Permission> permissions;
    
    Role(Permission... permissions) {
        this.permissions = Set.of(permissions);
    }
    
    public Set<Permission> getPermissions() {
        return permissions;
    }
    
    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
}