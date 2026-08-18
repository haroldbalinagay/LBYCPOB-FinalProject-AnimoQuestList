package ph.edu.dlsu.lbycpob.animoquest.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "user_type",
        discriminatorType = DiscriminatorType.STRING
)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "user_type",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private String type;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "id_number", nullable = false, unique = true)
    private Long idNumber;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public User() {
    }

    public User(
            String type,
            String username,
            Long idNumber,
            String firstName,
            String lastName,
            String password
    ) {
        this.type = type;
        this.username = username;
        this.idNumber = idNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }
}