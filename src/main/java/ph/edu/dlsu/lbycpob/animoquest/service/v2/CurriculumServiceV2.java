package ph.edu.dlsu.lbycpob.animoquest.service.v2;

import org.springframework.stereotype.Service;
import ph.edu.dlsu.lbycpob.animoquest.model.v2.CurriculumProgressV2;
import ph.edu.dlsu.lbycpob.animoquest.model.Student;
import ph.edu.dlsu.lbycpob.animoquest.repository.v2.CurriculumRepositoryV2;

import java.util.List;

@Service
public class CurriculumServiceV2 {

    private final CurriculumRepositoryV2 progressRepository;

    public CurriculumServiceV2(CurriculumRepositoryV2 progressRepository) {
        this.progressRepository = progressRepository;
    }

    public List<CurriculumProgressV2> getProgressOf(Long studentId) {
        return progressRepository.findByStudentId(studentId);
    }
}
