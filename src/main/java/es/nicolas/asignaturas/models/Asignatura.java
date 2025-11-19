package es.nicolas.asignaturas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import es.nicolas.alumnos.models.Alumno;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@Getter
@Setter
@NoArgsConstructor // JPA necesita un constructor sin argumentos
@AllArgsConstructor
@Entity
@Table(name="ASIGNATURAS")
public class Asignatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String nombre;

    @Column(name = "duracionhoras", nullable = false)
    private Integer duracionHoras;

    @Builder.Default
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(columnDefinition = "boolean default false")
    @Builder.Default
    // Nueva columna para soft delete
    private Boolean isDeleted = false;

    // Relación One-to-Many con Alumnos
    @JsonIgnoreProperties("asignatura")
    @OneToMany(mappedBy = "asignatura")
    private List<Alumno> alumnos; // Una asignatura puede tener muchos alumnos

}
