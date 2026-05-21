package co.edu.uniquindio.read_now.repository;

import co.edu.uniquindio.read_now.model.PqrMensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPqrMensajeRepository extends JpaRepository<PqrMensaje, Long> {

    List<PqrMensaje> findByPqrPqrIdOrderByFechaCreacionAsc(Long pqrId);
}
