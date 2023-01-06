package ru.urfu.mutual_marker.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.urfu.mutual_marker.dto.ProjectCreationInfoV2;
import ru.urfu.mutual_marker.dto.ProjectCreationResultDto;
import ru.urfu.mutual_marker.service.ProjectService;

@RestController
@RequestMapping(value = "api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProjectApiV2 {
    ProjectService projectService;

    @Operation(summary = "Создание проекта", description = "Создает проект с вложениями")
    @PostMapping(value = "/task/{task_id}/v2/project")
    public ProjectCreationResultDto createProject(Authentication authentication, @PathVariable("task_id") Long taskId,
                                                  @RequestParam("files") MultipartFile[] files,
                                                  @RequestBody ProjectCreationInfoV2 creationInfo) {
        return projectService.createProjectWithAttachments((UserDetails) authentication.getPrincipal(), files, creationInfo, taskId);
    }
}
