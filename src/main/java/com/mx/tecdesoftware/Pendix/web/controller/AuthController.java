package com.mx.tecdesoftware.Pendix.web.controller;

import com.mx.tecdesoftware.Pendix.domain.service.AuthService;
import com.mx.tecdesoftware.Pendix.web.dto.auth.AuthErrorResponse;
import com.mx.tecdesoftware.Pendix.web.dto.auth.LoginRequest;
import com.mx.tecdesoftware.Pendix.web.dto.auth.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Iniciar sesión",
            description = "Valida correo y contraseña y devuelve un JWT",
            security = {}
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {
        String token = authService.login(
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(
                new LoginResponse(token, "Bearer")
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<AuthErrorResponse> handleBadCredentials(
            BadCredentialsException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        new AuthErrorResponse(
                                exception.getMessage()
                        )
                );
    }
}
