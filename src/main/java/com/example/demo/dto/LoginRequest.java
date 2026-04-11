package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class LoginRequest {

    // Accepts any of these field names from the request body:
    // { "usernameOrEmail": "..." }  ← frontend sends this
    // { "email": "..." }            ← Postman may send this
    // { "username": "..." }         ← also supported
    @JsonAlias({"email", "username", "usernameOrEmail"})
    private String usernameOrEmail;

    private String password;

    public String getUsernameOrEmail() { return usernameOrEmail; }
    public void setUsernameOrEmail(String usernameOrEmail) { this.usernameOrEmail = usernameOrEmail; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
