package ru.urfu.mutual_marker.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.urfu.mutual_marker.jpa.entity.Mark;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Project;
import ru.urfu.mutual_marker.jpa.entity.Task;
import ru.urfu.mutual_marker.jpa.repository.MarkRepository;
import ru.urfu.mutual_marker.jpa.repository.ProjectRepository;
import ru.urfu.mutual_marker.security.exception.UserNotExistingException;
import ru.urfu.mutual_marker.service.exception.MarkServiceException;
import ru.urfu.mutual_marker.service.exception.NotFoundException;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MarkCalculator {

    ProjectRepository projectRepository;
    ProjectService projectService;
    ProfileService profileService;
    MarkRepository markRepository;

    public Double calculateMarkForProjectByTask(Long taskId, Long student, int precision) {
        Optional<Project> projectOptional = projectRepository.findByStudentIdAndTaskId(student, taskId);
        if (projectOptional.isPresent()) {
            var project = projectOptional.get();
            log.info("calculate mark for project {} student {}", project.getId(), student);
            Task task = project.getTask();
            if (task == null){
                log.error("Failed to calculate mark, not task found for project with id {}", project.getId());
                throw new MarkServiceException(String.format("Failed to find task for project with id %s", project.getId()));
            }
            if (task.getCloseDate().isAfter(LocalDateTime.now())){
                return calculateBeforeCloseDate(project, task, student, precision);
            }
            return calculateAfterCloseDate(project, task, student, precision);
        }
        return 0.0;
    }

    @Transactional
    public Double calculateMarkForProject(Long projectId, Long studentId, int precision){
        double res;
        try {
            Project project = projectService.findProjectById(projectId);
            Task task = project.getTask();
            if (task == null){
                log.error("Failed to calculate mark, not task found for project with id {}", projectId);
                throw new MarkServiceException(String.format("Failed to find task for project with id %s", projectId));
            }
            if (task.getCloseDate().isAfter(LocalDateTime.now())){
                return calculateBeforeCloseDate(project, task, studentId, precision);
            }
            return calculateAfterCloseDate(project, task, studentId, precision);
//            NumberOfGraded number = student.getNumberOfGradedSet().stream()
//                    .filter(n -> Objects.equals(n.getTask().getId(), task.getId())).findFirst().orElse(null);

//            if (number == null){
////                throw new MarkServiceException(String.format("Failed to get number of graded for student with id %s when processing final mark",
////                        owner.getId()));
//                number = NumberOfGraded.builder()
//                        .task(project.getTask())
//                        .profile(student)
//                        .graded(0).build();
//            }

        } catch (NotFoundException e){
            log.error("Failed to calculate mark, project not found");
            throw new MarkServiceException(e.getLocalizedMessage());
        } catch (UserNotExistingException e){
            log.error("Failed to calculate mark, user not found");
            throw new MarkServiceException(e.getLocalizedMessage());
        }
    }

    public double calculateBeforeCloseDate(Project project, Task task, Long studentId, int precision){
        long numberOfMarkedByStudent = markRepository.countAllByOwnerIdAndProjectTaskId(studentId, project.getTask().getId());
        if (numberOfMarkedByStudent >= task.getMinNumberOfGraded()){
            return calculate(project, precision);
        }
        return Double.NaN;
    }

    public double calculateAfterCloseDate(Project project, Task task, Long studentId, int precision){
        if (task.getCloseDate().isAfter(project.getCompletionDate())){
            if (project.getMarks().size() < task.getMinNumberOfGraded()) {
                return Double.NaN;
            } else{
                return calculate(project, precision);
            }
        } else {
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

        return BigDecimal.valueOf(teachersMark + studentsMark).setScale(precision, RoundingMode.HALF_UP).doubleValue();
    }
}
