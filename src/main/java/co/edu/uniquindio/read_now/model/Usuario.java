package co.edu.uniquindio.read_now.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long usuarioId;

    private String nombre;
    private String apellido;

    @Column(unique = true)
    private String email;

    private String telefono;
    private String username;
    private String password;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    private String activo;

    @Column(name = "inicio_suscripcion")
    private LocalDate inicioSuscripcion;

    @Column(name = "fin_suscripcion")
    private LocalDate finSuscripcion;

    /** Fecha/hora exacta de vencimiento (opcional). Si está presente, se usa en lugar de finSuscripcion para la verificación. */
    @Column(name = "fin_suscripcion_at")
    private LocalDateTime finSuscripcionAt;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    /** Indica si ya se notificó por correo que la suscripción venció (para no reenviar). */
    @Column(name = "suscripcion_vencida_notificada")
    private Boolean suscripcionVencidaNotificada;

    /** Indica si ya se envió el recordatorio de "5 días restantes". */
    @Column(name = "recordatorio_5_dias_enviado")
    private Boolean recordatorio5DiasEnviado;

    /** Indica si ya se envió el recordatorio de "1 día restante". */
    @Column(name = "recordatorio_1_dia_enviado")
    private Boolean recordatorio1DiaEnviado;

    /** True si ya se envió el único correo de recordatorio por inactividad (3+ días sin acceso). */
    @Column(name = "recordatorio_inactividad_enviado")
    private Boolean recordatorioInactividadEnviado;

    /** Verificación en dos pasos (2FA): true = activa, false = desactivada. null = usa configuración global. */
    @Column(name = "two_factor_activo")
    private Boolean twoFactorActivo;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Rol rol;

    @ManyToOne
    @JoinColumn(name = "suscripcion_id")
    private Suscripcion suscripcion;

    /** Si true, el usuario ya usó la prueba gratuita de 15 días (no puede volver a activarla). */
    @Column(name = "prueba_gratuita_usada")
    private Boolean pruebaGratuitaUsada;

    @Column(name = "stripe_customer_id", length = 255)
    private String stripeCustomerId;

    @Column(name = "stripe_subscription_id", length = 255)
    private String stripeSubscriptionId;

    /** Momento en que el usuario solicitó cancelar su inscripción (cuenta pasada a inactiva sin borrar datos). */
    @Column(name = "fecha_solicitud_baja")
    private LocalDateTime fechaSolicitudBaja;

    /** Motivo opcional declarado en la solicitud de baja. */
    @Column(name = "motivo_solicitud_baja", length = 2000)
    private String motivoSolicitudBaja;
}
