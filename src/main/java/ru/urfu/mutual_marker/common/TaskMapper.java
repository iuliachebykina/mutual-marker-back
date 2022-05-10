package ru.urfu.mutual_marker.common;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.urfu.mutual_marker.dto.TaskCreationRequest;
import ru.urfu.mutual_marker.dto.TaskFullInfo;
import ru.urfu.mutual_marker.dto.TaskInfo;
import ru.urfu.mutual_marker.jpa.entity.MarkStepValue;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Task;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
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

    List<TaskInfo> entitiesToInfos(List<Task> tasks);

    @Mapping(source = "room.id", target = "roomId")
    TaskFullInfo entityToFullInfo(Task task);

    Task creationRequestToEntity(TaskCreationRequest request, Profile owner);
}
