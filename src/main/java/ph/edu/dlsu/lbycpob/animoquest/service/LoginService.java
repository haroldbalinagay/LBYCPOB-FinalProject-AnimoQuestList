package ph.edu.dlsu.lbycpob.animoquest.service;

import org.springframework.stereotype.Service;
import ph.edu.dlsu.lbycpob.animoquest.model.Student;
import ph.edu.dlsu.lbycpob.animoquest.model.User;
import ph.edu.dlsu.lbycpob.animoquest.repository.UserRepository;
import ph.edu.dlsu.lbycpob.animoquest.util.IDValidator;

@Service
public class LoginService {

    private final UserRepository userRepository;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ============================================================
    // SIGN UP
    // ============================================================

    public Student addStudentAccount(
            String idNumber,
            String password,
            String firstName,
            String middleName,
            String lastName,
            String major
    ) {

        // Validate ID number
        if (!IDValidator.validateID(idNumber)) {
            throw new IllegalArgumentException(
                    "Invalid DLSU ID number."
            );
        }

        // Validate password
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                    "Password cannot be empty."
            );
        }

        // Validate first name
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "First name cannot be empty."
            );
        }

        // Validate middle name
        if (middleName == null || middleName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Middle name cannot be empty."
            );
        }

        // Validate last name
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Last name cannot be empty."
            );
        }

        // Validate major
        if (major == null || major.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Major cannot be empty."
            );
        }

        Long id = Long.parseLong(idNumber);

        // Check if ID number already exists
        if (userRepository.findByIdNumber(id).isPresent()) {
            throw new IllegalArgumentException(
                    "An account with this ID number already exists."
            );
        }

        // Clean up the names
        String cleanFirstName = firstName.trim();
        String cleanMiddleName = middleName.trim();
        String cleanLastName = lastName.trim();

        // Automatically create username
        String username =
                cleanFirstName + " "
                        + cleanMiddleName + " "
                        + cleanLastName;

        // Check if generated username already exists
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "An account with this name already exists."
            );
        }

        // Create Student
        Student student = new Student(
                "STUDENT",
                username,
                id,
                cleanFirstName,
                cleanMiddleName,
                cleanLastName,
                password,
                major.trim()
        );

        // Save to database
        return userRepository.save(student);
    }


    // ============================================================
    // LOGIN
    // ============================================================

    public User login(
            String username,
            String idNumber,
            String password
    ) {

        // Validate ID number
        if (!IDValidator.validateID(idNumber)) {
            throw new IllegalArgumentException(
                    "Invalid DLSU ID number."
            );
        }

        // Validate username
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Username cannot be empty."
            );
        }

        // Validate password
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                    "Password cannot be empty."
            );
        }

        Long id = Long.parseLong(idNumber);

        // Find account using username + ID
        User user = userRepository
                .findByUsernameAndIdNumber(
                        username.trim(),
                        id
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No account was found with those credentials."
                        )
                );

        // Check password
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException(
                    "Incorrect password."
            );
        }

        return user;
    }
}