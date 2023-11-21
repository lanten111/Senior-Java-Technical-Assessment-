package com.makhadoni.customer.service.modules.auth.dto;


import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

public class UserDto implements Serializable {

    @Schema(description = "username", example = "admin")
    private String username;
    @Schema(description = "password", example = "admin")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
