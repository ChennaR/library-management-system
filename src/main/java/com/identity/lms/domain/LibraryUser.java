package com.identity.lms.domain;

import lombok.Data;

/**
 * Class to hold user details
 */
@Data
public class LibraryUser {
    private String username;
    private String password;
    private Role role;
}
