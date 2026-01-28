package es.nicolas.web.controllers;

import es.nicolas.rest.user.models.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Collectors;

// Los atributos globales para las vistas
@ControllerAdvice
public class GlobalControllerAdvice {

  @Value("${spring.application.name}")
  private String appName;

  @ModelAttribute("appName")
  public String getAppName() {
    return appName;
  }

  @Value("${application.title}")
  private String appDescription;

  @ModelAttribute("appDescription")
  public String getAppDescription() {
    return appDescription;
  }

  @ModelAttribute("currentUser")
  public User getCurrentUser(Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
      return (User) authentication.getPrincipal();
    }
    return null;
  }

  @ModelAttribute("isAuthenticated")
  public boolean isAuthenticated(Authentication authentication) {
    return authentication != null && authentication.isAuthenticated()
      && !(authentication.getPrincipal() instanceof String);
  }

  // ⭐ AÑADIR MÉTODO HELPER PARA ADMIN ⭐
  @ModelAttribute("isAdmin")
  public boolean isAdmin(Authentication authentication) {
      if (authentication != null && authentication.isAuthenticated()
              && !(authentication.getPrincipal() instanceof String)) {
          User user = (User) authentication.getPrincipal();
        // comprobar si ADMIN está en la lista de roles del usuario
        return user.getRoles().stream()
          .anyMatch(role -> role.toString().equals("ADMIN"));
      }
      return false;
  }


  @ModelAttribute("username")
  public String getUsername(Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()
      && !(authentication.getPrincipal() instanceof String)) {
      User user = (User) authentication.getPrincipal();
      return user.getNombre() + " " + user.getApellidos();
    }
    return null;
  }


  @ModelAttribute("userRoles")
  public String getUserRole(Authentication authentication) {
      if (authentication != null && authentication.isAuthenticated()
              && !(authentication.getPrincipal() instanceof String)) {
          User user = (User) authentication.getPrincipal();
          return user.getRoles().stream().map(Objects::toString).collect(Collectors.joining(","));
      }
      return null;
  }


  @ModelAttribute("csrfToken")
  public String getCsrfToken(HttpServletRequest request) {
    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    return csrfToken != null ? csrfToken.getToken() : "";
  }

  @ModelAttribute("csrfParamName")
  public String getCsrfParamName(HttpServletRequest request) {
    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    return csrfToken != null ? csrfToken.getParameterName() : "_csrf";
  }

  @ModelAttribute("csrfHeaderName")
  public String getCsrfHeaderName(HttpServletRequest request) {
    CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    return csrfToken != null ? csrfToken.getHeaderName() : "X-CSRF-TOKEN";
  }

  @ModelAttribute("currentDateTime")
  public java.time.LocalDateTime getCurrentDateTime() {
    return java.time.LocalDateTime.now();
  }

  @ModelAttribute("currentYear")
  public int getCurrentYear() {
    return java.time.LocalDate.now().getYear();
  }

  @ModelAttribute("currentMonth")
  public String getCurrentMonth() {
    return java.time.LocalDate.now().getMonth().getDisplayName(
      java.time.format.TextStyle.FULL,
      new java.util.Locale("es", "ES")
    );
  }

}
