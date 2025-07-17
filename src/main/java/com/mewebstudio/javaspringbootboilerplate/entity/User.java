package com.mewebstudio.javaspringbootboilerplate.entity;

import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email"}, name = "uk_users_email")
}, indexes = {
    @Index(columnList = "name", name = "idx_users_name"),
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractBaseEntity {
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "avatar", columnDefinition = "text")
    private String avatar;

    private Integer semestre;

    private TipoUsuario tipoUsuario;

    private String biografia;

    //TODO: implementar validação de CPF e implementar restrição de banco (unique e nullable = false)
    private String cpf;

    @Lob
    @Column(name = "foto_perfil")
    private String fotoPerfil;
    
    private String curso;

    // Deve seguir esse padrão: Pampulha, Belo Horizonte - MG / <BAIRRO>, <CIDADE> - <ESTADO>
    private String regiaoDeInteresse;

    @OneToMany(mappedBy = "anunciante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Anuncio> anuncios = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private Set<Anuncio> anunciosFavoritos = new HashSet<>();

    @ElementCollection(targetClass = InteressesUsuario.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "user_interesses", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "interesses", nullable = false)
    @Size(max = 5, message = "{max_list_size}")
    @Builder.Default
    private Set<InteressesUsuario> interesses = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(
            name = "user_id",
            foreignKey = @ForeignKey(
                name = "fk_user_roles_user_id",
                foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE"
            ),
            nullable = false
        ),
        inverseJoinColumns = @JoinColumn(
            name = "role_id",
            foreignKey = @ForeignKey(
                name = "fk_user_roles_role_id",
                foreignKeyDefinition = "FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE"
            ),
            nullable = false
        ),
        uniqueConstraints = {
            @UniqueConstraint(
                columnNames = {"user_id", "role_id"},
                name = "uk_user_roles_user_id_role_id"
            )
        }
    )
    @Builder.Default
    private List<Role> roles = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private EmailVerificationToken emailVerificationToken;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private PasswordResetToken passwordResetToken;

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;

    @Column(name = "blocked_at")
    private LocalDateTime blockedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "privacidade_perfil")
    @Builder.Default
    private PrivacidadePerfil privacidadePerfil = PrivacidadePerfil.PUBLICO;

    @ManyToMany
    @JoinTable(
            name = "user_anuncios_vistos",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "anuncio_id")
    )
    @Builder.Default
    private Set<Anuncio> anunciosVistosRecentemente = new LinkedHashSet<>();

    /**
     * Get full name of user.
     *
     * @return String
     */
    public String getFullName() {
        return this.name;
    }
}
