package ph.edu.dlsu.lbycpob.animoquest.service;

import org.springframework.stereotype.Service;
import ph.edu.dlsu.lbycpob.animoquest.model.Student;
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
            String name,
            String password,
            String major
    ) {

        // Check if the ID follows the DLSU ID format/checksum
        if (!IDValidator.validateID(idNumber)) {
            throw new IllegalArgumentException("Invalid DLSU ID number.");
        }

        Long id = Long.parseLong(idNumber);

        // Prevent duplicate accounts
        if (userRepository.findByIdNumber(id).isPresent()) {
            throw new IllegalArgumentException(
                    "An account with this ID number already exists."
            );
        }

        // Create the Student account
        Student student = new Student(
                "STUDENT",
                id,
                name,
                password,
                major
        );

        // Save the account to the database
        return userRepository.save(student);
    }
}