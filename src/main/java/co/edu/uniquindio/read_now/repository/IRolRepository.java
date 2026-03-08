package co.edu.uniquindio.read_now.repository;

import co.edu.uniquindio.read_now.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByNombre(String nombre);
}
