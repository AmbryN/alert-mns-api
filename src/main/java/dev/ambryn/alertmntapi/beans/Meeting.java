package dev.ambryn.alertmntapi.beans;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.text.StringEscapeUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@Table(name = "Meeting")
public class Meeting extends Subject {

    @Column(name = "mee_name")
    private String name;

    @Column(name = "mee_datetime")
    private LocalDateTime dateTime;

    @Column(name = "mee_duration")
    private int duration;

    public Meeting() {

    }

    public void setName(String name) {
        this.name = StringEscapeUtils.escapeHtml4(name.trim());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meeting meeting = (Meeting) o;
        return duration == meeting.duration && Objects.equals(name, meeting.name) && Objects.equals(dateTime,
                                                                                                    meeting.dateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dateTime, duration);
    }
}
