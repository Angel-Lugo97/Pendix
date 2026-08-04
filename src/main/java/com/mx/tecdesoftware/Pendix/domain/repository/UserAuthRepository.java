package com.mx.tecdesoftware.Pendix.domain.repository;

import com.mx.tecdesoftware.Pendix.domain.auth.UserCredentials;

import java.util.Optional;

public interface UserAuthRepository {

    Optional<UserCredentials> findByEmail(String email);

    boolean existsById(Integer userId);
}
