package es.nicolas.web.controllers;

import es.nicolas.rest.alumnos.models.Alumno;
import es.nicolas.rest.alumnos.services.AlumnosService;
import es.nicolas.rest.user.services.UserService;
import es.nicolas.rest.user.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("app")
public class AlumnosController {
  private final AlumnosService alumnosService;
  private final UserService userService;

  @ModelAttribute("alumnos")
  public List<Alumno> misAlumnos() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Optional<User> usuario = userService.findByUsername(username);
    if (usuario.isEmpty()) {
      return List.of();
    }
    return alumnosService.buscarPorUsuarioId(usuario.get().getId());
  }

  @GetMapping("/misalumnos")
  public String list() {
    return "app/alumnos/lista";
  }

  @GetMapping("/misalumnos/{id}")
  public String getById(@PathVariable Long id, Model model) {
    Alumno alumno = alumnosService.buscarPorId(id).orElse(null);
    model.addAttribute("alumno", alumno);
    return "app/alumnos/detalle";
  }

}
