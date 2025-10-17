package es.nicolas.alumnocrud.services;

import es.nicolas.alumnocrud.models.Alumno;
import es.nicolas.alumnocrud.repositories.AlumnosRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AlumnosServiceImpl implements AlumnosService{
    private final AlumnosRepository alumnosRepository;

    @Autowired
    public AlumnosServiceImpl(AlumnosRepository alumnosRepository) {
        this.alumnosRepository = alumnosRepository;
    }

    @Override
    public List<Alumno> findAll(String nombre, String apellido) {
        //Si está vacio o nulo, devolvemos todos los alumnos
        if ((nombre == null || nombre.isEmpty()) && (apellido == null || apellido.isEmpty())) {
            log.info("Buscando todos los alumnos");
            return alumnosRepository.findAll();
        }

        //Si el nombre no está vacío pero el apellido si, buscamos por nombre
        if((nombre != null && !nombre.isEmpty()) && (apellido == null || apellido.isEmpty())) {
            log.info("Buscando alumnos por nombre: {}", nombre);
            return alumnosRepository.findAllByNombre(nombre);
        }

        //Si el apellido no está vacío pero el nombre si, buscamos por apellido
        if ((nombre == null || nombre.isEmpty())) {
            log.info("Buscando alumnos por apellido: {}", apellido);
            return alumnosRepository.findAllByApellido(apellido);
        }

        //Si el nombre y apellido no están vacíos, buscamos por ambos
        log.info("Buscando alumnos por nombre: {}", nombre + " y apellido: " + apellido);
        return alumnosRepository.findAllByNombreAndApellido(nombre, apellido);
    }

    @Override
    public Alumno findById(Long id) {
        alumnosRepository.findById(id).orElse(null);
    }
}
