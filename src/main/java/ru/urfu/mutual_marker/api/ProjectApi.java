package ru.urfu.mutual_marker.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.urfu.mutual_marker.dto.*;
import ru.urfu.mutual_marker.service.AttachmentService;
import ru.urfu.mutual_marker.service.ProjectService;

import java.util.List;

@RestController
@RequestMapping(value = "api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProjectApi {

    ProjectService projectService;
    AttachmentService attachmentService;

    @Operation(summary = "Проект пользователя", description = "Возвращает проект текущего пользователя для таска")
    @GetMapping(value = "/task/{task_id}/project/self")
    public ProjectInfo getSelfProject(Authentication authentication, @PathVariable("task_id") Long taskId) {
        return projectService.getSelfProject((UserDetails) authentication.getPrincipal(), taskId);
    }

    @Operation(summary = "Обновление проекта")
    @PutMapping(value = "/task/{task_id}/project/self")
    public ProjectCreationResultDto updateProject(Authentication authentication, @RequestBody ProjectUpdateInfo updateInfo) {
        return projectService.updateProject((UserDetails) authentication.getPrincipal(), updateInfo);
    }

    @Operation(summary = "Рандомный проект id", description = "Возвращает id рандомного проекта для оценки в текущей таске")
    @GetMapping(value = "/task/{task_id}/project/random")
    public Long getRandomProject(Authentication authentication, @PathVariable("task_id") Long taskId) {
        return projectService.getRandomProject((UserDetails) authentication.getPrincipal(), taskId);
    }

    @Operation(summary = "Возвращение проекта по id", description = "Возвращает проект по id")
    @GetMapping(value = "/task/project/{project_id}")
    public ProjectInfo getProject(@PathVariable("project_id") Long projectId) {
        return projectService.getProject(projectId);
    }

    @Operation(summary = "Все проекты в задании", description = "Возвращает все проекты в задании")
    @GetMapping(value = "/task/{task_id}/projects")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    public List<ProjectFullInfo> getProjects(@PathVariable("task_id") Long taskId) {
        return projectService.getProjects(taskId);
    }


    @Operation(summary = "Создание проекта", description = "Создает проект. ВАЖНО: сначала надо загрузить вложения")
    @PostMapping(value = "/task/{task_id}/project")
    public ProjectCreationResultDto createProjectWithAttachments(Authentication authentication, @PathVariable("task_id") Long taskId, @RequestBody ProjectCreationInfo creationInfo) {
        return projectService.createProject((UserDetails) authentication.getPrincipal(), creationInfo, taskId, Boolean.TRUE);
    }

    @Operation(summary = "Создание проекта", description = "Создает проект без привязки файлов, привязка отдельным методом")
    @PostMapping(value = "/task/{task_id}/projectWithoutAttachments")
    public ProjectCreationResultDto createProjectWithoutAttachments(Authentication authentication, @PathVariable("task_id") Long taskId, @RequestBody ProjectCreationInfo creationInfo) {
        return projectService.createProject((UserDetails) authentication.getPrincipal(), creationInfo, taskId, Boolean.FALSE);
    }

    @Operation(summary = "Привязка новых вложений", description = "Загрузка вложений и привязка к существующему проекту")
    @PostMapping(value = "/project/{project_id}/appendAttachments")
    public ResponseEntity<ProjectInfo> appendAttachmentsToProject(Authentication authentication, @PathVariable("projectId") Long projectId, @RequestParam List<MultipartFile> files) {
        return ResponseEntity.ok(projectService.appendNewAttachmentsToExistingProject((UserDetails) authentication.getPrincipal(), files, projectId));
    }
}
