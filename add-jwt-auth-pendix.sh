#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(pwd)"
BASE_PACKAGE_DIR="src/main/java/com/mx/tecdesoftware/Pendix"
USUARIO_FILE="$BASE_PACKAGE_DIR/persistence/entity/Usuario.java"

if [[ ! -f "build.gradle" || ! -f "gradlew" || ! -d "$BASE_PACKAGE_DIR" ]]; then
  echo "ERROR: ejecuta este script desde la raíz de Pendix."
  echo "Ruta esperada: la carpeta que contiene build.gradle, gradlew y src/."
  exit 1
fi

if [[ ! -f "$USUARIO_FILE" ]]; then
  echo "ERROR: no se encontró $USUARIO_FILE"
  exit 1
fi

echo "Configurando Spring Security y JWT en: $PROJECT_ROOT"

python3 <<'PY'
from pathlib import Path

build_file = Path("build.gradle")
text = build_file.read_text(encoding="utf-8")

anchor = "\timplementation 'org.springframework.boot:spring-boot-starter-data-jpa'\n"
dependencies = (
    "\timplementation 'org.springframework.boot:spring-boot-starter-security'\n"
    "\timplementation 'io.jsonwebtoken:jjwt-api:0.12.6'\n"
    "\truntimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.6'\n"
    "\truntimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.6'\n"
)

if "spring-boot-starter-security" not in text:
    if anchor not in text:
        raise SystemExit("ERROR: no se encontró el punto de inserción en build.gradle")
    text = text.replace(anchor, anchor + dependencies, 1)

build_file.write_text(text, encoding="utf-8")

properties_file = Path("src/main/resources/application.properties")
properties = properties_file.read_text(encoding="utf-8")
properties_to_add = {
    "security.jwt.secret": "${JWT_SECRET:AIFmaNEbHeFtZgnYINZTf2TPWeCz1VF6KhlHyku3cbw=}",
    "security.jwt.expiration-ms": "3600000",
}

if not properties.endswith("\n"):
    properties += "\n"

for key, value in properties_to_add.items():
    if not any(line.strip().startswith(key + "=") for line in properties.splitlines()):
        properties += f"{key}={value}\n"

properties_file.write_text(properties, encoding="utf-8")

usuario_file = Path("src/main/java/com/mx/tecdesoftware/Pendix/persistence/entity/Usuario.java")
usuario = usuario_file.read_text(encoding="utf-8")

if "private String contrasena;" not in usuario:
    field_anchor = "    @Column(nullable = false, unique = true)\n    private String correo;\n"
    field_replacement = (
        field_anchor
        + "\n"
        + "    @Column(nullable = false, length = 60)\n"
        + "    private String contrasena;\n"
    )
    if field_anchor not in usuario:
        raise SystemExit("ERROR: no se encontró el campo correo en Usuario.java")
    usuario = usuario.replace(field_anchor, field_replacement, 1)

if "public String getContrasena()" not in usuario:
    method_anchor = (
        "    public void setCorreo(String correo) {\n"
        "        this.correo = correo;\n"
        "    }\n"
    )
    methods = (
        method_anchor
        + "\n"
        + "    public String getContrasena() {\n"
        + "        return contrasena;\n"
        + "    }\n\n"
        + "    public void setContrasena(String contrasena) {\n"
        + "        this.contrasena = contrasena;\n"
        + "    }\n"
    )
    if method_anchor not in usuario:
        raise SystemExit("ERROR: no se encontró setCorreo en Usuario.java")
    usuario = usuario.replace(method_anchor, methods, 1)

usuario_file.write_text(usuario, encoding="utf-8")

