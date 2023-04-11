package ru.urfu.mutual_marker.service.mark;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.common.MarkMapper;
import ru.urfu.mutual_marker.common.TaskMapper;
import ru.urfu.mutual_marker.dto.mark.*;
import ru.urfu.mutual_marker.jpa.entity.*;
import ru.urfu.mutual_marker.jpa.repository.ProjectRepository;
import ru.urfu.mutual_marker.jpa.repository.TaskRepository;
import ru.urfu.mutual_marker.jpa.repository.mark.MarkRepository;
import ru.urfu.mutual_marker.security.exception.UserNotExistingException;
import ru.urfu.mutual_marker.service.exception.mark.MarkServiceException;
import ru.urfu.mutual_marker.service.profile.ProfileService;
import ru.urfu.mutual_marker.service.project.ProjectService;
import ru.urfu.mutual_marker.service.room.RoomService;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class MarkService {
    MarkRepository markRepository;
    RoomService roomService;
    ProjectRepository projectRepository;
    ProfileService profileService;
    ProjectService projectService;
    MarkMapper markMapper;
    MarkCalculator markCalculator;
    MarkStepFeedbackService markStepFeedbackService;
    TaskRepository taskRepository;
    TaskMapper taskMapper;


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
            Double res = addMarkDto.getMarkStepFeedbackDtos().stream().mapToInt(MarkStepFeedbackDto::getValue).average().orElse(Double.NaN);
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
                    .taskId(project.getTask().getId())
                    .markValue(truncation.intValue())
                    .build();

        } catch (EntityNotFoundException e){
            log.error("Failed to find project with id {}", addMarkDto.getProjectId());
            throw new MarkServiceException(String.format("Failed to find project with id %s", addMarkDto.getProjectId()));
        } catch (UserNotExistingException e){
            log.error("Failed to find student with id {}", addMarkDto.getProfileId());
            throw new MarkServiceException(String.format("Failed to find student with id %s", addMarkDto.getProfileId()));
        }
        Mark result = markRepository.save(mark);
        markStepFeedbackService.addMarkStepFeedbacksForMark(addMarkDto, result);
        return result;
    }

    @Transactional
    public Mark saveMark(Mark mark){
        return markRepository.save(mark);
    }

    @Transactional
    public List<MarkDto> getAllMarksForProject(Long projectId){
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null){
            log.error("Failed to get all marks for project with id {}", projectId);
            throw new MarkServiceException(String.format("Failed to obtain marks for project with %s", projectId));
        }
        return markMapper.listOfEntitiesToDtos(project.getMarks());
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
        return markCalculator.calculateMarkForProject(projectId, studentId, precision);
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
