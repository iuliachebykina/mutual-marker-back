package ru.urfu.mutual_marker.service;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.dto.AddMarkDto;
import ru.urfu.mutual_marker.dto.AddTeacherMarkDto;
import ru.urfu.mutual_marker.dto.ProjectFinalMarkDto;
import ru.urfu.mutual_marker.jpa.entity.Mark;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Project;
import ru.urfu.mutual_marker.jpa.entity.Task;
import ru.urfu.mutual_marker.jpa.repository.MarkRepository;
import ru.urfu.mutual_marker.jpa.repository.NumberOfGradedRepository;
import ru.urfu.mutual_marker.jpa.repository.ProjectRepository;
import ru.urfu.mutual_marker.security.exception.UserNotExistingException;
import ru.urfu.mutual_marker.service.exception.MarkServiceException;
import ru.urfu.mutual_marker.service.exception.NotFoundException;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class MarkService {
    MarkRepository markRepository;
    ProjectRepository projectRepository;
    ProfileService profileService;
    ProjectService projectService;
    TaskService taskService;
    NumberOfGradedRepository numberOfGradedRepository;


    public Mark addStudentMark(AddMarkDto addMarkDto){
        return addMark(addMarkDto, false, null);
    }

    public Mark addTeacherMark(AddTeacherMarkDto addTeacherMarkDto){
        return addMark(addTeacherMarkDto, true, addTeacherMarkDto.getCoefficient());
    }

    @Transactional
    Mark addMark(AddMarkDto addMarkDto, Boolean isTeacherMark, Double coefficient){
        Mark mark;
        try {
            Double res = addMarkDto.getMarkStepValues().stream().mapToInt(m -> m).average().orElse(Double.NaN);
            if (res.equals(Double.NaN)){
                log.error("Failed to process components to calculate final mark");
                throw new MarkServiceException("Failed to process markComponents");
            }
            BigDecimal truncation = new BigDecimal(res);
            truncation = truncation.setScale(0, RoundingMode.HALF_UP);
            Profile owner = profileService.findById(addMarkDto.getProfileId());
            Project project = projectService.findProjectById(addMarkDto.getProjectId());
            if(project.getTask().getCloseDate().isBefore(LocalDateTime.now()) && !isTeacherMark){
                return null;
            }
            mark = Mark
                    .builder()
                    .owner(owner)
                    .project(project)
                    .comment(addMarkDto.getComment())
                    .isTeacherMark(isTeacherMark)
                    .coefficient(coefficient)
                    .markValue(truncation.intValue())
                    .build();

//            NumberOfGraded numberOfGraded = owner.getNumberOfGradedSet().stream()
//                    .filter(n -> Objects.equals(n.getProfile().getId(), owner.getId())).findFirst()
//                    .orElse(null);
//            if (numberOfGraded == null){
////                throw new MarkServiceException(String.format("Failed to get number of graded for student with id %s when processing final mark",
////                        owner.getId()));
//                numberOfGraded = NumberOfGraded.builder()
//                        .task(project.getTask())
//                        .profile(owner)
//                        .graded(0).build();
//            }
//            numberOfGraded.setGraded(numberOfGraded.getGraded() + 1);
//            numberOfGradedRepository.save(numberOfGraded);

        } catch (EntityNotFoundException e){
            log.error("Failed to find project with id {}", addMarkDto.getProjectId());
            throw new MarkServiceException(String.format("Failed to find project with id %s", addMarkDto.getProjectId()));
        } catch (UserNotExistingException e){
            log.error("Failed to find student with id {}", addMarkDto.getProfileId());
            throw new MarkServiceException(String.format("Failed to find student with id %s", addMarkDto.getProfileId()));
        }
        return markRepository.save(mark);
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
        Mark mark = markRepository.findByProjectIdAndOwnerId(projectId, studentId).orElse(null);
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
            Project project = projectService.findProjectById(projectId);
            Profile student = profileService.findById(studentId);
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

    @Transactional
    public List<ProjectFinalMarkDto> getAllMarksForTask(Long taskId){
        List<Project> projects = projectService.findAllProjectsByTaskId(taskId);
        if (projects.isEmpty()){
            log.error("Failed to get all marks for task, no projects found for task with id {}", taskId);
            throw new MarkServiceException("Failed to fetch all marks for task, no projects found");
        }
        return projects.stream().map(project ->
        {
            try {
                return ProjectFinalMarkDto
                        .builder()
                        .finalMark(calculateMarkForProject(project.getId(), project.getStudent().getId(), 2))
                        .projectTitle(project.getTitle())
                        .profileId(project.getStudent().getId())
                        .projectId(project.getId())
                        .studentName(project.getStudent().getName())
                        .group(project.getStudent().getStudentGroup())
                        .build();
            } catch (MarkServiceException e) {
                log.error("Failed to calculate mark for student with {} in project with id {}",
                        project.getStudent().getId(),
                        project.getId());
                return null;
            }
        }).collect(Collectors.toList());
    }
}
