package es.nicolas.graphql.controllers;

import es.nicolas.rest.alumnos.models.Alumno;
import es.nicolas.rest.alumnos.repositories.AlumnosRepository;
import es.nicolas.rest.asignaturas.repositories.AsignaturasRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

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
}
