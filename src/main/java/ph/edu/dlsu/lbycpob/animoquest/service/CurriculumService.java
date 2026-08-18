package ph.edu.dlsu.lbycpob.animoquest.service;

import org.springframework.stereotype.Service;
import ph.edu.dlsu.lbycpob.animoquest.model.CurriculumProgress;
import ph.edu.dlsu.lbycpob.animoquest.model.Student;
import ph.edu.dlsu.lbycpob.animoquest.repository.CurriculumRepository;

import java.util.List;

@Service
public class CurriculumService {
    private final CurriculumRepository progressRepository;

    public CurriculumService(CurriculumRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    public List<CurriculumProgress> getProgressOf(Student student) {
        Long temp = 1L;
        return progressRepository.findByStudentId(temp);
    }
}
