package ru.urfu.mutual_marker.service.mark;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.jpa.entity.*;
import ru.urfu.mutual_marker.jpa.repository.ProjectRepository;
import ru.urfu.mutual_marker.jpa.repository.mark.MarkRepository;
import ru.urfu.mutual_marker.security.exception.UserNotExistingException;
import ru.urfu.mutual_marker.service.exception.NotFoundException;
import ru.urfu.mutual_marker.service.exception.mark.MarkServiceException;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MarkCalculator {

    ProjectRepository projectRepository;
    MarkRepository markRepository;

    public Double calculateMarkForProjectByTask(Long taskId, Long student, int precision) {
        Optional<Project> projectOptional = projectRepository.findByStudentIdAndTaskIdAndDeletedIsFalse(student, taskId);
        if (projectOptional.isPresent()) {
            var project = projectOptional.get();
            log.info("calculate mark for project {} student {}", project.getId(), student);
            Task task = project.getTask();
            if (task == null){
                log.error("Failed to calculate mark, not task found for project with id {}", project.getId());
                throw new MarkServiceException(String.format("Failed to find task for project with id %s", project.getId()));
            }
            if (task.getCloseDate().isAfter(LocalDateTime.now())){
                return calculateBeforeCloseDate(project, task, precision);
            }
            return calculateAfterCloseDate(project, task, precision);
        }
        return Double.NaN;
    }

    public Double calculateMarkForProject(Project project){

        try {
            Task task = project.getTask();
            if (task == null){
                log.error("Failed to calculate mark, not task found for project with id {}", project.getId());
                throw new MarkServiceException(String.format("Failed to find task for project with id %s", project.getId()));
            }
            if (task.getCloseDate().isAfter(LocalDateTime.now())){
                return calculateBeforeCloseDate(project, task, 2);
            }
            return calculateAfterCloseDate(project, task, 2);

        } catch (NotFoundException e){
            log.error("Failed to calculate mark, project not found");
            throw new MarkServiceException(e.getLocalizedMessage());
        } catch (UserNotExistingException e){
            log.error("Failed to calculate mark, user not found");
            throw new MarkServiceException(e.getLocalizedMessage());
        }
    }
    @Transactional
    public Double calculateMarkForProject(Project project, int precision){

        try {
            Task task = project.getTask();
            if (task == null){
                log.error("Failed to calculate mark, not task found for project with id {}", project.getId());
                throw new MarkServiceException(String.format("Failed to find task for project with id %s", project.getId()));
            }
            if (task.getCloseDate().isAfter(LocalDateTime.now())){
                return calculateBeforeCloseDate(project, task, precision);
            }
            return calculateAfterCloseDate(project, task, precision);

        } catch (NotFoundException e){
            log.error("Failed to calculate mark, project not found");
            throw new MarkServiceException(e.getLocalizedMessage());
        } catch (UserNotExistingException e){
            log.error("Failed to calculate mark, user not found");
            throw new MarkServiceException(e.getLocalizedMessage());
        }
    }

    public double calculateBeforeCloseDate(Project project, Task task, int precision){
        long numberOfMarkedByStudent = markRepository.countAllByOwnerIdAndProjectTaskId(project.getStudent().getId(), task.getId());
        if (numberOfMarkedByStudent >= task.getMinNumberOfGraded()){
            return calculateAndScaleToHundred(project, precision);
        }
        log.debug("Number of marked works for student with id {} in task with id {} is not enough to calculate mark", project.getStudent().getId(), task.getId());
        return Double.NaN;
    }

    public double calculateAfterCloseDate(Project project, Task task, int precision){
        if (task.getCloseDate().isAfter(project.getCompletionDate())){
            if (project.getMarks().size() < task.getMinNumberOfGraded()) {
                log.debug("Number of marked works for student with id {} in task with id {} is not enough to calculate mark", project.getStudent().getId(), task.getId());
                return Double.NaN;
            } else{
                return calculateAndScaleToHundred(project, precision);
            }
        } else {
            log.debug("Project with id {} is created after task close date", project.getId());
            return Double.NaN;
        }
    }

    public double calculate(Project project, int precision){
        double teachersMark = 0;
        double studentsMark = 0;
        double teacherCoefficient = 0;
        Set<Mark> teacherMarks = project.getMarks().stream().filter(Mark::getIsTeacherMark).collect(Collectors.toSet());
        Set<Mark> studentMarks = project.getMarks().stream().filter(mark -> !mark.getIsTeacherMark()).collect(Collectors.toSet());
        for (Mark mark : teacherMarks) {
            teachersMark += mark.getMarkValue();
            teacherCoefficient+=mark.getCoefficient();
        }
        studentsMark = studentMarks.stream().mapToInt(Mark::getMarkValue).average().orElse(0);
        teachersMark = teacherMarks.stream().mapToInt(Mark::getMarkValue).average().orElse(0);
        if(teacherMarks.size() != 0){
            teacherCoefficient = (teacherCoefficient/teacherMarks.size());
            teachersMark = teachersMark * teacherCoefficient;
        }
        if(studentMarks.size() != 0){
            studentsMark = studentsMark * (1d - teacherCoefficient);
        }
        log.debug("[MARK SERVICE] Calculated mark: teachers mark: {} \n students mark: {}", teachersMark, studentsMark);

        return BigDecimal.valueOf(teachersMark + studentsMark).setScale(precision, RoundingMode.HALF_UP).doubleValue();
    }

    public double calculateAndScaleToHundred(Project project, int precision){
        BigDecimal calculateResult = BigDecimal.valueOf(calculate(project, precision));
        BigDecimal maxMark = BigDecimal.valueOf(project.getTask().getMarkSteps()
                .stream()
                .map(MarkStep::getValues)
                .flatMapToInt(values -> values.stream().mapToInt(MarkStepValue::getValue))
                .sum());
        BigDecimal result = calculateResult.compareTo(BigDecimal.ZERO) == 0
                ? calculateResult.divide(maxMark, precision, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;
        return result.setScale(precision, RoundingMode.HALF_UP).doubleValue();
    }
}
