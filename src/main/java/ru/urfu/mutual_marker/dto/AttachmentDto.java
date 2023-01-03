package ru.urfu.mutual_marker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Value
public class AttachmentDto {
    MultipartFile file;
    String description;
}
