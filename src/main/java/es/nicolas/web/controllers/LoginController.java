package es.nicolas.web.controllers;

import es.nicolas.rest.user.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class LoginController {

    @GetMapping("/")
    public String welcome() {
        return "redirect:/public/";
    }

    @GetMapping("/auth/login")
    public String login(Model model) {
        // CSRF token is handled by GlobalControllerAdvice
        // Para el formulario de registro
        model.addAttribute("usuario", new User());
        return "login";
    }

}
