-- =============================================================================
-- ReadNow — Auditoría central en MySQL (triggers)
-- =============================================================================
-- Ejecutar este script en la base usada por la app (read_now_db).
--
-- Formato de cada registro: usuario_app | fecha_hora | tabla_nombre | accion | id_registro
-- La aplicación Spring envía el usuario vía variable de sesión @audit_usuario por conexión.
-- Si no está definida, se guarda 'SISTEMA'.
-- =============================================================================

SET NAMES utf8mb4;

-- -----------------------------------------------------------------------------
-- Tabla única de auditoría
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS auditoria_cambios (
    auditoria_id     BIGINT NOT NULL AUTO_INCREMENT,
    usuario_app      VARCHAR(255) NOT NULL COMMENT 'Email del usuario o SISTEMA',
    fecha_hora       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    tabla_nombre     VARCHAR(128) NOT NULL,
    accion           VARCHAR(16)  NOT NULL COMMENT 'INSERT, UPDATE, DELETE',
    id_registro      VARCHAR(64)  NULL COMMENT 'PK afectada, si aplica',
    PRIMARY KEY (auditoria_id),
    KEY idx_auditoria_tabla_fecha (tabla_nombre, fecha_hora),
    KEY idx_auditoria_usuario_fecha (usuario_app, fecha_hora)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DELIMITER $$

DROP PROCEDURE IF EXISTS sp_registrar_auditoria$$
CREATE PROCEDURE sp_registrar_auditoria (
    IN p_tabla VARCHAR(128),
    IN p_accion VARCHAR(16),
    IN p_id_registro VARCHAR(64)
)
BEGIN
    INSERT INTO auditoria_cambios (usuario_app, fecha_hora, tabla_nombre, accion, id_registro)
    VALUES (
        IFNULL(@audit_usuario, 'SISTEMA'),
        NOW(3),
        p_tabla,
        p_accion,
        p_id_registro
    );
END$$

DELIMITER ;

-- -----------------------------------------------------------------------------
-- Usuarios
-- -----------------------------------------------------------------------------
DROP TRIGGER IF EXISTS tr_usuarios_ai;
CREATE TRIGGER tr_usuarios_ai AFTER INSERT ON usuarios
FOR EACH ROW CALL sp_registrar_auditoria('usuarios', 'INSERT', CAST(NEW.usuario_id AS CHAR));

DROP TRIGGER IF EXISTS tr_usuarios_au;
CREATE TRIGGER tr_usuarios_au AFTER UPDATE ON usuarios
FOR EACH ROW CALL sp_registrar_auditoria('usuarios', 'UPDATE', CAST(NEW.usuario_id AS CHAR));

DROP TRIGGER IF EXISTS tr_usuarios_ad;
CREATE TRIGGER tr_usuarios_ad AFTER DELETE ON usuarios
FOR EACH ROW CALL sp_registrar_auditoria('usuarios', 'DELETE', CAST(OLD.usuario_id AS CHAR));

-- Roles
DROP TRIGGER IF EXISTS tr_roles_ai;
CREATE TRIGGER tr_roles_ai AFTER INSERT ON roles
FOR EACH ROW CALL sp_registrar_auditoria('roles', 'INSERT', CAST(NEW.rol_id AS CHAR));

DROP TRIGGER IF EXISTS tr_roles_au;
CREATE TRIGGER tr_roles_au AFTER UPDATE ON roles
FOR EACH ROW CALL sp_registrar_auditoria('roles', 'UPDATE', CAST(NEW.rol_id AS CHAR));

DROP TRIGGER IF EXISTS tr_roles_ad;
CREATE TRIGGER tr_roles_ad AFTER DELETE ON roles
FOR EACH ROW CALL sp_registrar_auditoria('roles', 'DELETE', CAST(OLD.rol_id AS CHAR));

-- Suscripciones (planes)
DROP TRIGGER IF EXISTS tr_suscripciones_ai;
CREATE TRIGGER tr_suscripciones_ai AFTER INSERT ON suscripciones
FOR EACH ROW CALL sp_registrar_auditoria('suscripciones', 'INSERT', CAST(NEW.suscripcion_id AS CHAR));

DROP TRIGGER IF EXISTS tr_suscripciones_au;
CREATE TRIGGER tr_suscripciones_au AFTER UPDATE ON suscripciones
FOR EACH ROW CALL sp_registrar_auditoria('suscripciones', 'UPDATE', CAST(NEW.suscripcion_id AS CHAR));

DROP TRIGGER IF EXISTS tr_suscripciones_ad;
CREATE TRIGGER tr_suscripciones_ad AFTER DELETE ON suscripciones
FOR EACH ROW CALL sp_registrar_auditoria('suscripciones', 'DELETE', CAST(OLD.suscripcion_id AS CHAR));

-- Recursos
DROP TRIGGER IF EXISTS tr_recursos_ai;
CREATE TRIGGER tr_recursos_ai AFTER INSERT ON recursos
FOR EACH ROW CALL sp_registrar_auditoria('recursos', 'INSERT', CAST(NEW.recurso_id AS CHAR));

DROP TRIGGER IF EXISTS tr_recursos_au;
CREATE TRIGGER tr_recursos_au AFTER UPDATE ON recursos
FOR EACH ROW CALL sp_registrar_auditoria('recursos', 'UPDATE', CAST(NEW.recurso_id AS CHAR));

DROP TRIGGER IF EXISTS tr_recursos_ad;
CREATE TRIGGER tr_recursos_ad AFTER DELETE ON recursos
FOR EACH ROW CALL sp_registrar_auditoria('recursos', 'DELETE', CAST(OLD.recurso_id AS CHAR));

-- Categorías
DROP TRIGGER IF EXISTS tr_categorias_recursos_ai;
CREATE TRIGGER tr_categorias_recursos_ai AFTER INSERT ON categorias_recursos
FOR EACH ROW CALL sp_registrar_auditoria('categorias_recursos', 'INSERT', CAST(NEW.categoria_recurso_id AS CHAR));

DROP TRIGGER IF EXISTS tr_categorias_recursos_au;
CREATE TRIGGER tr_categorias_recursos_au AFTER UPDATE ON categorias_recursos
FOR EACH ROW CALL sp_registrar_auditoria('categorias_recursos', 'UPDATE', CAST(NEW.categoria_recurso_id AS CHAR));

DROP TRIGGER IF EXISTS tr_categorias_recursos_ad;
CREATE TRIGGER tr_categorias_recursos_ad AFTER DELETE ON categorias_recursos
FOR EACH ROW CALL sp_registrar_auditoria('categorias_recursos', 'DELETE', CAST(OLD.categoria_recurso_id AS CHAR));

-- Tipos de recurso
DROP TRIGGER IF EXISTS tr_tipos_recursos_ai;
CREATE TRIGGER tr_tipos_recursos_ai AFTER INSERT ON tipos_recursos
FOR EACH ROW CALL sp_registrar_auditoria('tipos_recursos', 'INSERT', CAST(NEW.tipo_recurso_id AS CHAR));

DROP TRIGGER IF EXISTS tr_tipos_recursos_au;
CREATE TRIGGER tr_tipos_recursos_au AFTER UPDATE ON tipos_recursos
FOR EACH ROW CALL sp_registrar_auditoria('tipos_recursos', 'UPDATE', CAST(NEW.tipo_recurso_id AS CHAR));

DROP TRIGGER IF EXISTS tr_tipos_recursos_ad;
CREATE TRIGGER tr_tipos_recursos_ad AFTER DELETE ON tipos_recursos
FOR EACH ROW CALL sp_registrar_auditoria('tipos_recursos', 'DELETE', CAST(OLD.tipo_recurso_id AS CHAR));

-- Calificaciones
DROP TRIGGER IF EXISTS tr_calificaciones_ai;
CREATE TRIGGER tr_calificaciones_ai AFTER INSERT ON calificaciones
FOR EACH ROW CALL sp_registrar_auditoria('calificaciones', 'INSERT', CAST(NEW.calificacion_id AS CHAR));

DROP TRIGGER IF EXISTS tr_calificaciones_au;
CREATE TRIGGER tr_calificaciones_au AFTER UPDATE ON calificaciones
FOR EACH ROW CALL sp_registrar_auditoria('calificaciones', 'UPDATE', CAST(NEW.calificacion_id AS CHAR));

DROP TRIGGER IF EXISTS tr_calificaciones_ad;
CREATE TRIGGER tr_calificaciones_ad AFTER DELETE ON calificaciones
FOR EACH ROW CALL sp_registrar_auditoria('calificaciones', 'DELETE', CAST(OLD.calificacion_id AS CHAR));

-- Reseñas
DROP TRIGGER IF EXISTS tr_resenias_ai;
CREATE TRIGGER tr_resenias_ai AFTER INSERT ON resenias
FOR EACH ROW CALL sp_registrar_auditoria('resenias', 'INSERT', CAST(NEW.resenia_id AS CHAR));

DROP TRIGGER IF EXISTS tr_resenias_au;
CREATE TRIGGER tr_resenias_au AFTER UPDATE ON resenias
FOR EACH ROW CALL sp_registrar_auditoria('resenias', 'UPDATE', CAST(NEW.resenia_id AS CHAR));

DROP TRIGGER IF EXISTS tr_resenias_ad;
CREATE TRIGGER tr_resenias_ad AFTER DELETE ON resenias
FOR EACH ROW CALL sp_registrar_auditoria('resenias', 'DELETE', CAST(OLD.resenia_id AS CHAR));

-- Favoritos
DROP TRIGGER IF EXISTS tr_favoritos_ai;
CREATE TRIGGER tr_favoritos_ai AFTER INSERT ON favoritos
FOR EACH ROW CALL sp_registrar_auditoria('favoritos', 'INSERT', CAST(NEW.favorito_id AS CHAR));

DROP TRIGGER IF EXISTS tr_favoritos_au;
CREATE TRIGGER tr_favoritos_au AFTER UPDATE ON favoritos
FOR EACH ROW CALL sp_registrar_auditoria('favoritos', 'UPDATE', CAST(NEW.favorito_id AS CHAR));

DROP TRIGGER IF EXISTS tr_favoritos_ad;
CREATE TRIGGER tr_favoritos_ad AFTER DELETE ON favoritos
FOR EACH ROW CALL sp_registrar_auditoria('favoritos', 'DELETE', CAST(OLD.favorito_id AS CHAR));

-- Historias de lectura
DROP TRIGGER IF EXISTS tr_historias_lecturas_ai;
CREATE TRIGGER tr_historias_lecturas_ai AFTER INSERT ON historias_lecturas
FOR EACH ROW CALL sp_registrar_auditoria('historias_lecturas', 'INSERT', CAST(NEW.historia_lectura_id AS CHAR));

DROP TRIGGER IF EXISTS tr_historias_lecturas_au;
CREATE TRIGGER tr_historias_lecturas_au AFTER UPDATE ON historias_lecturas
FOR EACH ROW CALL sp_registrar_auditoria('historias_lecturas', 'UPDATE', CAST(NEW.historia_lectura_id AS CHAR));

DROP TRIGGER IF EXISTS tr_historias_lecturas_ad;
CREATE TRIGGER tr_historias_lecturas_ad AFTER DELETE ON historias_lecturas
FOR EACH ROW CALL sp_registrar_auditoria('historias_lecturas', 'DELETE', CAST(OLD.historia_lectura_id AS CHAR));

-- Consultas útiles:
-- SELECT * FROM auditoria_cambios ORDER BY fecha_hora DESC LIMIT 100;
-- SELECT * FROM auditoria_cambios WHERE usuario_app = 'correo@ejemplo.com' ORDER BY fecha_hora DESC;
