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
