package ru.urfu.mutual_marker.service;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.dto.AddMarkDto;
import ru.urfu.mutual_marker.jpa.entity.*;
import ru.urfu.mutual_marker.jpa.repository.MarkRepository;
import ru.urfu.mutual_marker.jpa.repository.ProjectRepository;
import ru.urfu.mutual_marker.security.exception.UserNotExistingException;
import ru.urfu.mutual_marker.service.exception.MarkServiceException;
import ru.urfu.mutual_marker.service.exception.NotFoundException;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class MarkService {
    MarkRepository markRepository;
    ProjectRepository projectRepository;
    ProfileService profileService;
    ProjectService projectService;

    @Transactional
    public Mark addMark(AddMarkDto addMarkDto){
        Mark mark;
        try {
            Double res = addMarkDto.getMarkStepValues().stream().mapToInt(m -> m).average().orElse(Double.NaN);
            if (res.equals(Double.NaN)){
                log.error("Failed to process components to calculate final mark");
                throw new MarkServiceException("Failed to process markComponents");
            }
            BigDecimal truncation = new BigDecimal(res);
            truncation = truncation.setScale(0, RoundingMode.HALF_UP);
            Profile owner = profileService.getById(addMarkDto.getProfileId());
            Project project = projectRepository.getById(addMarkDto.getProjectId());
            mark = Mark
                    .builder()
                    .student(owner)
                    .project(project)
                    .comment(addMarkDto.getComment())
                    .markValue(truncation.intValue())
                    .build();
        } catch (EntityNotFoundException e){
            log.error("Failed to find project with id {}", addMarkDto.getProjectId());
            throw new MarkServiceException(String.format("Failed to find project with id %s", addMarkDto.getProjectId()));
        } catch (UserNotExistingException e){
            log.error("Failed to find student with id {}", addMarkDto.getProfileId());
            throw new MarkServiceException(String.format("Failed to find student with id %s", addMarkDto.getProfileId()));
        }
        Mark saved =  markRepository.save(mark);
//        Hibernate.initialize(mark.getProject().getMarks());
//        Hibernate.initialize(mark.getProject().getStudent().get);
//        //Hibernate.initialize(mark.getStudent());
        return saved;
    }

    @Transactional
    public Mark saveMark(Mark mark){
        return markRepository.save(mark);
    }

    @Transactional
    public Set<Mark> getAllMarksForProject(Long projectId){
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null){
            log.error("Failed to get all marks for project with id {}", projectId);
            throw new MarkServiceException(String.format("Failed to obtain marks for project with %s", projectId));
        }
        return project.getMarks();
    }

    @Transactional
    public Mark findMarkByProjectAndStudentIds(Long projectId, Long studentId){
        Mark mark = markRepository.findByProjectIdAndStudentId(projectId, studentId).orElse(null);
        if (mark == null){
            log.error("Failed to find mark for project with id {}", projectId);
            throw new MarkServiceException("Failed to find mark");
        }
        return mark;
    }

    @Transactional
    public Mark deleteMarkOnProjectForStudent(Long projectId, Long studentId){
        Mark toDelete = findMarkByProjectAndStudentIds(projectId, studentId);
        toDelete.setDeleted(true);
        markRepository.save(toDelete);
        return toDelete;
    }

    @Transactional
    public Double calculateMarkForProject(Long projectId, Long studentId, int precision){
        double res;
        try {
            Project project = projectRepository.findById(projectId).orElse(null);
            Profile student = profileService.findById(studentId);
            Task task = project.getTask();
            Integer number = student.getNumberOfGradedSet().stream()
                    .filter(n -> Objects.equals(n.getTask().getId(), task.getId())).findFirst().orElse(null).getGraded();
            if (number >= task.getMinNumberOfGraded()) {
                double gradeWithoutPrecision = project.getMarks().stream().mapToInt(Mark::getMarkValue).average().orElse(Double.NaN);
                res = BigDecimal.valueOf(gradeWithoutPrecision).setScale(precision, RoundingMode.HALF_UP).doubleValue();
            } else {
                res = Double.NaN;
            }
        } catch (NotFoundException e){
            log.error("Failed to calculate mark, project not found");
            throw new MarkServiceException(e.getLocalizedMessage());
        } catch (UserNotExistingException e){
            log.error("Failed to calculate mark, user not found");
            throw new MarkServiceException(e.getLocalizedMessage());
        }
        return res;
    }
}
