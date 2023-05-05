package dev.ambryn.alertmntapi.beans;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Table(name = "Notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "not_id", nullable = false)
    private Long id;

    @Column(name = "not_seen", nullable = false)
    private boolean seen;

    @OneToOne(targetEntity = Subject.class)
    @JoinColumn(name = "not_subject", nullable = false)
    private Subject subject;

    public Notification() {}

    public Notification(Subject sub) {
        this.subject = sub;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return seen == that.seen && Objects.equals(id, that.id) && Objects.equals(subject, that.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, seen, subject);
    }
}
