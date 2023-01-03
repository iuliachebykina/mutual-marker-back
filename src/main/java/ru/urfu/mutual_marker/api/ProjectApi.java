package ru.urfu.mutual_marker.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.urfu.mutual_marker.dto.ProjectCreationInfo;
import ru.urfu.mutual_marker.dto.ProjectCreationResultDto;
import ru.urfu.mutual_marker.dto.ProjectInfo;
import ru.urfu.mutual_marker.dto.ProjectUpdateInfo;
import ru.urfu.mutual_marker.service.ProjectService;

import java.util.List;

@RestController
@RequestMapping(value = "api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProjectApi {

    ProjectService projectService;

    @Operation(summary = "Проект пользователя", description = "Возвращает проект текущего пользователя для таска")
    @GetMapping(value = "/task/{task_id}/project/self")
    public ProjectInfo getSelfProject(Authentication authentication, @PathVariable("task_id") Long taskId) {
        return projectService.getSelfProject((UserDetails) authentication.getPrincipal(), taskId);
    }

    @Operation(summary = "Обновление проекта")
    @PutMapping(value = "/task/{task_id}/project/self")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ProjectCreationResultDto updateProject(Authentication authentication, @RequestBody ProjectUpdateInfo updateInfo) {
        return projectService.updateProject((UserDetails) authentication.getPrincipal(), updateInfo);
    }

    @Operation(summary = "Рандомный проект id", description = "Возвращает id рандомного проекта для оценки в текущей таске")
    @GetMapping(value = "/task/{task_id}/project/random")
    public Long getRandomProject(Authentication authentication, @PathVariable("task_id") Long taskId) {
        return projectService.getRandomProject((UserDetails) authentication.getPrincipal(), taskId);
    }

    @Operation(summary = "Рандомный проект", description = "Возвращает рандомный проект для оценки в текущей таске")
    @GetMapping(value = "/task/{task_id}/project/{project_id}")
    public ProjectInfo getProject(@PathVariable("project_id") Long projectId) {
        return projectService.getProject(projectId);
    }

    @Operation(summary = "Все проекты в задании", description = "Возвращает все проекты в задании")
    @GetMapping(value = "/task/{task_id}/projects")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER')")
    public List<ProjectInfo> getProjects(@PathVariable("task_id") Long taskId) {
        return projectService.getProjects(taskId);
    }


    @Operation(summary = "Создание проекта", description = "Создает проект. ВАЖНО: сначала надо загрузить вложения")
    @PostMapping(value = "/task/{task_id}/project")
    public ProjectCreationResultDto createProject(Authentication authentication, @PathVariable("task_id") Long taskId, @RequestBody ProjectCreationInfo creationInfo) {
        return projectService.createProject((UserDetails) authentication.getPrincipal(), creationInfo, taskId);
    }
}
