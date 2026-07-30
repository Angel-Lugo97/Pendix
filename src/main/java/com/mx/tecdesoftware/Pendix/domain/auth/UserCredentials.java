package com.mx.tecdesoftware.Pendix.domain.auth;

public record UserCredentials(
        String email,
        String passwordHash,
        boolean active
) {
}
