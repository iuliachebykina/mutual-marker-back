package ru.urfu.mutual_marker.common;

import lombok.RequiredArgsConstructor;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.urfu.mutual_marker.dto.mark.MarkDto;
import ru.urfu.mutual_marker.jpa.entity.Mark;
import ru.urfu.mutual_marker.service.mark.MarkCalculator;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class MarkMapper {
    @Autowired
    private MarkCalculator markCalculator;

    public abstract List<MarkDto> listOfEntitiesToDtos(Set<Mark> marks);

//    @Mapping(target = "finalMark", source = "markValue")
    public abstract MarkDto entityToDto(Mark mark);

    @AfterMapping
    public MarkDto map(Mark mark, @MappingTarget MarkDto.MarkDtoBuilder markDto) {
        markDto.scaledMark(markCalculator.calculateAndScaleToHundred(mark.getProject(), 2));
        markDto.maxMarkValue(mark.getProject().getTask().getMaxGrade());
        markDto.unscaledMark(markCalculator.calculate(mark.getProject(), 2));
        return markDto.build();
    }
}