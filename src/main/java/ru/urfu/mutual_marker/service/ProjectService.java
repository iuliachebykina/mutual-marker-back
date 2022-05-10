package ru.urfu.mutual_marker.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.common.ProjectMapper;
import ru.urfu.mutual_marker.dto.ProjectCreationInfo;
import ru.urfu.mutual_marker.dto.ProjectInfo;
import ru.urfu.mutual_marker.dto.ProjectUpdateInfo;
import ru.urfu.mutual_marker.jpa.entity.Project;
import ru.urfu.mutual_marker.jpa.repository.*;
import ru.urfu.mutual_marker.service.exception.NotFoundException;

import javax.transaction.Transactional;
import java.rmi.UnexpectedException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProjectService {

    ProjectRepository projectRepository;
    TaskRepository taskRepository;
    MarkRepository markRepository;
    ProfileRepository profileRepository;
    AttachmentRepository attachmentRepository;
    ProjectMapper projectMapper;

    @SneakyThrows
    public ProjectInfo getRandomProject(UserDetails principal, Long taskId) {

        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task was not found"));
        var student = profileRepository.findByEmail(principal.getUsername());
        var markedProjects = markRepository.findAllByStudentId(student.get().getId())
                .stream().map(mark -> mark.getProject().getId())
                .collect(Collectors.toList());
        var projects = projectRepository.findAllByTask(task)
                .stream()
                .filter(project -> !project.getDeleted())
                .sorted((p1, p2) -> p1.getMarks().size() <= p2.getMarks().size() ? -1 : 1)
                .collect(Collectors.toList());

        for (var project : projects) {

            if (markedProjects.contains(project.getId()) ||
                    project.getStudent().getEmail().equals(student.get().getEmail())) {
                continue;
            }
            return projectMapper.entityToInfo(project);
        }

        throw new UnexpectedException("Сорян, пока проектов больше нет");
    }

    @Transactional
    public void updateProject(UserDetails principal, ProjectUpdateInfo updateInfo) {

        var profile = profileRepository.findByEmail(principal.getUsername());
        var projectOptional = projectRepository.findById(updateInfo.getId());
        if (projectOptional.isPresent() && projectOptional.get().getStudent().equals(profile.get())) {
            var project = projectOptional.get();
            project.setTitle(updateInfo.getTitle());
            project.setDescription(updateInfo.getDescription());
        }
    }

    @Transactional
    public void createProject(UserDetails principal, ProjectCreationInfo creationInfo, Long taskId) {

        var profile = profileRepository.findByEmail(principal.getUsername());
        var task = taskRepository.findById(taskId).orElseThrow(() -> new NotFoundException("Task was not found"));
        var attachments = attachmentRepository.findAllByFileNames(creationInfo.getAttachments());
        var project = Project.builder()
                .student(profile.get())
                .task(task)
                .title(creationInfo.getTitle())
                .description(creationInfo.getDescription())
                .attachments(attachments)
                .build();
        projectRepository.save(project);
        attachments.forEach(attachment -> attachment.getProjects().add(project));
    }

    public ProjectInfo getSelfProject(UserDetails principal, Long taskId) {

        var profile = profileRepository.findByEmail(principal.getUsername());
        var task = taskRepository.findById(taskId).orElseThrow(() -> new NotFoundException("Task was not found"));
        var project = projectRepository.findByStudentAndTask(profile.get(), task)
                .orElseThrow(() -> new NotFoundException("Project was not found"));
        return projectMapper.entityToInfo(project);
    }
}
