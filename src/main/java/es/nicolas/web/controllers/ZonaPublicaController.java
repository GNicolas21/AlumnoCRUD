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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/public")
public class ZonaPublicaController {
    private final AlumnosService alumnosService;

    @GetMapping({"", "/", "/index"})
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "4") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<AlumnoResponseDto> alumnosPage = alumnosService.findAll(
                Optional.empty(), Optional.empty(), Optional.empty(), pageable);
        model.addAttribute("page", alumnosPage);
        return "index";
    }
}
