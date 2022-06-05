package ru.urfu.mutual_marker.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
    @Operation(summary = "Получение подробной инфы по заданию")
    @GetMapping(value = "/task/{task_id}")
    public TaskFullInfo getTaskInfo(@PathVariable("task_id") Long id) {
        return taskService.findTask(id);
    }

    @Operation(summary = "Создание задания")
    @PostMapping(value = "/task")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createTask(@RequestBody TaskCreationRequest request) {
        taskService.saveTask(request);
    }

    @Operation(summary = "Удаление задания")
    @DeleteMapping(value = "/task/{task_id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TEACHER')")
    public void deleteTask(@PathVariable("task_id") Long id) {
        taskService.deleteTask(id);
    }
}
