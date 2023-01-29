package ru.urfu.mutual_marker.dto.attachment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)

public class AttachmentInfoDto {
    String fileName;
    String description;
}
