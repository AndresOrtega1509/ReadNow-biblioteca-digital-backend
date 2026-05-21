package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.dto.request.PqrActualizarEstadoRequestDTO;
import co.edu.uniquindio.read_now.dto.request.PqrCrearRequestDTO;
import co.edu.uniquindio.read_now.dto.request.PqrMensajeRequestDTO;
import co.edu.uniquindio.read_now.dto.response.PqrAdminResumenResponseDTO;
import co.edu.uniquindio.read_now.dto.response.PqrDetalleResponseDTO;
import co.edu.uniquindio.read_now.dto.response.PqrMensajeResponseDTO;
import co.edu.uniquindio.read_now.dto.response.PqrResumenResponseDTO;
import co.edu.uniquindio.read_now.model.Pqr;
import co.edu.uniquindio.read_now.model.PqrMensaje;
import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.model.enums.EstadoPqr;
import co.edu.uniquindio.read_now.model.enums.TipoPqr;
import co.edu.uniquindio.read_now.repository.IPqrMensajeRepository;
import co.edu.uniquindio.read_now.repository.IPqrRepository;
import co.edu.uniquindio.read_now.repository.IUsuarioRepository;
import co.edu.uniquindio.read_now.service.IEmailService;
import co.edu.uniquindio.read_now.service.IPqrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PqrServiceImpl implements IPqrService {

    private final IPqrRepository pqrRepository;
    private final IPqrMensajeRepository pqrMensajeRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IEmailService emailService;

    @Value("${app.frontend.url:http://localhost:4200}")
    private String frontendUrl;

    @Override
    @Transactional
    public PqrDetalleResponseDTO crearPqr(Long usuarioId, PqrCrearRequestDTO request) {
        Usuario lector = obtenerUsuario(usuarioId);
        validarRolLector(lector);

        LocalDateTime ahora = LocalDateTime.now();
        Pqr pqrGuardada = pqrRepository.save(Pqr.builder()
                .usuario(lector)
                .tipo(request.tipo())
                .asunto(request.asunto().trim())
                .descripcion(request.descripcion().trim())
                .estado(EstadoPqr.ABIERTA)
                .fechaCreacion(ahora)
                .fechaActualizacion(ahora)
                .build());

        agregarMensajeInterno(pqrGuardada, lector, request.descripcion().trim(), false);

        enviarCorreosSeguro(() -> emailService.enviarPqrRecibidaLector(
                lector.getEmail(), nombreCompleto(lector), pqrGuardada.getPqrId(), pqrGuardada.getAsunto()));
        enviarCorreosSeguro(() -> notificarAdminsNuevaPqr(pqrGuardada, lector));

        return toDetalleDto(pqrGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PqrResumenResponseDTO> listarMisPqrs(Long usuarioId) {
        return pqrRepository.findByUsuarioUsuarioIdOrderByFechaActualizacionDesc(usuarioId).stream()
                .map(this::toResumenDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PqrDetalleResponseDTO obtenerDetalleLector(Long usuarioId, Long pqrId) {
        Pqr pqr = pqrRepository.findByPqrIdAndUsuarioUsuarioId(pqrId, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PQR no encontrada"));
        return toDetalleDto(pqr);
    }

    @Override
    @Transactional
    public PqrDetalleResponseDTO agregarMensajeLector(Long usuarioId, Long pqrId, PqrMensajeRequestDTO request) {
        Pqr pqr = pqrRepository.findByPqrIdAndUsuarioUsuarioId(pqrId, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PQR no encontrada"));
        validarPqrNoCerrada(pqr);

        Usuario lector = pqr.getUsuario();
        agregarMensajeInterno(pqr, lector, request.contenido().trim(), false);
        pqr.setFechaActualizacion(LocalDateTime.now());
        if (pqr.getEstado() == EstadoPqr.RESUELTA) {
            pqr.setEstado(EstadoPqr.EN_TRAMITE);
        }
        pqrRepository.save(pqr);

        enviarCorreosSeguro(() -> notificarAdminsMensajeLector(pqr, lector, request.contenido().trim()));

        return toDetalleDto(pqr);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PqrAdminResumenResponseDTO> listarPqrsAdmin(EstadoPqr estado, TipoPqr tipo) {
        return pqrRepository.findAllAdmin(estado, tipo).stream()
                .map(this::toAdminResumenDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PqrDetalleResponseDTO obtenerDetalleAdmin(Long pqrId) {
        Pqr pqr = pqrRepository.findById(pqrId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PQR no encontrada"));
        return toDetalleDto(pqr);
    }

    @Override
    @Transactional
    public PqrDetalleResponseDTO actualizarEstadoAdmin(Long adminId, Long pqrId, PqrActualizarEstadoRequestDTO request) {
        Usuario admin = obtenerUsuario(adminId);
        validarRolAdmin(admin);

        Pqr pqr = pqrRepository.findById(pqrId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PQR no encontrada"));

        EstadoPqr estadoAnterior = pqr.getEstado();
        pqr.setEstado(request.estado());
        pqr.setFechaActualizacion(LocalDateTime.now());
        pqrRepository.save(pqr);

        if (request.mensaje() != null && !request.mensaje().isBlank()) {
            agregarMensajeInterno(pqr, admin, request.mensaje().trim(), true);
        }

        Usuario lector = pqr.getUsuario();
        enviarCorreosSeguro(() -> emailService.enviarPqrCambioEstadoLector(
                lector.getEmail(),
                nombreCompleto(lector),
                pqr.getPqrId(),
                pqr.getAsunto(),
                etiquetaEstado(estadoAnterior),
                etiquetaEstado(request.estado()),
                request.mensaje()));

        return toDetalleDto(pqr);
    }

    @Override
    @Transactional
    public PqrDetalleResponseDTO agregarMensajeAdmin(Long adminId, Long pqrId, PqrMensajeRequestDTO request) {
        Usuario admin = obtenerUsuario(adminId);
        validarRolAdmin(admin);

        Pqr pqr = pqrRepository.findById(pqrId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PQR no encontrada"));
        validarPqrNoCerrada(pqr);

        agregarMensajeInterno(pqr, admin, request.contenido().trim(), true);
        pqr.setFechaActualizacion(LocalDateTime.now());
        if (pqr.getEstado() == EstadoPqr.ABIERTA) {
            pqr.setEstado(EstadoPqr.EN_REVISION);
        }
        pqrRepository.save(pqr);

        Usuario lector = pqr.getUsuario();
        enviarCorreosSeguro(() -> emailService.enviarPqrRespuestaAdminLector(
                lector.getEmail(),
                nombreCompleto(lector),
                pqr.getPqrId(),
                pqr.getAsunto(),
                request.contenido().trim()));

        return toDetalleDto(pqr);
    }

    private void agregarMensajeInterno(Pqr pqr, Usuario autor, String contenido, boolean esAdmin) {
        PqrMensaje mensaje = PqrMensaje.builder()
                .pqr(pqr)
                .autor(autor)
                .contenido(contenido)
                .esAdmin(esAdmin)
                .fechaCreacion(LocalDateTime.now())
                .build();
        pqrMensajeRepository.save(mensaje);
    }

    private void notificarAdminsNuevaPqr(Pqr pqr, Usuario lector) {
        for (Usuario admin : listarAdminsActivos()) {
            emailService.enviarPqrNuevaAdmin(
                    admin.getEmail(),
                    nombreCompleto(admin),
                    pqr.getPqrId(),
                    pqr.getAsunto(),
                    etiquetaTipo(pqr.getTipo()),
                    nombreCompleto(lector),
                    lector.getEmail(),
                    pqr.getDescripcion());
        }
    }

    private void notificarAdminsMensajeLector(Pqr pqr, Usuario lector, String contenido) {
        for (Usuario admin : listarAdminsActivos()) {
            emailService.enviarPqrMensajeLectorAdmin(
                    admin.getEmail(),
                    nombreCompleto(admin),
                    pqr.getPqrId(),
                    pqr.getAsunto(),
                    nombreCompleto(lector),
                    contenido);
        }
    }

    private List<Usuario> listarAdminsActivos() {
        return usuarioRepository.findByRol_NombreAndActivo("ADMIN", "S");
    }

    private void enviarCorreosSeguro(Runnable accion) {
        try {
            accion.run();
        } catch (Exception e) {
            log.warn("No se pudo enviar correo PQR: {}", e.getMessage());
        }
    }

    private Usuario obtenerUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private void validarRolLector(Usuario usuario) {
        if (!"LECTOR".equalsIgnoreCase(usuario.getRol().getNombre())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo los lectores pueden crear PQRs");
        }
    }

    private void validarRolAdmin(Usuario usuario) {
        if (!"ADMIN".equalsIgnoreCase(usuario.getRol().getNombre())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acción reservada para administradores");
        }
    }

    private void validarPqrNoCerrada(Pqr pqr) {
        if (pqr.getEstado() == EstadoPqr.CERRADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta PQR está cerrada y no admite nuevos mensajes");
        }
    }

    private PqrResumenResponseDTO toResumenDto(Pqr pqr) {
        int total = pqrMensajeRepository.findByPqrPqrIdOrderByFechaCreacionAsc(pqr.getPqrId()).size();
        return new PqrResumenResponseDTO(
                pqr.getPqrId(),
                pqr.getTipo(),
                pqr.getAsunto(),
                pqr.getEstado(),
                pqr.getFechaCreacion(),
                pqr.getFechaActualizacion(),
                total);
    }

    private PqrAdminResumenResponseDTO toAdminResumenDto(Pqr pqr) {
        Usuario u = pqr.getUsuario();
        int total = pqrMensajeRepository.findByPqrPqrIdOrderByFechaCreacionAsc(pqr.getPqrId()).size();
        return new PqrAdminResumenResponseDTO(
                pqr.getPqrId(),
                pqr.getTipo(),
                pqr.getAsunto(),
                pqr.getEstado(),
                u.getUsuarioId(),
                nombreCompleto(u),
                u.getEmail(),
                pqr.getFechaCreacion(),
                pqr.getFechaActualizacion(),
                total);
    }

    private PqrDetalleResponseDTO toDetalleDto(Pqr pqr) {
        Usuario u = pqr.getUsuario();
        List<PqrMensajeResponseDTO> mensajes = pqrMensajeRepository
                .findByPqrPqrIdOrderByFechaCreacionAsc(pqr.getPqrId())
                .stream()
                .map(m -> new PqrMensajeResponseDTO(
                        m.getMensajeId(),
                        nombreCompleto(m.getAutor()),
                        m.isEsAdmin(),
                        m.getContenido(),
                        m.getFechaCreacion()))
                .toList();

        return new PqrDetalleResponseDTO(
                pqr.getPqrId(),
                pqr.getTipo(),
                pqr.getAsunto(),
                pqr.getDescripcion(),
                pqr.getEstado(),
                pqr.getFechaCreacion(),
                pqr.getFechaActualizacion(),
                u.getUsuarioId(),
                nombreCompleto(u),
                u.getEmail(),
                mensajes);
    }

    private static String nombreCompleto(Usuario u) {
        String n = u.getNombre() != null ? u.getNombre().trim() : "";
        String a = u.getApellido() != null ? u.getApellido().trim() : "";
        String completo = (n + " " + a).trim();
        return completo.isEmpty() ? u.getEmail() : completo;
    }

    private static String etiquetaEstado(EstadoPqr estado) {
        return switch (estado) {
            case ABIERTA -> "Abierta";
            case EN_REVISION -> "En revisión";
            case EN_TRAMITE -> "En trámite";
            case RESUELTA -> "Resuelta";
            case CERRADA -> "Cerrada";
        };
    }

    private static String etiquetaTipo(TipoPqr tipo) {
        return switch (tipo) {
            case PETICION -> "Petición";
            case QUEJA -> "Queja";
            case RECLAMO -> "Reclamo";
            case SUGERENCIA -> "Sugerencia";
        };
    }
}
