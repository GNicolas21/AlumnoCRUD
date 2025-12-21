package es.nicolas.config.auth;

import es.nicolas.auth.services.jwt.JwtService;
import es.nicolas.auth.services.users.AuthUsersService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final AuthUsersService authUsersService;

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        log.debug("Iniciando el filtro de autenticacion");
        final String authHEader = request.getHeader("Authorization");
        final String jwt;
        UserDetails userDetails = null;
        String userName = null;

        // Si no tenemos cabereca o empieza por BEarer no hacemos nada
        if (!StringUtils.hasText(authHEader) || StringUtils.startsWithIgnoreCase(authHEader, "Bearer")) {
            log.info("No se ha encontrado cabecera de autenticación, se ignora");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Se ha encontrado cabecera de autenticación, se procesa");
        // La extraemos y comprobamos que sea válida
        jwt = authHEader.substring(7);
        // Lo primero es que el token es válido
        try {
            userName = jwtService.extractUserName(jwt);
        } catch (Exception e) {
            log.info("Token no válido");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token no autorizado o no válido");
            return;
        }

        log.info("Usuario autenticado: {}", userName);
        if((StringUtils.hasText(userName)) && SecurityContextHolder.getContext().getAuthentication() == null){
            // Comprobamos usuario + token
            log.info("Comprobando usuario y token");
            try {
                userDetails = authUsersService.loadUserByUsername(userName);
            } catch (Exception e){
                log.info("Usuario no encontrado: {}",  userName);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario no autorizado");
                return;
            }
            authUsersService.loadUserByUsername(userName);
            log.info("Usuario autenticado: {}", userDetails);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                log.info("JWT token valido");
                // Si es válido, lo autenticamos en el contexto de seguridad
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                // Añadimos los detalles de la petición
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Lo añadimos al contexto de seguridad
                context.setAuthentication(authToken);

                SecurityContextHolder.setContext(context);
            }
        }

        // Seguimos con la peticion
        filterChain.doFilter(request, response);

    }
}
