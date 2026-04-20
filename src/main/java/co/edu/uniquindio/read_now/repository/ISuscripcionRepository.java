package co.edu.uniquindio.read_now.repository;

import co.edu.uniquindio.read_now.model.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ISuscripcionRepository extends JpaRepository<Suscripcion, Long> {

    /** Planes gratuitos (precio = 0). */
    List<Suscripcion> findByPrecio(double precio);

    Optional<Suscripcion> findByCodigoPlan(String codigoPlan);

    Optional<Suscripcion> findByStripePriceId(String stripePriceId);
}
