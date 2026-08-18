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
            String username,
            String idNumber,
            String password,
            String firstName,
            String lastName,
            String major
    ) {
        if (!IDValidator.validateID(idNumber)) {
            throw new IllegalArgumentException(
                    "Invalid DLSU ID number."
            );
        }

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Username cannot be empty."
            );
        }

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                    "Password cannot be empty."
            );
        }

        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "First name cannot be empty."
            );
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Last name cannot be empty."
            );
        }

        Long id = Long.parseLong(idNumber);

        if (userRepository.findByIdNumber(id).isPresent()) {
            throw new IllegalArgumentException(
                    "An account with this ID number already exists."
            );
        }

        if (userRepository.existsByUsername(username.trim())) {
            throw new IllegalArgumentException(
                    "That username is already taken."
            );
        }

        Student student = new Student(
                "STUDENT",
                username.trim(),
                id,
                firstName.trim(),
                lastName.trim(),
                password,
                major
        );

        return userRepository.save(student);
    }
    public User login(String idNumber, String password) {

        // Validate ID format
        if (!IDValidator.validateID(idNumber)) {
            throw new IllegalArgumentException(
                    "Invalid DLSU ID number."
            );
        }

        Long id = Long.parseLong(idNumber);

        // Find the user
        User user = userRepository.findByIdNumber(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No account was found with this ID number."
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