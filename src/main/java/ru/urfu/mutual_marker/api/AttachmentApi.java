package ru.urfu.mutual_marker.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.urfu.mutual_marker.service.AttachmentService;


@RestController
@RequestMapping(value = "api", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AttachmentApi {

    AttachmentService attachmentService;

    @Operation(summary = "Загрузка вложений", description = "Загружает лист с файликами")
    @PostMapping(value = "/attachments/upload")
    public void uploadAttachment(Authentication authentication, @RequestParam("files") MultipartFile[] files) {
        attachmentService.uploadAttachments((UserDetails) authentication.getPrincipal(), files);
    }

    @Operation(summary = "Добавление вложений", description = "Добавляет вложения в уже созданный проект")
    @PostMapping(value = "/project/{project_id}/attachments/append")
    public void appendAttachments(Authentication authentication,
                                  @RequestParam("files") MultipartFile[] files,
                                  @PathVariable("project_id") Long projectId) {
        attachmentService.appendAttachments((UserDetails) authentication.getPrincipal(), files, projectId);
    }

    @Operation(summary = "Открыть файл", description = "Возвращает содержимое файла по его имени")
    @SneakyThrows
    @GetMapping(value = "/attachments/open")
    public ResponseEntity openFile(@RequestParam("filename") String filename) {
        return attachmentService.downloadFile(filename);
    }

    @Operation(summary = "Удалить неиспользуемые вложения")
    @DeleteMapping(value = "/admin/attachemnts/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFiles() {
        attachmentService.deleteUseless();
    }
}
