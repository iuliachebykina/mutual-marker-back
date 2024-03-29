package ru.urfu.mutual_marker.service.project;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.urfu.mutual_marker.common.ProfileMapper;
import ru.urfu.mutual_marker.common.ProjectMapper;
import ru.urfu.mutual_marker.dto.profile.profileInfo.StudentInfo;
import ru.urfu.mutual_marker.dto.project.ProjectCreationInfo;
import ru.urfu.mutual_marker.dto.project.ProjectCreationResultDto;
import ru.urfu.mutual_marker.dto.project.ProjectInfo;
import ru.urfu.mutual_marker.dto.project.ProjectUpdateInfo;
import ru.urfu.mutual_marker.jpa.entity.Project;
import ru.urfu.mutual_marker.jpa.repository.ProfileRepository;
import ru.urfu.mutual_marker.jpa.repository.ProjectRepository;
import ru.urfu.mutual_marker.jpa.repository.TaskRepository;
import ru.urfu.mutual_marker.jpa.repository.mark.MarkRepository;
import ru.urfu.mutual_marker.security.exception.UserNotExistingException;
import ru.urfu.mutual_marker.service.attachment.AttachmentService;
import ru.urfu.mutual_marker.service.exception.NotFoundException;
import ru.urfu.mutual_marker.service.exception.ProjectExistingException;
import ru.urfu.mutual_marker.service.exception.mark.MarkServiceException;
import ru.urfu.mutual_marker.service.mark.MarkCalculator;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class ProjectService {

    ProjectRepository projectRepository;
    TaskRepository taskRepository;
    MarkRepository markRepository;
    ProfileRepository profileRepository;
    AttachmentService attachmentService;
    ProjectMapper projectMapper;
    ProfileMapper profileMapper;
    MarkCalculator markCalculator;

    public ProjectInfo getProjectForStudent(Long projectId) {

        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project was not found"));;
        ProjectInfo projectInfo = projectMapper.entityToInfo(project);
        projectInfo.setStudent(null);
        return projectInfo;
    }

    @SneakyThrows
    public Long getRandomProject(String username, Long taskId) {

        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task was not found"));
        var student = profileRepository.findByEmailAndDeletedIsFalse(username.toLowerCase(Locale.ROOT));

        if(task.getCloseDate().isBefore(LocalDateTime.now())){
            throw new MarkServiceException("Task is overdue");
        }
        if(student.isEmpty()){
            throw new UserNotExistingException(String.format("Student with email: %s does not existing", username));
        }
        if(!projectRepository.existsByStudentIdAndTaskIdAndDeletedIsFalse(student.get().getId(), taskId)) {
            return null;
        }
        var markedProjects = markRepository.findAllByOwnerId(student.get().getId())
                .stream().map(mark -> mark.getProject().getId())
                .collect(Collectors.toList());
        var projects = projectRepository.findAllByTaskAndDeletedIsFalse(task)
                .stream()
                .filter(project -> !project.getDeleted())
                .sorted((p1, p2) -> p1.getMarks().size() <= p2.getMarks().size() ? -1 : 1)
                .collect(Collectors.toList());

        for (var project : projects) {

            if (!markedProjects.contains(project.getId()) &&
                    !project.getStudent().getEmail().equals(student.get().getEmail())) {
                return project.getId();
            }
        }
        log.info("Not found available project for rate for user with email: {}", username);
        return null;
    }

    @Transactional
    public ProjectCreationResultDto updateProject(String username, ProjectUpdateInfo updateInfo) {

        var profile = profileRepository.findByEmailAndDeletedIsFalse(username.toLowerCase(Locale.ROOT));
        if(profile.isEmpty()){
            throw new UserNotExistingException(String.format("Profile with email: %s does not existing", username));
        }


        var projectOptional = projectRepository.findById(updateInfo.getId());
        if (projectOptional.isPresent() && projectOptional.get().getStudent().equals(profile.get())) {
            if(projectOptional.get().getTask().getCloseDate().toLocalDate().isBefore(LocalDateTime.now().toLocalDate())){
                return ProjectCreationResultDto.builder()
                        .isOverdue(true)
                        .build();
            }
            var project = projectOptional.get();
            project.setTitle(updateInfo.getTitle());
            project.setDescription(updateInfo.getDescription());
        }
        return ProjectCreationResultDto.builder()
                .isOverdue(false)
                .build();
    }

    @Transactional
    public ProjectCreationResultDto createProject(String username, ProjectCreationInfo creationInfo,
                                                  Long taskId,
                                                  Boolean appendAttachments) {
        var profile = profileRepository.findByEmailAndDeletedIsFalse(username.toLowerCase(Locale.ROOT));
        if(profile.isEmpty()){
            throw new UserNotExistingException(String.format("Profile with email: %s does not existing", username));
        }

        Optional<Project> existingProject = projectRepository.findByStudentAndTask_IdAndDeletedIsFalse(profile.get(), taskId);
        if(existingProject.isPresent()){
            throw new ProjectExistingException(String.format("Project for student (%s) and task (%s) already exists", username, taskId));
        }

        var task = taskRepository.findById(taskId).orElseThrow(() -> new NotFoundException("Task was not found"));
        if(task.getCloseDate().toLocalDate().isBefore(LocalDateTime.now().toLocalDate())){
            return ProjectCreationResultDto.builder()
                    .isOverdue(true)
                    .build();
        }

        var project = Project.builder()
                .student(profile.get())
                .task(task)
                .title(creationInfo.getTitle())
                .description(creationInfo.getDescription())
                .completionDate(LocalDateTime.now())
                .build();

        if (appendAttachments) {
            attachmentService.appendExistingAttachmentsToProject(creationInfo.getAttachments(), project);
        }

        Project save = projectRepository.save(project);
        return ProjectCreationResultDto.builder()
                .id(save.getId())
                .isOverdue(false)
                .build();
    }

    public ProjectInfo getSelfProject(String username, Long taskId) {

        var profile = profileRepository.findByEmailAndDeletedIsFalse(username.toLowerCase(Locale.ROOT));
        if(profile.isEmpty()){
            throw new UserNotExistingException(String.format("Profile with email: %s does not existing", username));
        }
        var task = taskRepository.findById(taskId).orElseThrow(() -> new NotFoundException("Task was not found"));
        var project = projectRepository.findByStudentAndTask_IdAndDeletedIsFalse(profile.get(), task.getId())
                .orElse(null);
        return projectMapper.entityToInfo(project);
    }

    public Project findProjectById(Long projectId){
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null){
            log.error("Failed to find project with id {}", projectId);
            throw new NotFoundException(String.format("Failed to find project with id %s", projectId));
        }
        return project;
    }

    public List<Project> findAllProjectsByTaskId(Long taskId){
        return projectRepository.findAllByTask_IdAndDeletedIsFalse(taskId);
    }

    public List<ProjectInfo> getProjects(Long taskId) {
        List<Project> allByTask_id = findAllProjectsByTaskId(taskId);
        List<ProjectInfo> projectInfos = new ArrayList<>();
        for (Project project : allByTask_id) {
            ProjectInfo projectInfo = projectToProjectInfo(project);
            projectInfos.add(projectInfo);
        }
        return projectInfos;
    }

    private ProjectInfo projectToProjectInfo(Project project) {
        ProjectInfo projectInfo = projectMapper.entityToInfo(project);
        StudentInfo studentInfo = profileMapper.profileEntityToStudentDto(project.getStudent());
        projectInfo.setStudent(studentInfo);
        Double mark = markCalculator.calculateMarkForProject(project);
        projectInfo.setFinalMark(mark);
        return projectInfo;
    }


    public ProjectInfo appendNewAttachmentsToExistingProject(String username, List<MultipartFile> files, Long projectId){
        return projectMapper.entityToInfo(attachmentService.appendNewAttachmentsToProject(username, files, projectId));
    }

    public ProjectInfo getProject(Long projectId) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project was not found"));
        return projectToProjectInfo(project);
    }
}
