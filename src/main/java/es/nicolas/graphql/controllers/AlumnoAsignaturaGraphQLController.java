package es.nicolas.graphql.controllers;

import es.nicolas.rest.alumnos.models.Alumno;
import es.nicolas.rest.alumnos.repositories.AlumnosRepository;
import es.nicolas.rest.asignaturas.models.Asignatura;
import es.nicolas.rest.asignaturas.repositories.AsignaturasRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class AlumnoAsignaturaGraphQLController {
    private final AlumnosRepository alumnosRepository;
    private final AsignaturasRespository asignaturasRespository;


    // QUERIES

    @QueryMapping
    public List<Alumno> alumnos(){
        return alumnosRepository.findAll();
    }

    @QueryMapping
    public Alumno alumnoById(@Argument Long id){
        // Devuelve un alumno por su id
        Optional<Alumno> alumnoOpt = alumnosRepository.findById(id);
        return alumnoOpt.orElse(null);
    }

    @QueryMapping
    public List<Asignatura> asignaturas(){
        // Devuelve todas las asignaturas como entidades
        return asignaturasRespository.findAll();
    }

    @QueryMapping
    public Asignatura asignaturaById(@Argument Long id){
        // Devuelve una asignatura por su id
        return asignaturasRespository.findById(id).orElse(null);
    }

    // asignaturasByNombre(nombre: String!): [Asignatura!]!
    @QueryMapping
    public List<Asignatura> asignaturasByNombre(@Argument String nombre){
        // Devuelve una lista de asignaturas que coinciden con el nombre proporcionado, sino devuelve vacio
        return asignaturasRespository.findByNombreContainsIgnoreCase(nombre);
    }

    // --- RESOLVERS RELACIONES ---
    @SchemaMapping(typeName = "Alumno", field = "asignatura")
    public Asignatura asignatura(Alumno alumno){
        // Resuelve la asignatura asociada a un alumno
        return alumno.getAsignatura();
    }

    @SchemaMapping(typeName = "Asignatura", field = "alumnos")
    public List<Alumno> alumnos(Asignatura asignatura){
        // Resuelve la lista de alumnos asociados a una asignatura
        return alumnosRepository.findByAsignatura(asignatura);
    }

}
