package ru.urfu.mutual_marker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomDto {
    Long id;
    String title;
    String code;
    Long membersCount;

}
