package co.edu.uniquindio.read_now.repository;

import co.edu.uniquindio.read_now.model.Calificacion;
import co.edu.uniquindio.read_now.model.Recurso;
import co.edu.uniquindio.read_now.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICalificacionRepository extends JpaRepository<Calificacion, Long> {

    List<Calificacion> findByRecurso(Recurso recurso);

    Optional<Calificacion> findByUsuarioAndRecurso(Usuario usuario, Recurso recurso);

    @Query("SELECT COALESCE(AVG(c.valor), 0) FROM Calificacion c WHERE c.recurso.recursoId = :recursoId")
    Double findPromedioByRecursoId(@Param("recursoId") Long recursoId);

    long countByRecursoRecursoId(Long recursoId);
}
