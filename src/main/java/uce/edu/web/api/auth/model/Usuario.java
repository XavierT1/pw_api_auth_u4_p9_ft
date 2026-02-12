package uce.edu.web.api.auth.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Usuario extends PanacheEntity {
    public String usuario;
    public String password;
    public String rol;
}
