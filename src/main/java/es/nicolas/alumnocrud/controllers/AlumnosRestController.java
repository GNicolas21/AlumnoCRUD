package es.nicolas.alumnocrud.controllers;


import es.nicolas.alumnocrud.models.Alumno;
import es.nicolas.alumnocrud.repositories.AlumnosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RequestMapping("api/${api.version}/alumnos")
@RestController
public class AlumnosRestController {

    private final AlumnosRepository alumnosRepository;

    @Autowired
    public AlumnosRestController(AlumnosRepository alumnosRepository) {
        this.alumnosRepository = alumnosRepository;
    }

    @GetMapping
    public ResponseEntity<List<Alumno>> getAllAlumnos(@RequestParam(required = false) String nombre) {
        if (nombre != null) {
            return  ResponseEntity.ok(alumnosRepository.findAllByNombre(nombre));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(alumnosRepository.findAll());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alumno> getAlumnoById(@PathVariable long id){
        return alumnosRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
