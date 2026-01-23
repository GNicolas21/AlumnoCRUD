package es.nicolas.web.controllers;

import es.nicolas.rest.alumnos.dto.AlumnoCreateDto;
import es.nicolas.rest.alumnos.dto.AlumnoResponseDto;
import es.nicolas.rest.alumnos.services.AlumnosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("alumnos")
public class AlumnosController {
    private final AlumnosService alumnosService;

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model) {
        AlumnoResponseDto alumno = alumnosService.findById(id);
        model.addAttribute("alumno", alumno);
        return "alumnos/detalle";
    }

    @GetMapping({"", "/", "/lista"})
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "page", defaultValue = "0") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<AlumnoResponseDto> alumnosPage = alumnosService.findAll(
                Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        model.addAttribute("page", alumnosPage);
        return "alumnos/lista";
    }

    // Crear un nuevo alumno
    @GetMapping("/new")
    public String nuevoAlumnoForm(Model model) {
        // LO añadimos al model
        model.addAttribute("alumno", AlumnoCreateDto.builder().build());
        return "/alumnos/form";
    }

    @PostMapping("/new")
    public String nuevoAlumnoSubmit(@Valid @ModelAttribute AlumnoCreateDto alumno, BindingResult bindingResult) {
        // Si tiene errores ...
        if (bindingResult.hasErrors()) {
            return "/alumnos/form";
        } else {
            // si no insertamos
            alumnosService.save(alumno);
            return "redirect:/alumnos/lista";
        }
    }

}
