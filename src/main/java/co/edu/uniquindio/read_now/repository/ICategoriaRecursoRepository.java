package co.edu.uniquindio.read_now.repository;

import co.edu.uniquindio.read_now.model.CategoriaRecurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICategoriaRecursoRepository extends JpaRepository<CategoriaRecurso, Long> {

    Optional<CategoriaRecurso> findByNombre(String nombre);
}
