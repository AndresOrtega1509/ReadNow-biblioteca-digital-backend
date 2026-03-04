package co.edu.uniquindio.read_now.repository;

import co.edu.uniquindio.read_now.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<Usuario> findByUltimoAccesoBeforeAndActivoAndRolNombre(
            LocalDateTime fecha, String activo, String rolNombre);

    /** Suscripciones activas: considera finSuscripcionAt si existe, sino finSuscripcion. */
    @Query("SELECT COUNT(u) FROM Usuario u WHERE " +
            "(u.finSuscripcionAt IS NOT NULL AND u.finSuscripcionAt > :ahora) OR " +
            "(u.finSuscripcionAt IS NULL AND u.finSuscripcion IS NOT NULL AND u.finSuscripcion >= :hoy)")
    long countSuscripcionesActivas(@Param("hoy") LocalDate hoy, @Param("ahora") LocalDateTime ahora);

    /** Suscripciones vencidas: considera finSuscripcionAt si existe, sino finSuscripcion. */
    @Query("SELECT COUNT(u) FROM Usuario u WHERE " +
            "(u.finSuscripcionAt IS NOT NULL AND u.finSuscripcionAt <= :ahora) OR " +
            "(u.finSuscripcionAt IS NULL AND u.finSuscripcion IS NOT NULL AND u.finSuscripcion < :hoy)")
    long countSuscripcionesVencidas(@Param("hoy") LocalDate hoy, @Param("ahora") LocalDateTime ahora);

    /** Usuarios con suscripción activa (finSuscripcion >= hoy). */
    long countByFinSuscripcionGreaterThanEqual(LocalDate hoy);

    /** Usuarios con suscripción vencida (finSuscripcion no nulo y < hoy). */
    long countByFinSuscripcionIsNotNullAndFinSuscripcionBefore(LocalDate hoy);

    /** Usuarios con plan específico y suscripción activa. */
    long countBySuscripcionSuscripcionIdAndFinSuscripcionGreaterThanEqual(Long suscripcionId, LocalDate hoy);

    /** Usuarios con plan específico y suscripción vencida. */
    long countBySuscripcionSuscripcionIdAndFinSuscripcionIsNotNullAndFinSuscripcionBefore(Long suscripcionId, LocalDate hoy);

    /** Usuarios sin plan asignado (suscripcion null) con suscripción activa (prueba gratuita). */
    long countBySuscripcionIsNullAndFinSuscripcionGreaterThanEqual(LocalDate hoy);

    /** Usuarios sin plan asignado con suscripción vencida. */
    long countBySuscripcionIsNullAndFinSuscripcionIsNotNullAndFinSuscripcionBefore(LocalDate hoy);

    /** Lectores con suscripción activa (para recordatorios de vencimiento). */
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = 'LECTOR' AND u.activo = 'S' AND (" +
            "(u.finSuscripcionAt IS NOT NULL AND u.finSuscripcionAt > :ahora) OR " +
            "(u.finSuscripcionAt IS NULL AND u.finSuscripcion IS NOT NULL AND u.finSuscripcion >= :hoy))")
    List<Usuario> findLectoresConSuscripcionActiva(@Param("hoy") LocalDate hoy, @Param("ahora") LocalDateTime ahora);

    /** Lectores con suscripción vencida que aún no han sido notificados por correo. */
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = 'LECTOR' " +
            "AND (u.suscripcionVencidaNotificada IS NULL OR u.suscripcionVencidaNotificada = false) " +
            "AND ((u.finSuscripcionAt IS NOT NULL AND u.finSuscripcionAt <= :ahora) OR " +
            "(u.finSuscripcionAt IS NULL AND u.finSuscripcion IS NOT NULL AND u.finSuscripcion < :hoy))")
    List<Usuario> findLectoresConSuscripcionVencidaNoNotificados(@Param("hoy") LocalDate hoy, @Param("ahora") LocalDateTime ahora);
}