seed_file = Path("seed-pendix.sql")
if seed_file.exists():
    seed = seed_file.read_text(encoding="utf-8")
    hash_value = "$2a$10$nkNvamAqeHUoDdonizTvyOA7byMnC5F/DXXjA.nzCvGiD1q5Lprhy"

    seed = seed.replace(
        "INSERT INTO usuarios (nombre, correo, estado)",
        "INSERT INTO usuarios (nombre, correo, contrasena, estado)",
    )

    known_users = [
        ("Ángel Lugo", "angel.lugo@pendix.com", "true"),
        ("María Hernández", "maria.hernandez@pendix.com", "true"),
        ("Carlos Gaxiola", "carlos.gaxiola@pendix.com", "true"),
        ("Laura Martínez", "laura.martinez@pendix.com", "true"),
        ("Roberto Díaz", "roberto.diaz@pendix.com", "false"),
    ]

    for name, email, state in known_users:
        old = f"('{name}', '{email}', {state})"
        new = f"('{name}', '{email}', '{hash_value}', {state})"
        if old in seed:
            seed = seed.replace(old, new)

    seed_file.write_text(seed, encoding="utf-8")
PY

mkdir -p \
  "$BASE_PACKAGE_DIR/domain/auth" \
  "$BASE_PACKAGE_DIR/domain/security" \
  "$BASE_PACKAGE_DIR/config/security" \
  "$BASE_PACKAGE_DIR/web/dto/auth"

cat > "$BASE_PACKAGE_DIR/domain/auth/UserCredentials.java" <<'JAVA'
package com.mx.tecdesoftware.Pendix.domain.auth;

public record UserCredentials(
        String email,
        String passwordHash,
        boolean active
) {
}
JAVA

cat > "$BASE_PACKAGE_DIR/domain/security/TokenProvider.java" <<'JAVA'
package com.mx.tecdesoftware.Pendix.domain.security;

public interface TokenProvider {

    String generateToken(String subject);
}
JAVA

cat > "$BASE_PACKAGE_DIR/domain/repository/UserAuthRepository.java" <<'JAVA'
package com.mx.tecdesoftware.Pendix.domain.repository;

import com.mx.tecdesoftware.Pendix.domain.auth.UserCredentials;

import java.util.Optional;

public interface UserAuthRepository {

    Optional<UserCredentials> findByEmail(String email);
}
JAVA

cat > "$BASE_PACKAGE_DIR/persistence/crud/UsuarioCrudRepository.java" <<'JAVA'
package com.mx.tecdesoftware.Pendix.persistence.crud;

import com.mx.tecdesoftware.Pendix.persistence.entity.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioCrudRepository
        extends CrudRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreoIgnoreCase(String correo);
}
JAVA

cat > "$BASE_PACKAGE_DIR/persistence/repository/UserAuthRepositoryImpl.java" <<'JAVA'
package com.mx.tecdesoftware.Pendix.persistence.repository;

import com.mx.tecdesoftware.Pendix.domain.auth.UserCredentials;
import com.mx.tecdesoftware.Pendix.domain.repository.UserAuthRepository;
import com.mx.tecdesoftware.Pendix.persistence.crud.UsuarioCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class UserAuthRepositoryImpl implements UserAuthRepository {

    private final UsuarioCrudRepository usuarioCrudRepository;

    public UserAuthRepositoryImpl(
            UsuarioCrudRepository usuarioCrudRepository
    ) {
        this.usuarioCrudRepository = usuarioCrudRepository;
    }

    @Override
    public Optional<UserCredentials> findByEmail(String email) {
        return usuarioCrudRepository
                .findByCorreoIgnoreCase(email)
                .map(usuario -> new UserCredentials(
                        usuario.getCorreo(),
                        usuario.getContrasena(),
                        Boolean.TRUE.equals(usuario.getEstado())
                ));
    }
}
JAVA

cat > "$BASE_PACKAGE_DIR/config/security/JwtUtil.java" <<'JAVA'
package com.mx.tecdesoftware.Pendix.config.security;

