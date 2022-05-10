package ru.urfu.mutual_marker.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.urfu.mutual_marker.dto.ProjectCreationInfo;
import ru.urfu.mutual_marker.dto.ProjectInfo;
import ru.urfu.mutual_marker.dto.ProjectUpdateInfo;
import ru.urfu.mutual_marker.service.ProjectService;

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
    public void updateProject(Authentication authentication, @RequestBody ProjectUpdateInfo updateInfo) {
        projectService.updateProject((UserDetails) authentication.getPrincipal(), updateInfo);
    }

    @Operation(summary = "Рандомный проект", description = "Возвращает рандомный проект для оценки в текущей таске")
    @GetMapping(value = "/task/{task_id}/project/random")
    public ProjectInfo getRandomProject(Authentication authentication, @PathVariable("task_id") Long taskId) {
        return projectService.getRandomProject((UserDetails) authentication.getPrincipal(), taskId);
    }

    @Operation(summary = "Создание проекта", description = "Создает проект. ВАЖНО: сначала надо загрузить вложения")
    @PostMapping(value = "/task/{task_id}/project")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createProject(Authentication authentication, @PathVariable("task_id") Long taskId, @RequestBody ProjectCreationInfo creationInfo) {
        projectService.createProject((UserDetails) authentication.getPrincipal(), creationInfo, taskId);
    }
}
