package ru.urfu.mutual_marker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Value
public class AttachmentDto {
//    @Schema(title = "Файл", required = true)
//    MultipartFile file;
    @Schema(title = "Описание")
    String description;
    @Schema(title = "Уникальное имя файла", required = true)
    String fileName;
}
