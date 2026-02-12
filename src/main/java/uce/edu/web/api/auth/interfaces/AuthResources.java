package uce.edu.web.api.auth.interfaces;

import io.smallrye.jwt.build.Jwt;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import java.time.Instant;
import java.util.Set;

@Path("/auth")
public class AuthResources {

    @jakarta.inject.Inject
    uce.edu.web.api.auth.repository.UsuarioRepository usuarioRepository;

    @GET
    @Path("/token")
    @Produces(MediaType.APPLICATION_JSON)
    public TokenResponse token(
            @QueryParam("user") @DefaultValue("estudiante1") String user,
            @QueryParam("password") @DefaultValue("estudiante1") String password) {

        uce.edu.web.api.auth.model.Usuario usuario = usuarioRepository.findByUsuarioAndPassword(user, password);

        if (usuario != null) {
            String issuer = "matricula-auth";
            long ttl = 3600;

            Instant now = Instant.now();
            Instant exp = now.plusSeconds(ttl);

            String role = usuario.rol.toLowerCase(); // "Admin" -> "admin" (necesario para la API)

            String jwt = Jwt.issuer(issuer)
                    .subject(user)
                    .groups(Set.of(role))
                    .issuedAt(now)
                    .expiresAt(exp)
                    .sign();

            return new TokenResponse(jwt, exp.getEpochSecond(), role);
        } else {
            return null; // Or throw WebApplicationException(401)
        }

    }

    public static class TokenResponse {
        public String accessToken;
        public long expiresAt;
        public String role;

        public TokenResponse() {
        }

        public TokenResponse(String accessToken, long expiresAt, String role) {
            this.accessToken = accessToken;
            this.expiresAt = expiresAt;
            this.role = role;
        }
    }
}
