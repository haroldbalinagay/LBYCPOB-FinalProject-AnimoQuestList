package ph.edu.dlsu.lbycpob.animoquest.service.v2;

import org.springframework.stereotype.Service;
import ph.edu.dlsu.lbycpob.animoquest.model.v2.CurriculumProgress;
import ph.edu.dlsu.lbycpob.animoquest.model.Student;
import ph.edu.dlsu.lbycpob.animoquest.repository.v2.CurriculumRepository;

import java.util.List;

@Service
public class CurriculumService {

    private final CurriculumRepository progressRepository;

    public CurriculumService(CurriculumRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    public List<CurriculumProgress> getProgressOf(Student student) {
        return progressRepository.findByStudentId(1L);
    }
}
