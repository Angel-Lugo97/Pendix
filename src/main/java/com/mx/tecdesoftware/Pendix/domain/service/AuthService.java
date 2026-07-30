package com.mx.tecdesoftware.Pendix.domain.service;

import com.mx.tecdesoftware.Pendix.domain.auth.UserCredentials;
import com.mx.tecdesoftware.Pendix.domain.repository.UserAuthRepository;
import com.mx.tecdesoftware.Pendix.domain.security.TokenProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {

    private final UserAuthRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public AuthService(
            UserAuthRepository userAuthRepository,
            PasswordEncoder passwordEncoder,
            TokenProvider tokenProvider
    ) {
        this.userAuthRepository = userAuthRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public String login(String email, String password) {
        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {
            throw invalidCredentials();
        }

        String normalizedEmail = email
                .trim()
                .toLowerCase(Locale.ROOT);

        UserCredentials user = userAuthRepository
                .findByEmail(normalizedEmail)
                .filter(UserCredentials::active)
                .orElseThrow(this::invalidCredentials);

        if (!passwordEncoder.matches(
                password,
                user.passwordHash()
        )) {
            throw invalidCredentials();
        }

        return tokenProvider.generateToken(user.email());
    }

    private BadCredentialsException invalidCredentials() {
        return new BadCredentialsException(
                "Correo o contraseña incorrectos"
        );
    }
}
