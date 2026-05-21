package co.edu.uniquindio.read_now.repository;

import co.edu.uniquindio.read_now.model.Pqr;
import co.edu.uniquindio.read_now.model.Usuario;
import co.edu.uniquindio.read_now.model.enums.EstadoPqr;
import co.edu.uniquindio.read_now.model.enums.TipoPqr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPqrRepository extends JpaRepository<Pqr, Long> {

    List<Pqr> findByUsuarioUsuarioIdOrderByFechaActualizacionDesc(Long usuarioId);

    @Query("SELECT p FROM Pqr p WHERE p.usuario.usuarioId = :usuarioId AND p.pqrId = :pqrId")
    Optional<Pqr> findByPqrIdAndUsuarioUsuarioId(@Param("pqrId") Long pqrId, @Param("usuarioId") Long usuarioId);

    @Query("SELECT p FROM Pqr p WHERE (:estado IS NULL OR p.estado = :estado) "
            + "AND (:tipo IS NULL OR p.tipo = :tipo) "
            + "ORDER BY p.fechaActualizacion DESC")
    List<Pqr> findAllAdmin(@Param("estado") EstadoPqr estado, @Param("tipo") TipoPqr tipo);

    long countByEstado(EstadoPqr estado);

    List<Pqr> findByUsuarioOrderByFechaActualizacionDesc(Usuario usuario);
}
