BEGIN;

-- Limpiar la información anterior y reiniciar los ID.
TRUNCATE TABLE tareas, proyectos, usuarios
RESTART IDENTITY CASCADE;

-- =====================================================
-- USUARIOS
-- =====================================================

INSERT INTO usuarios (nombre, correo, estado)
VALUES
    ('Ángel Lugo', 'angel.lugo@pendix.com', true),
    ('María Hernández', 'maria.hernandez@pendix.com', true),
    ('Carlos Gaxiola', 'carlos.gaxiola@pendix.com', true),
    ('Laura Martínez', 'laura.martinez@pendix.com', true),
    ('Roberto Díaz', 'roberto.diaz@pendix.com', false);

-- =====================================================
-- PROYECTOS
-- =====================================================

INSERT INTO proyectos (
    id_usuario_propietario,
    nombre,
    descripcion,
    fecha_inicio,
    fecha_limite,
    estado
)
VALUES
(
    1,
    'Desarrollo de Pendix',
    'Aplicación para administrar proyectos, tareas y recordatorios.',
    '2026-07-01 08:00:00',
    '2026-09-30 23:59:00',
    'EN_PROGRESO'
),
(
    1,
    'Monografía de formación dual',
    'Organización y seguimiento de los capítulos de la monografía.',
    '2026-06-15 09:00:00',
    '2026-08-15 23:59:00',
    'EN_PROGRESO'
),
(
    2,
    'Portal de citas médicas',
    'Sistema web para registrar pacientes, médicos y citas.',
    '2026-07-10 10:00:00',
    '2026-10-20 18:00:00',
    'PENDIENTE'
),
(
    3,
    'Control financiero IEEE',
    'Control de ingresos y egresos de la rama estudiantil.',
    '2026-07-12 12:00:00',
    '2026-12-15 18:00:00',
    'EN_PROGRESO'
),
(
    4,
    'Proyecto temporal',
    'Proyecto creado específicamente para probar las eliminaciones.',
    '2026-07-20 08:00:00',
    '2026-08-20 18:00:00',
    'PENDIENTE'
);

-- =====================================================
-- TAREAS
-- =====================================================

INSERT INTO tareas (
    id_proyecto,
    id_usuario_asignado,
    titulo,
    descripcion,
    fecha_creacion,
    fecha_limite,
    prioridad,
    estado
)
VALUES
(
    1,
    1,
    'Crear modelo de dominio',
    'Crear las clases Project y Task sin anotaciones de JPA.',
    '2026-07-01 08:30:00',
    '2026-07-05 23:59:00',
    'ALTA',
    'COMPLETADA'
),
(
    1,
    2,
    'Implementar mapeadores',
    'Configurar ProjectMapper y TaskMapper utilizando MapStruct.',
    '2026-07-05 09:00:00',
    '2026-07-12 23:59:00',
    'ALTA',
    'COMPLETADA'
),
(
    1,
    1,
    'Crear servicios',
    'Implementar ProjectService y TaskService.',
    '2026-07-12 10:00:00',
    '2026-07-20 23:59:00',
    'MEDIA',
    'COMPLETADA'
),
(
    1,
    3,
    'Crear controladores REST',
    'Exponer los endpoints de proyectos y tareas.',
    '2026-07-20 10:00:00',
    '2026-07-30 23:59:00',
    'ALTA',
    'EN_PROGRESO'
),
(
    1,
    2,
    'Documentar API con Swagger',
    'Agregar documentación OpenAPI a los endpoints.',
    '2026-07-25 11:00:00',
    '2026-08-05 23:59:00',
    'MEDIA',
    'PENDIENTE'
),
(
    2,
    1,
    'Revisar capítulo 3',
    'Revisar el diagnóstico, la propuesta y la ejecución.',
    '2026-07-15 17:00:00',
    '2026-07-30 23:59:00',
    'ALTA',
    'EN_PROGRESO'
),
(
    2,
    4,
    'Redactar conclusiones',
    'Preparar las conclusiones generales del capítulo 4.',
    '2026-07-20 17:00:00',
    '2026-08-10 23:59:00',
    'MEDIA',
    'PENDIENTE'
),
(
    3,
    2,
    'Diseñar base de datos',
    'Crear las tablas de pacientes, médicos y citas.',
    '2026-07-11 08:00:00',
    '2026-07-25 23:59:00',
    'ALTA',
    'COMPLETADA'
),
(
    3,
    3,
    'Crear endpoints de citas',
    'Implementar las operaciones REST para administrar citas.',
    '2026-07-18 08:00:00',
    '2026-08-15 23:59:00',
    'ALTA',
    'EN_PROGRESO'
),
(
    4,
    3,
    'Registrar ingresos',
    'Registrar los ingresos obtenidos por eventos y patrocinios.',
    '2026-07-13 12:00:00',
    '2026-08-15 23:59:00',
    'MEDIA',
    'PENDIENTE'
),
(
    4,
    4,
    'Registrar egresos',
    'Registrar los gastos realizados por la mesa directiva.',
    '2026-07-13 12:30:00',
    '2026-08-15 23:59:00',
    'MEDIA',
    'PENDIENTE'
),
(
    5,
    4,
    'Tarea temporal',
    'Esta tarea será utilizada para probar DELETE.',
    '2026-07-25 08:00:00',
    '2026-08-01 23:59:00',
    'BAJA',
    'PENDIENTE'
);

COMMIT;

-- =====================================================
-- VERIFICACIÓN
-- =====================================================

SELECT * FROM usuarios ORDER BY id_usuario;
SELECT * FROM proyectos ORDER BY id_proyecto;
SELECT * FROM tareas ORDER BY id_tarea;
