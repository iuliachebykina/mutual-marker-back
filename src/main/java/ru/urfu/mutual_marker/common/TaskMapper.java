package ru.urfu.mutual_marker.common;

import org.mapstruct.*;
import ru.urfu.mutual_marker.dto.task.TaskCreationRequest;
import ru.urfu.mutual_marker.dto.task.TaskFullInfo;
import ru.urfu.mutual_marker.dto.task.TaskInfo;
import ru.urfu.mutual_marker.jpa.entity.Attachment;
import ru.urfu.mutual_marker.jpa.entity.MarkStepValue;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Task;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {

    default Integer map(MarkStepValue value) {
        return value.getValue();
    }

    default MarkStepValue map(Integer value) {
        return new MarkStepValue(value);
    }

    Set<Integer> mapToInteger(Set<MarkStepValue> value);

    Set<MarkStepValue> mapToEntity(Set<Integer> value);

    @Mapping(source = "room.id", target = "roomId")
    TaskInfo entityToInfo(Task task);

    List<TaskInfo> listOfEntitiesToDtos(List<Task> tasks);

    @Mapping(source = "room.id", target = "roomId")
    TaskFullInfo entityToFullInfoDto(Task task);

    default String attachmentToString(Attachment attachment) {
        return attachment.getFileName();
    }

    @Mapping(target = "deleted", defaultValue = "false")
    @Mapping(target = "attachments", ignore = true)
    Task creationRequestToEntity(TaskCreationRequest request, Profile owner);

    @Mapping(target = "attachments", ignore = true) //TODO Check if overwrites existing attachments
    Task creationRequestToExistingEntity(@MappingTarget Task task, TaskCreationRequest request, Profile owner);
}
