package com.example.monicio.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * User entity.
 *
 * @author HukoJlauII, Nikita Zhiznevskiy
 */
@Table(name = "user")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    /**
     * The Id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * The Username.
     */
    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 3, message = "Никнейм не может содержать менее 3-ёх символов")
    @Size(max = 20, message = "Слишком длинный никнейм")
    @Column(name = "username", unique = true, length = 20)
    private String username;

    /**
     * The Email.
     */
    @Email(message = "Поле должно иметь формат эл.почты")
    @NotBlank(message = "Поле не может быть пустым")
    @Column(name = "email", unique = true, length = 40)
    private String email;


    /**
     * The Name.
     */
    @NotBlank(message = "Поле не должно быть путсым")
    @Column(name = "name")
    private String name;


    /**
     * The Surname.
     */
    @NotBlank(message = "Поле не должно быть пустым")
    @Column(name = "surname")
    private String surname;


    /**
     * The Password.
     */
    @NotBlank(message = "Поле не может быть пустым")
    @Size(min = 8, message = "Пароль должен содержать минимум 8 символов")
    @Column(name = "password")
    @JsonIgnore
    private String password;

    /**
     * The Password confirm.
     */
    @Transient
    @JsonIgnore
    private String passwordConfirm;

    /**
     * The Avatar.
     */
    @OneToOne(targetEntity = Media.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "avatar_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = "bytes", allowSetters = true)
    @RestResource(exported = false)
    private Media avatar;

    /**
     * The Active.
     */
    @Column(name = "active")
    private boolean active;

    /**
     * The Projects.
     */
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_project",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "project_id", referencedColumnName = "id")}
    )
    private Set<Project> projects = new HashSet<>();

    /**
     * The Authorities.
     */
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> authorities = new HashSet<>();


    /**
     * Gets authorities.
     *
     * @return the authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Is account non expired boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Is account non locked boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Is credentials non expired boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Is enabled boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Equals boolean.
     *
     * @param o the o
     * @return the boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    /**
     * Hash code int.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}