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
@Table( name="roles")
public class Rol {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column (name="rol_id")
    private Long rolId;
    private String nombre;
    private String descripcion;
}
