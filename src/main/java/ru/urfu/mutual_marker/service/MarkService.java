package ru.urfu.mutual_marker.service;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.urfu.mutual_marker.dto.AddMarkStepDto;
import ru.urfu.mutual_marker.jpa.entity.Mark;
import ru.urfu.mutual_marker.jpa.repository.MarkRepository;
import ru.urfu.mutual_marker.jpa.repository.MarkStepRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class MarkService {
    MarkRepository markRepository;

    public void saveMark(Mark mark){
        markRepository.save(mark);
    }

    public void calculateAndSaveFinalMark(Mark mark, List<Integer> markComponents){
        Double res = markComponents.stream().mapToInt(m -> m).average().orElse(Double.NaN);
        if (res.equals(Double.NaN)){
            throw new RuntimeException("Failed to process markComponents"); //TODO продумать ошибку или убрать проверку
        }

        BigDecimal truncation = new BigDecimal(res);
        truncation = truncation.setScale(0, RoundingMode.HALF_UP);
        mark.setMarkValue(truncation.intValue());
        markRepository.save(mark);
    }

    public Mark findMarkByProjectAndStudentIds(Long projectId, Long studentId){
        Mark mark = markRepository.findByProjectIdAndStudentId(projectId, studentId).orElse(null);
        if (mark == null){
            log.error("Failed to find mark for project with id {}", projectId);
            throw new RuntimeException("Failed to find mark"); //TODO write custom exception
        }
        return mark;
    }

    public void deleteMarkOnProjectForStudent(Long projectId, Long studentId){
        Mark toDelete = findMarkByProjectAndStudentIds(projectId, studentId);
        toDelete.setDeleted(true);
        markRepository.save(toDelete);
    }
}
