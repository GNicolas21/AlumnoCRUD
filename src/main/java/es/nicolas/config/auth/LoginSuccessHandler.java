package es.nicolas.config.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  private static final String COOCKIE_NAME = "visitarApp";
  private static final int MAX_AGE = 60 * 60 * 24 * 365; // 1 año

  public LoginSuccessHandler() {
    // Valor por defecto a donde redirigir después de un login exitoso
    setDefaultTargetUrl("/public");
  }

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
    int val = 0;
    Cookie[] cookies =request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (COOCKIE_NAME.equals(cookie.getName())) {
          try {
            val = Integer.parseInt(cookie.getValue());
          } catch (NumberFormatException e) {}
        }
      }
    }
    val++;

    Cookie newCookie = new Cookie(COOCKIE_NAME, Integer.toString(val));
    newCookie.setPath("/");
    newCookie.setMaxAge(MAX_AGE);

    // Debe ser accesible desde JS para la plantilla que muestra el contador
    newCookie.setHttpOnly(false);
    newCookie.setSecure(request.isSecure());
    response.addCookie(newCookie);

    String now = java.time.Instant.now().toString();
    Cookie ultimoLogin = new Cookie("ultimoLogin", URLEncoder.encode(now, StandardCharsets.UTF_8));
    ultimoLogin.setPath("/");
    ultimoLogin.setMaxAge(60 * 60 * 24 * 365);
    ultimoLogin.setHttpOnly(false);
    ultimoLogin.setSecure(request.isSecure());
    response.addCookie(ultimoLogin);

    super.onAuthenticationSuccess(request, response, authentication);
  }
}
