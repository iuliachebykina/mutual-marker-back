package ru.urfu.mutual_marker.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.urfu.mutual_marker.service.AttachmentService;

import java.util.List;


@RestController
@RequestMapping(value = "api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AttachmentApi {

    AttachmentService attachmentService;

    @Operation(summary = "Загрузка вложений", description = "Загружает лист с файликами")
    @PostMapping(value = "/attachments/upload")
    public List<String> uploadAttachments(Authentication authentication, @RequestParam("attachments") List<MultipartFile> attachments) {
        return attachmentService.uploadAttachments((UserDetails) authentication.getPrincipal(), attachments);
    }

    //TODO удалить нужно, так для простого теста делалось
    @Operation(summary = "Загрузка вложений", description = "Загружает лист с файликами")
    @PostMapping(value = "/v1/attachments/upload")
    public List<String> uploadAttachmentsV1(Authentication authentication, @RequestParam("files") MultipartFile[] attachments) {
        return attachmentService.uploadAttachmentsV1((UserDetails) authentication.getPrincipal(), attachments);
    }

    @Operation(summary = "Удалить вложение")
    @DeleteMapping(value = "/attachments/delete/{filename}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAttachments(Authentication authentication, @PathVariable String filename) {
        attachmentService.deleteAttachment((UserDetails) authentication.getPrincipal(), filename );
    }

    @Operation(summary = "Добавление вложений в проект студента", description = "Добавляет вложения в уже созданный проект")
    @PostMapping(value = "/project/{project_id}/attachments/append", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void appendAttachments(Authentication authentication,
                                  @RequestParam("attachments") List<MultipartFile> attachments,
                                  @PathVariable("project_id") Long projectId) {
        attachmentService.appendNewAttachmentsToProject((UserDetails) authentication.getPrincipal(), attachments, projectId);
    }

    @Operation(summary = "Добавление вложений в созданное задание", description = "Добавляет вложения в созданное задание")
    @PostMapping(value = "/task/{task_id}/attachments/append", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void appendAttachmentsTask(Authentication authentication,
                                  @RequestParam("attachments") List<MultipartFile> attachments,
                                  @PathVariable("task_id") Long taskId) {
        attachmentService.apppendNewAttachmentsToTask((UserDetails) authentication.getPrincipal(), attachments, taskId);
    }

    @Operation(summary = "Открыть файл", description = "Возвращает содержимое файла по его имени")
    @SneakyThrows
    @GetMapping(value = "/attachments/open")
    public ResponseEntity<?> openFile(@RequestParam("filename") String filename) {
        return attachmentService.downloadFile(filename);
    }

    @Operation(summary = "Удалить неиспользуемые вложения")
    @DeleteMapping(value = "/admin/attachments/delete")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFiles() {
        attachmentService.deleteUseless();
    }
}
