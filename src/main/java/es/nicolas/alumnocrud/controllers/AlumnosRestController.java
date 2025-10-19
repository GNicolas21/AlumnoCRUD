package es.nicolas.alumnocrud.controllers;


import es.nicolas.alumnocrud.models.Alumno;
import es.nicolas.alumnocrud.services.AlumnosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @PostMapping()
    public ResponseEntity<Alumno> create(@RequestBody Alumno alumno){
        log.info("Creando alumno: {}", alumno);
        var saved = alumnosService.save(alumno);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alumno> update(@PathVariable long id, @RequestBody Alumno alumno) {
        log.info("Actualizando alumno con id: {} con alumno: {}", id, alumno);
        return ResponseEntity.ok(alumnosService.update(id, alumno));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Alumno> updatePartial(@PathVariable Long id, @RequestBody Alumno alumno){
        log.info("Actualizando parcialmente alumno con id: {} con alumno: {}", id, alumno);
        return ResponseEntity.ok(alumnosService.update(id, alumno));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        log.info("Eliminando alumno con id: {}", id);
        alumnosService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
