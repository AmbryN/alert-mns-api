package dev.ambryn.alertmntapi.beans;

import jakarta.persistence.*;
import dev.ambryn.alertmntapi.enums.EVisibility;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Entity
@Getter
@Setter
@ToString
@Table(name = "Channel")
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cha_id", nullable = false)
    private Long id;

    @Column(name = "cha_name", nullable = false)
    private String name;

    @Column(name = "cha_visibility", nullable = false)
    private EVisibility visibility;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "is_allowed_in", joinColumns = @JoinColumn(name = "cha_id", referencedColumnName = "cha_id"),
            inverseJoinColumns = @JoinColumn(name = "usr_id", referencedColumnName = "usr_id"))
    @ToString.Exclude
    private Set<User> members = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "group_is_allowed_in", joinColumns = @JoinColumn(name = "cha_id", referencedColumnName =
            "cha_id"), inverseJoinColumns = @JoinColumn(name = "gro_id", referencedColumnName = "gro_id"))
    @ToString.Exclude
    private Set<Group> groups = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "has_subscribed_to", joinColumns = @JoinColumn(name = "cha_id",
            referencedColumnName = "cha_id"), inverseJoinColumns = @JoinColumn(name = "usr_id", referencedColumnName
            = "usr_id"))
    @ToString.Exclude
    private Set<User> subscribers = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "channel")
    @ToString.Exclude
    private List<Message> messages = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "channel")
    @ToString.Exclude
    private List<Meeting> meetings = new ArrayList<>();

    public Channel() {
    }

    public Channel(String name, EVisibility visibility) {
        this.name = name;
        this.visibility = visibility;
    }

    public void addMember(User member) {
        this.members.add(member);
        this.subscribers.add(member);
    }

    public void removeMember(User member) {
        this.members.remove(member);
        this.subscribers.remove(member);
    }

    public void addGroup(Group group) {
        this.groups.add(group);
        group.getMembers()
             .forEach(this::addMember);
    }

    public void removeGroup(Group group) {
        this.groups.remove(group);
        group.getMembers()
             .forEach(this::removeMember);
    }

    public void addSubscriber(User sub) {
        this.subscribers.add(sub);
    }

    public void removeSubscriber(User sub) {
        this.subscribers.remove(sub);
    }

    public void addMeeting(Meeting meeting) {
        this.meetings.add(meeting);
    }

    public void removeMeeting(Meeting meeting) {
        this.meetings.remove(meeting);
    }

    public void addMessage(Message message) {
        this.messages.add(message);
        this.notifySubscribers(message);
    }

    public List<User> getSubscribers() {
        return Collections.unmodifiableList(subscribers.stream()
                                                       .toList());
    }

    public List<User> getMembers() {
        return Collections.unmodifiableList(members.stream()
                                                   .toList());
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(this.messages);
    }

    public List<Meeting> getMeetings() {
        return Collections.unmodifiableList(meetings);
    }

    public void notifySubscribers(Subject notificationSubject) {
        this.subscribers.stream()
                        .filter(user -> user != notificationSubject.getCreator())
                        .forEach(user -> {
                            Notification notification = new Notification(user, notificationSubject);
                            user.addNotification(notification);
                        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return Objects.equals(id, channel.id) && Objects.equals(name, channel.name) && visibility == channel.visibility;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, visibility);
    }
}
