package es.nicolas.alumnos.models;

import es.nicolas.asignaturas.models.Asignatura;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "Alumnos")
@Table(name="ALUMNOS")
public class Alumno {
    @Schema(description = "ID del alumno", example = "1")
    // Indica que este campo es la clave primaria de la entidad
    @Id
    // Establece la estrategia de generación de valores para la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Por defecto, los campos son obligatorios (nullable = true)
    @Column(nullable = false, length = 30)
    @Schema(description = "Nombre del alumno", example = "Juan")
    private String nombre;
    @Column(nullable = false, length = 30)
    @Schema(description = "Apellido del alumno", example = "Pérez")
    private String apellido;
    @Column(nullable = false, length = 10)
    @Schema(description = "Grado del alumno", example = "1 DAW")
    private String grado;

    @Schema(description = "Fecha de creación del alumno", example = "2024-01-01T12:00:00")
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();
    @Schema(description = "Fecha de última actualización del alumno", example = "2024-01-02T12:12:12")
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();
    @Schema(description = "UUID del alumno", example = "123e4567-e89b-12d3-a456-426614174000")
    @Column(unique = true, updatable = false, nullable = false)
    private UUID uuid = UUID.randomUUID();

    // Nueva columna
    @Schema(description = "Indica si el alumno está eliminado", example = "false")
    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    @Builder.Default
    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name="asignatura_id")
    @Schema(description = "Asignatura del alumno", example = "Matemáticas")
    private Asignatura asignatura;

}
