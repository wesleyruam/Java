package com.wesleyruam.ticketflow.security;

public enum Permission {
    // User permissions
    CREATE_USER,
    EDIT_USER,
    DELETE_USER,
    VIEW_ALL_USERS,
    
    // Ticket permissions
    CREATE_TICKET,
    EDIT_TICKET,
    DELETE_TICKET,
    VIEW_ALL_TICKETS,
    
    // Comment permissions
    ADD_COMMENT,
    DELETE_COMMENT,
    EDIT_ANY_COMMENT
}