package uce.edu.web.api.auth.interfaces;

import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import uce.edu.web.api.auth.domain.Usuario;
import uce.edu.web.api.auth.repository.UsuarioRepository;

import java.time.Instant;
import java.util.Set;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    UsuarioRepository usuarioRepository;

    @ConfigProperty(name = "auth.issuer")
    String issuer;

    @ConfigProperty(name = "auth.token.ttl")
    Long ttl;

    @GET
    @Path("/token")
    @Transactional
    public Response token(
            @QueryParam("user") String user,
            @QueryParam("password") String password) {

        Usuario usuario = usuarioRepository.findByUsernameAndPassword(user, password);

        if (usuario != null) {
            Instant now = Instant.now();
            Instant exp = now.plusSeconds(ttl);

            // Generación del token usando el rol de la base de datos
            String jwt = Jwt.issuer(issuer)
                    .subject(user)
                    .groups(Set.of(usuario.role)) 
                    .issuedAt(now)
                    .expiresAt(exp)
                    .sign();

            return Response.ok(new TokenResponse(jwt, exp.getEpochSecond(), usuario.role)).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    public static class TokenResponse {
        public String accessToken;
        public long expiresAt;
        public String role;

        // Constructor vacío obligatorio para que JSON-B pueda serializar el objeto
        public TokenResponse() {
        }

        public TokenResponse(String accessToken, long expiresAt, String role) {
            this.accessToken = accessToken;
            this.expiresAt = expiresAt;
            this.role = role;
        }
    }
}