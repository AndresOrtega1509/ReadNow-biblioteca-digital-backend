package co.edu.uniquindio.read_now.repository;

import co.edu.uniquindio.read_now.model.TipoRecurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ITipoRecursoRepository extends JpaRepository<TipoRecurso, Long> {

    Optional<TipoRecurso> findByNombre(String nombre);
}
