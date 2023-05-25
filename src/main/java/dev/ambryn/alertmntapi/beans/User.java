package dev.ambryn.alertmntapi.beans;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.*;

@Entity
@Getter
@Setter
@ToString
@Table(name = "User", uniqueConstraints = @UniqueConstraint(name = "email", columnNames = {"usr_email"}))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usr_id", nullable = false)
    private Long id;

    @Column(name = "usr_email", nullable = false)
    @NotNull(message = "ne peut être vide")
    @Pattern(regexp = "^[a-zA-Z0-9-_]+\\.*[a-zA-Z0-9-_]*@([a-zA-Z0-9]+\\.{1})+([a-zA-Z]){2,3}$", message = "doit " +
            "être" + " un email valide")
    private String email;

    @Column(name = "usr_password", nullable = false)
    @NotNull(message = "ne peut être vide")
    @Size(min = 8, max = 200)
    @NotBlank
    private String password;

    @Column(name = "usr_lastname", nullable = false)
    @NotNull(message = "ne peut être vide")
    @NotBlank
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[a-zA-ZàâçéèếïîôöùûüÀÂÇÉÈẾÏÎÔÖÙÛÜ -]+$", message = "ne doit pas contenir de caractères " +
            "spéciaux")
    private String lastname;

    @Column(name = "usr_firstname", nullable = false)
    @NotNull(message = "ne peut être vide")
    @NotBlank(message = "doit contenir des characters autre que espace, tabulation etc.")
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[a-zA-ZàâçéèếïîôöùûüÀÂÇÉÈẾÏÎÔÖÙÛÜ -]+$", message = "ne doit pas contenir de caractères " +
            "spéciaux")
    private String firstname;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "has_role", joinColumns = @JoinColumn(name = "usr_id", referencedColumnName = "usr_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id", referencedColumnName = "rol_id"))
    @ToString.Exclude
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "members")
    @ToString.Exclude
    private Set<Channel> channels = new HashSet<>();

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, mappedBy = "receiver")
    @ToString.Exclude
    private Set<Notification> queue = new HashSet<>();

    public User() {
    }

    public User(String email, String password, String lastname, String firstname) {
        setEmail(email);
        setPassword(password);
        setLastname(lastname);
        setFirstname(firstname);
        this.roles = new HashSet<>();
        this.queue = new HashSet<>();
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = new HashSet<>(roles);
    }

    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public void addNotification(Notification notification) {
        this.queue.add(notification);
    }

    public Set<Notification> getNotifications() {
        return Collections.unmodifiableSet(queue);
    }

    public void setEmail(String email) {
        this.email = StringEscapeUtils.escapeHtml4(email.trim()
                                                        .toLowerCase());
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLastname(String lastname) {
        this.lastname = StringEscapeUtils.escapeHtml4(lastname.trim()
                                                              .toUpperCase());
    }

    public void setFirstname(String firstname) {
        this.firstname = StringEscapeUtils.escapeHtml4(StringUtils.capitalize(firstname.trim()));
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(password,
                                                                                                  user.password) && Objects.equals(
                lastname,
                user.lastname) && Objects.equals(firstname, user.firstname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, lastname, firstname, roles, queue);
    }
}