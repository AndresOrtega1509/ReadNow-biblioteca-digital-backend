package co.edu.uniquindio.read_now.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table( name = "favoritos" )
public class Favorito {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name ="favorito_id")
    private Long favoritoId;

    @ManyToOne
    @JoinColumn ( name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn ( name = "recurso_id")
    private Recurso recurso;

}
