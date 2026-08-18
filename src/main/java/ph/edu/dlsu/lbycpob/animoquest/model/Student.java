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

    public Student(
            String type,
            String username,
            Long idNumber,
            String firstName,
            String middleName,
            String lastName,
            String password,
            String major
    ) {
        super(
                type,
                username,
                idNumber,
                firstName,
                middleName,
                lastName,
                password
        );

        this.major = major;
    }
}