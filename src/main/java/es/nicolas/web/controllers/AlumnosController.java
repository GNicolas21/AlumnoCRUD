package es.nicolas.web.controllers;

import es.nicolas.rest.alumnos.dto.AlumnoCreateDto;
import es.nicolas.rest.alumnos.dto.AlumnoResponseDto;
import es.nicolas.rest.alumnos.dto.AlumnoUpdateDto;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String lista(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "10") int size) {
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
        model.addAttribute("modoEditar", false);
        return "/alumnos/form";
    }

    @PostMapping("/new")
    public String nuevoAlumnoSubmit(@Valid @ModelAttribute("alumno") AlumnoCreateDto alumno, BindingResult bindingResult) {
        // Si tiene errores ...
        if (bindingResult.hasErrors()) {
            return "/alumnos/form";
        } else {
            // si no insertamos
            alumnosService.save(alumno);
            return "redirect:/alumnos/lista";
        }
    }

    @GetMapping("/{id}/edit")
    public String editarAlumnoForm(@PathVariable Long id, Model model) {
        AlumnoResponseDto alumno = alumnosService.findById(id);
        if (alumno == null) {
            return "redirect:/alumnos/new";
        } else {
            AlumnoUpdateDto alumnoUpdateDto = AlumnoUpdateDto.builder()
                    .nombre(alumno.getNombre())
                    .apellido(alumno.getApellido())
                    .grado(alumno.getGrado())
                    .asignatura(alumno.getAsignatura())
                    .build();
            model.addAttribute("alumno", alumnoUpdateDto);
            model.addAttribute("alumnoId", id);
            model.addAttribute("modoEditar", true);
            return "/alumnos/form";
        }
    }


    @PostMapping("/{id}/edit")
    public String editarAlumnoSubmit(@PathVariable Long id,
                                     @Valid @ModelAttribute("alumno") AlumnoUpdateDto alumno,
                                     BindingResult bindingResult,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el alumno. Por favor, revise los datos.");
            model.addAttribute("alumnoId", id);
            model.addAttribute("modoEditar", true);
            return "/alumnos/form";
        }

        alumnosService.update(id, alumno);
        redirectAttributes.addFlashAttribute("message", "Alumno modificado exitosamente.");
        return "redirect:/alumnos/{id}";
    }

    @GetMapping("/{id}/delete")
    public String borrarAlumno(@PathVariable Long id) {
        // TO DO Borrar con confirmación mediante ventana modal
        alumnosService.deleteById(id);
        return "redirect:/alumnos/lista";
    }

}
