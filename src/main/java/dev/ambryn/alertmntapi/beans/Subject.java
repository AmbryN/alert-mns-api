package dev.ambryn.alertmntapi.beans;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Table(name = "Subject")
public abstract class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_id", nullable = false)
    protected Long id;

    @Column(name = "sub_sent_at", nullable = false)
    @CreationTimestamp
    protected LocalDateTime sentAt;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "sub_channel", nullable = false)
    protected Channel channel;

    public Subject() {
    }

    public Subject(Channel channel) {
        this.channel = channel;
    }
}
