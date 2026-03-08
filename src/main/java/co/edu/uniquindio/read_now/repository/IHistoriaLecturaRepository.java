package co.edu.uniquindio.read_now.repository;

import co.edu.uniquindio.read_now.model.HistoriaLectura;
import co.edu.uniquindio.read_now.model.Recurso;
import co.edu.uniquindio.read_now.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IHistoriaLecturaRepository extends JpaRepository<HistoriaLectura, Long> {

    List<HistoriaLectura> findByUsuarioOrderByFechaLecturaDesc(Usuario usuario);

    Optional<HistoriaLectura> findByUsuarioAndRecurso(Usuario usuario, Recurso recurso);

    @Query("SELECT hl.recurso, COUNT(hl) as total FROM HistoriaLectura hl " +
            "GROUP BY hl.recurso ORDER BY total DESC")
    List<Object[]> findRecursosMasLeidos();

    long countByRecursoRecursoId(Long recursoId);
}
