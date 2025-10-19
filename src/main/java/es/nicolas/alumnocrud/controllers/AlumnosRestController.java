package es.nicolas.alumnocrud.controllers;


import es.nicolas.alumnocrud.models.Alumno;
import es.nicolas.alumnocrud.services.AlumnosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@Slf4j
@RequestMapping("api/${api.version}/alumnos")
@RestController
public class AlumnosRestController {

    private final AlumnosService alumnosService;

    @Autowired
    public AlumnosRestController(AlumnosService alumnosService) {
        this.alumnosService = alumnosService;
    }

    @GetMapping()
    public ResponseEntity<List<Alumno>> getAllAlumnos(@RequestParam(required = false) String nombre,
                                                      @RequestParam(required = false) String apellido){
        log.info("Buscando alumnos por nombre: {} y apellido: {}", nombre, apellido);
        return ResponseEntity.ok(alumnosService.findAll(nombre, apellido));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alumno> getAlumnoById(@PathVariable long id){
        log.info("Buscando alumnos por id: {}", id);
        return ResponseEntity.ok(alumnosService.findById(id));
    }

}
