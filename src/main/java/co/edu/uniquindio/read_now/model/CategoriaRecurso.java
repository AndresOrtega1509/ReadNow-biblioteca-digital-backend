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
@Table (name = "categorias_recursos")
public class CategoriaRecurso {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name = "categoria_recurso_id")
    private Long categoriaRecursoId;
    private String nombre;
}
