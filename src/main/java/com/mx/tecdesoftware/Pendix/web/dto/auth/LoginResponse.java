package com.mx.tecdesoftware.Pendix.web.dto.auth;

public record LoginResponse(
        String token,
        String tokenType
) {
}
