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

        // Clean up the names
        String cleanFirstName = firstName.trim();
        String cleanMiddleName = middleName.trim();
        String cleanLastName = lastName.trim();

        /*
         * Automatically create the username
         * using the student's complete name.
         *
         * Example:
         * Juan + Dela + Cruz
         * = "Juan Dela Cruz"
         */
        String username = cleanFirstName
                + " "
                + cleanMiddleName
                + " "
                + cleanLastName;

        // Convert ID from String to Long
        Long id = Long.parseLong(idNumber);

        // Check if ID already exists
        if (userRepository.findByIdNumber(id).isPresent()) {
            throw new IllegalArgumentException(
                    "An account with this ID number already exists."
            );
        }

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

        // Save to Supabase
        return userRepository.save(student);
    }

