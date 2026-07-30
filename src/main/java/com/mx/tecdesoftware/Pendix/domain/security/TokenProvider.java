package com.mx.tecdesoftware.Pendix.domain.security;

public interface TokenProvider {

    String generateToken(String subject);
}
