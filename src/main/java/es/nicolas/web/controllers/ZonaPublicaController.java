package es.nicolas.web.controllers;

import es.nicolas.rest.alumnos.dto.AlumnoResponseDto;
import es.nicolas.rest.alumnos.services.AlumnosService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/public")
public class ZonaPublicaController {
    private final AlumnosService alumnosService;

    @GetMapping({"", "/", "/index"})
    public String index(Model model) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        Page<AlumnoResponseDto> alumnosPage = alumnosService.findAll(
                Optional.empty(), Optional.empty(), Optional.empty(), pageable);
        model.addAttribute("tarjetas", alumnosPage);
        return "index";
    }
}
