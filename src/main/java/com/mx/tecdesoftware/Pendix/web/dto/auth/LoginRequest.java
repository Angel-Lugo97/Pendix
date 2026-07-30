package com.mx.tecdesoftware.Pendix.web.dto.auth;

import com.fasterxml.jackson.annotation.JsonAlias;

public class LoginRequest {

    @JsonAlias("correo")
    private String email;

    @JsonAlias({"contrasena", "contraseña"})
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
