package ru.urfu.mutual_marker.common;

import org.mapstruct.Mapper;
import ru.urfu.mutual_marker.dto.ProjectFullInfo;
import ru.urfu.mutual_marker.dto.ProjectInfo;
import ru.urfu.mutual_marker.jpa.entity.Attachment;
import ru.urfu.mutual_marker.jpa.entity.Project;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectInfo entityToInfo(Project project);
    ProjectFullInfo entityToFullInfo(Project project);


    default String attachmentToString(Attachment attachment) {
        return attachment.getFileName();
    }
}
