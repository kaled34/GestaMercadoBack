package mx.gob.chiapas.gestamercado.api.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String jwt = extractJwtFromRequest(request);

        if (StringUtils.hasText(jwt) && tokenProvider.validateAccessToken(jwt)) {
            Claims claims = tokenProvider.getClaimsFromToken(jwt);
            UUID userId = UUID.fromString(claims.getSubject());
            String username = claims.get("username", String.class);
            Long municipioId = claims.get("municipioId", Long.class);
            Long mercadoId = claims.get("mercadoId", Long.class);

            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.get("roles", List.class);

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            if (roles != null) {
                for (String rol : roles) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + rol));
                    // Homologación con claves de gesta.rol en PostgreSQL (ADMINISTRADOR, TESORERIA, INSPECTOR, LOCATARIO)
                    switch (rol) {
                        case "ADMINISTRADOR" -> {
                            authorities.add(new SimpleGrantedAuthority("ROLE_SUPERADMIN"));
                            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN_MERCADO"));
                            authorities.add(new SimpleGrantedAuthority("ROLE_COBRADOR"));
                            authorities.add(new SimpleGrantedAuthority("ROLE_CONSULTA"));
                        }
                        case "TESORERIA" -> {
                            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN_MERCADO"));
                            authorities.add(new SimpleGrantedAuthority("ROLE_CONSULTA"));
                        }
                        case "INSPECTOR" -> {
                            authorities.add(new SimpleGrantedAuthority("ROLE_COBRADOR"));
                            authorities.add(new SimpleGrantedAuthority("ROLE_CONSULTA"));
                        }
                        case "LOCATARIO" -> authorities.add(new SimpleGrantedAuthority("ROLE_CONSULTA"));
                        default -> { }
                    }
                }
            }

            GestaUserDetails userDetails = new GestaUserDetails(
                    userId,
                    null,
                    username,
                    authorities,
                    true,
                    false,
                    false,
                    municipioId,
                    mercadoId
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length()).trim();
        }
        return null;
    }
}
