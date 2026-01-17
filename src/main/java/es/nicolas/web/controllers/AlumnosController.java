package es.nicolas.web.controllers;

import es.nicolas.rest.alumnos.dto.AlumnoResponseDto;
import es.nicolas.rest.alumnos.services.AlumnosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("alumnos")
public class AlumnosController {
    private final AlumnosService alumnosService;

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model) {
        AlumnoResponseDto alumno = alumnosService.findById(id);
        model.addAttribute("alumno", alumno);
        return "alumnos/alumnoDetalle";
    }
}
