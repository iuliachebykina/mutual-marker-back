package ru.urfu.mutual_marker.common;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.urfu.mutual_marker.dto.mark.MarkDto;
import ru.urfu.mutual_marker.jpa.entity.Mark;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MarkMapper {

    List<MarkDto> listOfEntitiesToDtos(Set<Mark> marks);

}
