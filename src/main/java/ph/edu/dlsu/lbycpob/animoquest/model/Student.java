package ph.edu.dlsu.lbycpob.animoquest.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("STUDENT")
public class Student extends User {
    @Column(nullable = false)
    private String major;

    public Student() {
    }

    public Student(String type, Long idNumber, String name, String password, String major) {
        super(type, idNumber, name, password);
        this.major = major;
    }

    // TODO: Add student attributes & behaviors
}