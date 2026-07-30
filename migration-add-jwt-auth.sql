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
