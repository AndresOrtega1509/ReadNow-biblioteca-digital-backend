package co.edu.uniquindio.read_now.repository;

import co.edu.uniquindio.read_now.model.Recurso;
import co.edu.uniquindio.read_now.model.Resenia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IReseniaRepository extends JpaRepository<Resenia, Long> {

    List<Resenia> findByRecursoOrderByFechaCreacionDesc(Recurso recurso);

    List<Resenia> findByRecurso(Recurso recurso);

    /** Borrado directo (evita cargar la entidad y problemas con proxies/Lombok en el flush). */
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Resenia r WHERE r.reseniaId = :reseniaId")
    int deleteByReseniaId(@Param("reseniaId") Long reseniaId);
}
