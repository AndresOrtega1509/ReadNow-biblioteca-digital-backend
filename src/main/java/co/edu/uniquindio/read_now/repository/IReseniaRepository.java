package co.edu.uniquindio.read_now.repository;

import co.edu.uniquindio.read_now.model.Recurso;
import co.edu.uniquindio.read_now.model.Resenia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IReseniaRepository extends JpaRepository<Resenia, Long> {

    List<Resenia> findByRecursoOrderByFechaCreacionDesc(Recurso recurso);

    List<Resenia> findByRecurso(Recurso recurso);
}
