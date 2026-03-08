package co.edu.uniquindio.read_now.repository;

import co.edu.uniquindio.read_now.model.Favorito;
import co.edu.uniquindio.read_now.model.Recurso;
import co.edu.uniquindio.read_now.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IFavoritoRepository extends JpaRepository<Favorito, Long> {

    List<Favorito> findByUsuario(Usuario usuario);

    Optional<Favorito> findByUsuarioAndRecurso(Usuario usuario, Recurso recurso);

    boolean existsByUsuarioAndRecurso(Usuario usuario, Recurso recurso);
}