import com.mx.tecdesoftware.Pendix.domain.security.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil implements TokenProvider {

    private static final int MINIMUM_HS256_KEY_BYTES = 32;

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtUtil(
            @Value("${security.jwt.secret}") String base64Secret,
            @Value("${security.jwt.expiration-ms}") long expirationMs
    ) {
        byte[] keyBytes;

        try {
            keyBytes = Decoders.BASE64.decode(base64Secret);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException(
                    "security.jwt.secret debe ser una cadena Base64 válida",
                    exception
            );
        }

        if (keyBytes.length < MINIMUM_HS256_KEY_BYTES) {
            throw new IllegalStateException(
                    "security.jwt.secret debe representar al menos 32 bytes"
            );
        }

        if (expirationMs <= 0) {
            throw new IllegalStateException(
                    "security.jwt.expiration-ms debe ser mayor que cero"
            );
        }

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    @Override
    public String generateToken(String subject) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(expirationMs);

        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public String extractSubject(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            String subject = claims.getSubject();

            return subject != null && !subject.isBlank();
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
JAVA

cat > "$BASE_PACKAGE_DIR/config/security/JwtFilter.java" <<'JAVA'
package com.mx.tecdesoftware.Pendix.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(
                HttpHeaders.AUTHORIZATION
        );

        if (authorizationHeader == null
                || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader
                .substring(BEARER_PREFIX.length())
                .trim();

        if (!token.isEmpty()
                && SecurityContextHolder.getContext().getAuthentication() == null
                && jwtUtil.isTokenValid(token)) {
            String email = jwtUtil.extractSubject(token);

            UsernamePasswordAuthenticationToken authentication =
                    UsernamePasswordAuthenticationToken.authenticated(
                            email,
                            null,
                            List.of(
                                    new SimpleGrantedAuthority("ROLE_USER")
                            )
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
JAVA

cat > "$BASE_PACKAGE_DIR/domain/service/AuthService.java" <<'JAVA'
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
JAVA

cat > "$BASE_PACKAGE_DIR/config/security/SecurityConfig.java" <<'JAVA'
package com.mx.tecdesoftware.Pendix.config.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/auth/login",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/**",
                                "/error"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(
                                (request, response, exception) -> {
                                    response.setStatus(
                                            HttpServletResponse.SC_UNAUTHORIZED
                                    );
                                    response.setContentType(
                                            MediaType.APPLICATION_JSON_VALUE
                                    );
                                    response.getWriter().write(
                                            "{\"error\":\"Authentication required\"}"
                                    );
                                }
                        )
                )
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }
}
JAVA

cat > "$BASE_PACKAGE_DIR/config/security/OpenApiSecurityConfig.java" <<'JAVA'
package com.mx.tecdesoftware.Pendix.config.security;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiSecurityConfig {

    public static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI pendixOpenApi() {
        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .components(
                        new Components().addSecuritySchemes(
                                SECURITY_SCHEME_NAME,
                                bearerScheme
                        )
                )
                .addSecurityItem(
                        new SecurityRequirement().addList(
                                SECURITY_SCHEME_NAME
                        )
                );
    }
}
JAVA

cat > "$BASE_PACKAGE_DIR/web/dto/auth/LoginRequest.java" <<'JAVA'
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
JAVA

cat > "$BASE_PACKAGE_DIR/web/dto/auth/LoginResponse.java" <<'JAVA'
package com.mx.tecdesoftware.Pendix.web.dto.auth;

public record LoginResponse(
        String token,
        String tokenType
) {
}
JAVA

cat > "$BASE_PACKAGE_DIR/web/dto/auth/AuthErrorResponse.java" <<'JAVA'
package com.mx.tecdesoftware.Pendix.web.dto.auth;

public record AuthErrorResponse(
        String error
) {
}
JAVA

cat > "$BASE_PACKAGE_DIR/web/controller/AuthController.java" <<'JAVA'
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
JAVA

cat > migration-add-jwt-auth.sql <<'SQL'
BEGIN;

ALTER TABLE usuarios
    ADD COLUMN IF NOT EXISTS contrasena VARCHAR(60);

-- Hash BCrypt válido para la contraseña de prueba: 1234
UPDATE usuarios
SET contrasena = '$2a$10$nkNvamAqeHUoDdonizTvyOA7byMnC5F/DXXjA.nzCvGiD1q5Lprhy';

ALTER TABLE usuarios
    ALTER COLUMN contrasena SET NOT NULL;

COMMIT;

SELECT
    id_usuario,
    nombre,
    correo,
    estado,
    LENGTH(contrasena) AS longitud_hash
FROM usuarios
ORDER BY id_usuario;
SQL

cat > test-jwt-auth-pendix.sh <<'SCRIPT_TEST'
#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${1:-http://localhost:5018}"
EMAIL="${2:-angel.lugo@pendix.com}"
PASSWORD="${3:-1234}"
TMP_DIR="$(mktemp -d)"
trap 'rm -rf "$TMP_DIR"' EXIT

assert_status() {
  local expected="$1"
  local actual="$2"
  local description="$3"

  if [[ "$actual" != "$expected" ]]; then
    echo "ERROR: $description"
    echo "Esperado: HTTP $expected"
    echo "Recibido: HTTP $actual"
    exit 1
  fi

  echo "OK: $description (HTTP $actual)"
}

echo "Probando Pendix JWT en $BASE_URL"

STATUS=$(curl -sS -o "$TMP_DIR/no-token.json" -w '%{http_code}' \
  "$BASE_URL/projects")
assert_status "401" "$STATUS" "endpoint protegido rechaza petición sin token"

STATUS=$(curl -sS -o "$TMP_DIR/bad-login.json" -w '%{http_code}' \
  -X POST "$BASE_URL/auth/login" \
  -H 'Content-Type: application/json' \
  -d "{\"email\":\"$EMAIL\",\"password\":\"incorrecta\"}")
assert_status "401" "$STATUS" "login rechaza contraseña incorrecta"

STATUS=$(curl -sS -o "$TMP_DIR/login.json" -w '%{http_code}' \
  -X POST "$BASE_URL/auth/login" \
  -H 'Content-Type: application/json' \
  -d "{\"email\":\"$EMAIL\",\"password\":\"$PASSWORD\"}")
assert_status "200" "$STATUS" "login devuelve un token"

TOKEN=$(python3 - "$TMP_DIR/login.json" <<'PY'
import json
import sys

with open(sys.argv[1], encoding="utf-8") as file:
    payload = json.load(file)

token = payload.get("token")
if not token:
    raise SystemExit("ERROR: la respuesta de login no contiene token")

print(token)
PY
)

STATUS=$(curl -sS -o "$TMP_DIR/projects.json" -w '%{http_code}' \
  "$BASE_URL/projects" \
  -H "Authorization: Bearer $TOKEN")
assert_status "200" "$STATUS" "token permite consultar proyectos"

STATUS=$(curl -sS -o "$TMP_DIR/tasks.json" -w '%{http_code}' \
  "$BASE_URL/tasks" \
  -H "Authorization: Bearer $TOKEN")
assert_status "200" "$STATUS" "token permite consultar tareas"

STATUS=$(curl -sS -o "$TMP_DIR/reminders.json" -w '%{http_code}' \
  "$BASE_URL/reminders" \
  -H "Authorization: Bearer $TOKEN")
assert_status "200" "$STATUS" "token permite consultar recordatorios"

STATUS=$(curl -sS -o "$TMP_DIR/swagger.json" -w '%{http_code}' \
  "$BASE_URL/v3/api-docs")
assert_status "200" "$STATUS" "documentación OpenAPI permanece pública"

INVALID_TOKEN="${TOKEN%?}x"
STATUS=$(curl -sS -o "$TMP_DIR/invalid-token.json" -w '%{http_code}' \
  "$BASE_URL/projects" \
  -H "Authorization: Bearer $INVALID_TOKEN")
assert_status "401" "$STATUS" "token alterado es rechazado"

echo
echo "TOKEN JWT:"
echo "$TOKEN"
echo
echo "OK: autenticación JWT y endpoints protegidos funcionan correctamente."
SCRIPT_TEST

chmod +x test-jwt-auth-pendix.sh

echo
echo "Implementación creada correctamente."
echo "Archivos principales:"
echo "  - migration-add-jwt-auth.sql"
echo "  - test-jwt-auth-pendix.sh"
echo "  - clases de autenticación, JWT y Spring Security"
echo
echo "Siguiente paso: revisa git status y realiza los commits indicados."
