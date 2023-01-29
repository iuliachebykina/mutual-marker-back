package ru.urfu.mutual_marker.common;

import org.mapstruct.Mapper;
import ru.urfu.mutual_marker.dto.project.ProjectInfo;
import ru.urfu.mutual_marker.jpa.entity.Attachment;
import ru.urfu.mutual_marker.jpa.entity.Project;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectInfo entityToInfo(Project project);

    default String attachmentToString(Attachment attachment) {
        return attachment.getFileName();
    }
}
