package ru.urfu.mutual_marker.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.urfu.mutual_marker.dto.ProjectInfo;
import ru.urfu.mutual_marker.dto.TaskCreationRequest;
import ru.urfu.mutual_marker.dto.TaskFullInfo;
import ru.urfu.mutual_marker.dto.TaskInfo;
import ru.urfu.mutual_marker.service.TaskService;

import java.util.List;

@RestController
@RequestMapping(value = "api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TasksApi {

    TaskService taskService;

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получение всех заданий для комнаты")
    @GetMapping(value = "/task", params = { "page", "size" })
    public List<TaskInfo> getAllTasks(@RequestParam("page") int page,
                                      @RequestParam("size") int size,
                                      @RequestParam("room_id") Long id) {

        Pageable pageable = PageRequest.of(page, size);
        return taskService.findAllTasks(id, pageable);
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получение выполненных заданий")
    @GetMapping(value = "/task/completed", params = { "page", "size" })
    public List<TaskInfo> getCompletedTasks(Authentication authentication,
                                            @RequestParam("page") int page,
                                            @RequestParam("size") int size,
                                            @RequestParam("room_id") Long id) {

        Pageable pageable = PageRequest.of(page, size);
        return taskService.findCompletedTasks(id, pageable, (UserDetails) authentication.getPrincipal());
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получение подробной инфы по заданию")
    @GetMapping(value = "/task/{task_id}")
    public TaskFullInfo getTaskInfo(@PathVariable("task_id") Long id) {
        return taskService.findTask(id);
    }

    @Operation(summary = "Создание задания", description = "Создание задания. ВАЖНО. При обращении к этом эндпоинту - загрузить вложения")
    @PostMapping(value = "/task")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    public TaskInfo createTaskWithAttachments(@RequestBody TaskCreationRequest request) {
        return taskService.saveTask(request, Boolean.TRUE);
    }

    @Operation(summary = "Создание задания", description = "Создание задания без вложений. Вложения прикрепляются отдельным методом")
    @PostMapping(value = "/taskWithoutAttachments")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    public TaskInfo createTaskWithoutAttachments(@RequestBody TaskCreationRequest request) {
        return taskService.saveTask(request, Boolean.FALSE);
    }

    @Operation(summary = "Изменить задание")
    @PatchMapping(value = "/task/{task_id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    public TaskInfo updateTask(@RequestBody TaskCreationRequest request, @PathVariable("task_id") Long taskId) {
        return taskService.updateTask(taskId, request);
    }

    @Operation(summary = "Удаление задания")
    @DeleteMapping(value = "/task/{task_id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    public void deleteTask(@PathVariable("task_id") Long id) {
        taskService.deleteTask(id);
    }

    @Operation(summary = "Привязка новых вложений", description = "Загрузка вложений и привязка к существующему заданию")
    @PostMapping(value = "/task/{task_id}/appendAttachments")
    public ResponseEntity<TaskInfo> appendAttachmentsToProject(Authentication authentication, @PathVariable("task_id") Long taskId, @RequestParam List<MultipartFile> files) {
        return ResponseEntity.ok(taskService.appendNewAttachmentsToTask((UserDetails) authentication.getPrincipal(), taskId, files));
    }
}
