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
@Table(name = "File")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fil_id", nullable = false)
    private Long id;

    @Column(name = "fil_name")
    private String name;

    @Column(name = "fil_path")
    private String path;

    protected File() {}

    public File(String name, String path) {
        this.name = name;
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return id.equals(file.id) && name.equals(file.name) && path.equals(file.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, path);
    }
}
