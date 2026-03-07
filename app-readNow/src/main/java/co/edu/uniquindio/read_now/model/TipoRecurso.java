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
@Table(name = "tipos_recursos")
public class TipoRecurso {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column ( name = "tipo_recurso_id")
    private Long tipoRecursoId;
    private String nombre;
}
