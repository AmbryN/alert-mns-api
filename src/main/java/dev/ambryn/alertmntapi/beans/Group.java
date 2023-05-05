package dev.ambryn.alertmntapi.beans;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.text.StringEscapeUtils;

import java.util.*;

@Entity
@Getter
@Setter
@ToString
@Table(name = "User_Group")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gro_id", nullable = false)
    private Long id;

    @Column(name = "gro_name", nullable = false, unique = true)
    private String name;

    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinTable(name = "is_member_of",
            joinColumns = @JoinColumn(name = "gro_id", referencedColumnName = "gro_id"),
            inverseJoinColumns = @JoinColumn(name = "usr_id", referencedColumnName = "usr_id")
    )
    @ToString.Exclude
    private Set<User> members = new HashSet<>();

    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    public void addMember(User user) {
        this.members.add(user);
    }

    public void removeMember(User user) {
        this.members.remove(user);
    }

    public void setName(String name) {
        this.name = StringEscapeUtils.escapeHtml4(name).trim();
    }

    public List<User> getMembers() {
        return Collections.unmodifiableList(members.stream().toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(id, group.id) && Objects.equals(name, group.name) && Objects.equals(members, group.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, members);
    }
}
