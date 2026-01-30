package es.nicolas.web.controllers;

import es.nicolas.rest.alumnos.dto.AlumnoCreateDto;
import es.nicolas.rest.alumnos.dto.AlumnoResponseDto;
import es.nicolas.rest.alumnos.dto.AlumnoUpdateDto;
import es.nicolas.rest.alumnos.models.Alumno;
import es.nicolas.rest.alumnos.services.AlumnosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
  private final AlumnosService alumnosService;

  @GetMapping("/alumnos")
  public String alumnos(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "4") int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
    Page<AlumnoResponseDto> alumnosPage = alumnosService.findAll(
      Optional.empty(), Optional.empty(), Optional.empty(), pageable);
    model.addAttribute("page", alumnosPage);
    return "admin/alumnos/lista";
  }

  @GetMapping("/alumnos/{id}")
  public String getById(@PathVariable Long id, Model model) {
    var alumno = alumnosService.buscarPorId(id).orElse(null);
    model.addAttribute("alumno", alumno);
    return "admin/alumnos/detalle";
  }

  // Creamos un nuevo alumno
  @GetMapping("/alumnos/new")
  public String nuevoAlumnoForm(Model model) {
    model.addAttribute("alumno", AlumnoCreateDto.builder().build());
    model.addAttribute("modoEditar", false);
    return "admin/alumnos/form";
  }

  @PostMapping("/alumnos/new")
  public String crearAlumno(@ModelAttribute("alumno") AlumnoCreateDto alumnoCreateDto, BindingResult result, Model model) {
    log.info("Datos recibidos del formulario: {}", alumnoCreateDto);
    if (result.hasErrors()) {
      log.info("Errores en el formulario: {}", alumnoCreateDto);
      model.addAttribute("modoEditar", false);
      return "admin/alumnos/form";
    } else {
      alumnosService.save(alumnoCreateDto);
      return "redirect:/admin/alumnos";
    }
  }

  @GetMapping("/alumnos/{id}/edit")
  public String editarAlumnoForm(@PathVariable Long id, Model model) {
    Alumno alumnoEncontrado = alumnosService.buscarPorId(id).orElse(null);
    if (alumnoEncontrado == null) {
      return "redirect:/admin/alumnos/new";
    } else {
      AlumnoUpdateDto alumnoUpdateDto = AlumnoUpdateDto.builder()
        .nombre(alumnoEncontrado.getNombre())
        .apellido(alumnoEncontrado.getApellido())
        .grado(alumnoEncontrado.getGrado())
        .asignatura(alumnoEncontrado.getAsignatura().getNombre())
        .build();
      model.addAttribute("alumno", alumnoUpdateDto);
      model.addAttribute("modoEditar", id);
      model.addAttribute("modoEditar", true);
      return "admin/alumnos/form";
    }
  }

  @PostMapping("/alumnos/{id}/edit")
  public String actualizarAlumno(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("alumno") AlumnoUpdateDto alumnoUpdateDto,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
    log.info("Datos recibidos del formulario para actualizar: {}", alumnoUpdateDto);
    if (result.hasErrors()) {
      log.info("Errores en el formulario al actualizar: {}", alumnoUpdateDto);
      redirectAttributes.addFlashAttribute("error", "Errores en el formulario. Por favor, corríjalos.");
      model.addAttribute("alumnoId", id);
      model.addAttribute("modoEditar", true);
      return "admin/alumnos/form";
    }
    alumnosService.update(id, alumnoUpdateDto);
    redirectAttributes.addFlashAttribute("message", "Alumno actualizado correctamente.");
    return "redirect:/admin/alumnos/{id}";
  }

  @GetMapping("/alumnos/{id}/delete")
  public String borrarAlumno(@PathVariable Long id) {
    alumnosService.deleteById(id);
    return "redirect:/admin/alumnos";
  }

}
