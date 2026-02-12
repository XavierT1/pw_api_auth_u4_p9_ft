package uce.edu.web.api.auth.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import uce.edu.web.api.auth.model.Usuario;

@ApplicationScoped
public class UsuarioRepository implements PanacheRepository<Usuario> {
    public Usuario findByUsuarioAndPassword(String usuario, String password) {
        return find("usuario = ?1 and password = ?2", usuario, password).firstResult();
    }
}
