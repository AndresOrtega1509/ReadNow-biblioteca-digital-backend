package co.edu.uniquindio.read_now.repository;

import co.edu.uniquindio.read_now.model.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRecursoRepository extends JpaRepository<Recurso, Long> {

    Optional<Recurso> findFirstByNombreIgnoreCase(String nombre);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Recurso r SET r.urlPortada = :urlPortada WHERE (r.urlPortada IS NULL OR r.urlPortada = '') AND LOWER(r.nombre) LIKE LOWER(CONCAT('%', :nombreContiene, '%'))")
    int actualizarPortadaSiVacia(@Param("nombreContiene") String nombreContiene, @Param("urlPortada") String urlPortada);

    List<Recurso> findByActivoAndNombreContainingIgnoreCase(String activo, String nombre);

    List<Recurso> findByActivo(String activo);

    List<Recurso> findByTipoRecursoTipoRecursoIdAndActivo(Long tipoRecursoId, String activo);

    List<Recurso> findByCategoriaRecursoCategoriaRecursoIdAndActivo(Long categoriaId, String activo);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Recurso r SET r.categoriaRecurso = null WHERE r.categoriaRecurso.categoriaRecursoId = :categoriaId")
    int desvincularPorCategoria(@Param("categoriaId") Long categoriaId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Recurso r SET r.tipoRecurso = null WHERE r.tipoRecurso.tipoRecursoId = :tipoId")
    int desvincularPorTipo(@Param("tipoId") Long tipoId);

    @Query("SELECT r FROM Recurso r WHERE r.activo = 'S' ORDER BY " +
            "(SELECT COALESCE(AVG(c.valor), 0) FROM Calificacion c WHERE c.recurso = r) DESC")
    List<Recurso> findMejorCalificados();

    long countByActivo(String activo);

    long countByTipoRecursoNombre(String tipoRecurso);
}
