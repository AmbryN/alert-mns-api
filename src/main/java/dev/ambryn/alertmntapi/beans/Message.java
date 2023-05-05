package dev.ambryn.alertmntapi.beans;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Table(name = "Message")
public class Message extends Subject {

    @ManyToOne
    @JoinColumn(name = "msg_sender", nullable = false)
    private User sender;

    @Column(name = "msg_content", nullable = false)
    private String content;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "msg_file", nullable = true)
    private File file;

    public Message() {
        super();
    }

    public Message(Channel channel, User sender, String content) {
        this(channel, sender, content, null);
    }

    public Message(Channel channel, User sender, String content, File file) {
        super(channel);
        this.sender = sender;
        this.content = content;
        this.file = file;
    }

    public void setContent(String content) {
        this.content = StringEscapeUtils.escapeHtml4(content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(sender, message.sender)
                && Objects.equals(content, message.content)
                && Objects.equals(file, message.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, content, file);
    }
}