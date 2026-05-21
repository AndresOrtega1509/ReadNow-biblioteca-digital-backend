package co.edu.uniquindio.read_now.repository;

import co.edu.uniquindio.read_now.model.HistoriaLectura;
import co.edu.uniquindio.read_now.model.Recurso;
import co.edu.uniquindio.read_now.model.Usuario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IHistoriaLecturaRepository extends JpaRepository<HistoriaLectura, Long> {

    @Query("SELECT hl FROM HistoriaLectura hl WHERE hl.usuario = :usuario AND hl.recurso.activo = 'S' "
            + "AND hl.ultimaPagina IS NOT NULL AND hl.totalPaginas IS NOT NULL AND hl.totalPaginas > 0 "
            + "ORDER BY hl.fechaLectura DESC")
    List<HistoriaLectura> findHistorialActivosByUsuario(@Param("usuario") Usuario usuario);

    Optional<HistoriaLectura> findByUsuarioAndRecurso(Usuario usuario, Recurso recurso);

    List<HistoriaLectura> findAllByUsuarioAndRecursoOrderByFechaLecturaDesc(Usuario usuario, Recurso recurso);

    @Query("SELECT hl FROM HistoriaLectura hl WHERE hl.usuario = :usuario AND hl.recurso.activo = 'S' "
            + "AND hl.ultimaPagina IS NOT NULL AND hl.totalPaginas IS NOT NULL AND hl.totalPaginas > 0 "
            + "AND hl.ultimaPagina >= 1 ORDER BY hl.fechaLectura DESC")
    List<HistoriaLectura> findContinuarLeyendo(@Param("usuario") Usuario usuario, Pageable pageable);

    @Query("SELECT hl.recurso, COUNT(hl) as total FROM HistoriaLectura hl " +
            "GROUP BY hl.recurso ORDER BY total DESC")
    List<Object[]> findRecursosMasLeidos();

    long countByRecursoRecursoId(Long recursoId);
}
