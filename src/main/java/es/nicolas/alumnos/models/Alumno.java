package es.nicolas.alumnos.models;

import es.nicolas.asignaturas.models.Asignatura;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
// Junto con @Entity indica que esta clase es una entidad de JPA
// Y no es recomendable usar @Data
@Entity
// Si no se especifica, el nombre de la tabla será el mismo que el de la clase
@Table(name="ALUMNOS")
public class Alumno {
    // Indica que este campo es la clave primaria de la entidad
    @Id
    // Establece la estrategia de generación de valores para la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Por defecto, los campos son obligatorios (nullable = true)
    @Column(nullable = false, length = 30)
    private String nombre;
    @Column(nullable = false, length = 30)
    private String apellido;
    @Column(nullable = false, length = 10)
    private String grado;

    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();
    @Column(unique = true, updatable = false, nullable = false)
    private UUID uuid = UUID.randomUUID();

    // Nueva columna
    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    @Builder.Default
    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name="asignatura_id")
    private Asignatura asignatura;
}
