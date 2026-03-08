package co.edu.uniquindio.read_now.service.impl;

import co.edu.uniquindio.read_now.dto.response.EstadisticasResponseDTO;
import co.edu.uniquindio.read_now.dto.response.RecursoResponseDTO;
import co.edu.uniquindio.read_now.model.Recurso;
import co.edu.uniquindio.read_now.model.Suscripcion;
import co.edu.uniquindio.read_now.model.TipoRecurso;
import co.edu.uniquindio.read_now.repository.*;
import co.edu.uniquindio.read_now.service.IEstadisticasService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadisticasServiceImpl implements IEstadisticasService {

    private final IUsuarioRepository usuarioRepository;
    private final ISuscripcionRepository suscripcionRepository;
    private final IRecursoRepository recursoRepository;
    private final IHistoriaLecturaRepository historiaLecturaRepository;
    private final ICalificacionRepository calificacionRepository;
    private final IReseniaRepository reseniaRepository;
    private final IFavoritoRepository favoritoRepository;
    private final ITipoRecursoRepository tipoRecursoRepository;

    @Override
    public EstadisticasResponseDTO obtenerEstadisticas() {
        long totalUsuarios = usuarioRepository.count();
        long totalRecursos = recursoRepository.countByActivo("S");
        long totalLecturas = historiaLecturaRepository.count();
        long totalCalificaciones = calificacionRepository.count();
        long totalResenias = reseniaRepository.count();
        long totalFavoritos = favoritoRepository.count();

        List<RecursoResponseDTO> recursosMasLeidos = historiaLecturaRepository.findRecursosMasLeidos()
                .stream()
                .limit(10)
                .map(row -> {
                    Recurso r = (Recurso) row[0];
                    Double promedio = calificacionRepository.findPromedioByRecursoId(r.getRecursoId());
                    long totalCal = calificacionRepository.countByRecursoRecursoId(r.getRecursoId());
                    return new RecursoResponseDTO(
                            r.getRecursoId(), r.getNombre(), r.getAutor(), r.getDescripcion(),
                            r.getIdioma(), r.getUrlArchivo(), r.getUrlPortada(), r.getFechaPublicacion(),
                            r.getTipoRecurso() != null ? r.getTipoRecurso().getNombre() : null,
                            r.getCategoriaRecurso() != null ? r.getCategoriaRecurso().getNombre() : null,
                            promedio, totalCal
                    );
                })
                .toList();

        List<RecursoResponseDTO> recursosMejorCalificados = recursoRepository.findMejorCalificados()
                .stream()
                .limit(10)
                .map(r -> {
                    Double promedio = calificacionRepository.findPromedioByRecursoId(r.getRecursoId());
                    long totalCal = calificacionRepository.countByRecursoRecursoId(r.getRecursoId());
                    return new RecursoResponseDTO(
                            r.getRecursoId(), r.getNombre(), r.getAutor(), r.getDescripcion(),
                            r.getIdioma(), r.getUrlArchivo(), r.getUrlPortada(), r.getFechaPublicacion(),
                            r.getTipoRecurso() != null ? r.getTipoRecurso().getNombre() : null,
                            r.getCategoriaRecurso() != null ? r.getCategoriaRecurso().getNombre() : null,
                            promedio, totalCal
                    );
                })
                .toList();

        List<EstadisticasResponseDTO.RecursoPorTipoDTO> recursosPorTipo = new ArrayList<>();
        List<TipoRecurso> tipos = tipoRecursoRepository.findAll();
        for (TipoRecurso tipo : tipos) {
            long cantidad = recursoRepository.countByTipoRecursoNombre(tipo.getNombre());
            recursosPorTipo.add(new EstadisticasResponseDTO.RecursoPorTipoDTO(tipo.getNombre(), cantidad));
        }
        recursosPorTipo.sort(Comparator.comparingLong(EstadisticasResponseDTO.RecursoPorTipoDTO::cantidad).reversed());

        LocalDate hoy = LocalDate.now();
        LocalDateTime ahora = LocalDateTime.now();
        long suscripcionesActivas = usuarioRepository.countSuscripcionesActivas(hoy, ahora);
        long suscripcionesVencidas = usuarioRepository.countSuscripcionesVencidas(hoy, ahora);

        List<EstadisticasResponseDTO.SuscripcionPorPlanDTO> suscripcionesPorPlan = new ArrayList<>();
        long activasPrueba = usuarioRepository.countPruebaGratuitaActivas(hoy, ahora);
        long vencidasPrueba = usuarioRepository.countPruebaGratuitaVencidas(hoy, ahora);
        if (activasPrueba > 0 || vencidasPrueba > 0) {
            suscripcionesPorPlan.add(new EstadisticasResponseDTO.SuscripcionPorPlanDTO("Prueba gratuita", activasPrueba, vencidasPrueba));
        }
        for (Suscripcion plan : suscripcionRepository.findAll()) {
            long activas = usuarioRepository.countPlanActivas(plan.getSuscripcionId(), hoy, ahora);
            long vencidas = usuarioRepository.countPlanVencidas(plan.getSuscripcionId(), hoy, ahora);
            if (activas > 0 || vencidas > 0) {
                suscripcionesPorPlan.add(new EstadisticasResponseDTO.SuscripcionPorPlanDTO(plan.getNombre(), activas, vencidas));
            }
        }

        return new EstadisticasResponseDTO(
                totalUsuarios, totalRecursos, totalLecturas,
                totalCalificaciones, totalResenias, totalFavoritos,
                suscripcionesActivas, suscripcionesVencidas, suscripcionesPorPlan,
                recursosMasLeidos, recursosMejorCalificados, recursosPorTipo
        );
    }
}
