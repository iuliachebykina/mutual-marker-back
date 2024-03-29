package ru.urfu.mutual_marker.service.mark;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.dto.mark.AddMarkStepDto;
import ru.urfu.mutual_marker.jpa.entity.MarkStep;
import ru.urfu.mutual_marker.jpa.entity.MarkStepValue;
import ru.urfu.mutual_marker.jpa.entity.Task;
import ru.urfu.mutual_marker.jpa.repository.ProfileRepository;
import ru.urfu.mutual_marker.jpa.repository.TaskRepository;
import ru.urfu.mutual_marker.jpa.repository.mark.MarkStepRepository;
import ru.urfu.mutual_marker.jpa.repository.mark.MarkStepValueRepository;
import ru.urfu.mutual_marker.security.jwt.JwtAuthentication;
import ru.urfu.mutual_marker.service.exception.mark.MarkStepServiceException;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Data
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MarkStepService { //TODO add error handling for repository methods
    MarkStepRepository markStepRepository;
    TaskRepository taskRepository;
    ProfileRepository profileRepository;
    MarkStepValueRepository markStepValueRepository;

    @Transactional
    public MarkStep addMarkStep(Authentication authentication, AddMarkStepDto addMarkStepDto){
        MarkStep toAdd = MarkStep.builder()
                .description(addMarkStepDto.getDescription())
                .owner(profileRepository.getByEmailAndDeletedIsFalse(((JwtAuthentication) authentication).getUsername().toLowerCase(Locale.ROOT)).get())
                .title(addMarkStepDto.getTitle())
                .build();
        addMarkStepDto.getValues().forEach(value ->
                toAdd.getValues().add(MarkStepValue.builder().markStep(toAdd).value(value).build()));
        toAdd.addTask(taskRepository.getById(addMarkStepDto.getTaskId()));
        return markStepRepository.save(toAdd);
    }

    @Transactional
    public List<MarkStep> addMarkSteps(Authentication authentication, List<AddMarkStepDto> addMarkStepDtos){
        List<MarkStep> toReturn = new ArrayList<>();
        addMarkStepDtos.forEach(dto -> toReturn.add(addMarkStep(authentication, dto)));
        return toReturn;
    }

    @Transactional
    public MarkStep updateMarkStep(MarkStep markStep){
        return markStepRepository.save(markStep);
    }

    @Transactional
    public MarkStep deleteMarkStep(Long markStepId){
        MarkStep toDelete = markStepRepository.findById(markStepId).orElse(null);
        if (toDelete == null){
            log.error("Failed to find markStep for deletion. Id: {}", markStepId);
            throw new MarkStepServiceException("Failed to find markStep for deletion");
        }
        toDelete.setDeleted(true);
        return markStepRepository.save(toDelete);
    }

    @Transactional
    public MarkStep deleteMarkStepForTask(Long markStepId, Long taskId){
        MarkStep toDelete = markStepRepository.findById(markStepId).orElse(null);
        if (toDelete == null){
            log.error("Failed to find markStep for deletion");
            throw new MarkStepServiceException("Failed to find markStep for deletion");
        }
        toDelete.setTasks(toDelete.getTasks().stream().filter(task ->
                !Objects.equals(task.getId(), taskId)).collect(Collectors.toSet()));
        return markStepRepository.save(toDelete);
    }

    public List<ru.urfu.mutual_marker.dto.task.MarkStep> getAllMarkStepsByTask(Task task){
        List<MarkStep> markSteps = markStepRepository.findAllByTasksIn(Set.of(task));
        return toDto(markSteps);
    }

    public List<ru.urfu.mutual_marker.dto.task.MarkStep> toDto(List<MarkStep> markSteps) {
        return markSteps.stream().map(s -> new ru.urfu.mutual_marker.dto.task.MarkStep(s.getId(),
                        s.getTitle(),
                        s.getDescription(),
                        s.getValues().stream().map(MarkStepValue::getValue).collect(Collectors.toSet()),
                        s.getDeleted()))
                .collect(Collectors.toList());
    }

    public List<MarkStep> toEntity(List<ru.urfu.mutual_marker.dto.task.MarkStep> markSteps, Task task) {

        return markSteps.stream().map(s -> {
                    MarkStep markStep = MarkStep.builder()
                                    .title(s.getTitle())
                                            .description(s.getDescription())
                            .deleted(false)
                                                    .build();
                    Set<MarkStepValue> markStepValueSet = s.getValues().stream().map(v -> MarkStepValue.builder()
                                    .markStep(markStep)
                                    .value(v)
                                    .deleted(false)
                                    .build())
                            .collect(Collectors.toSet());
                    markStep.setValues(markStepValueSet);
                    markStep.addTask(task);
                    return markStep;

                })
                .collect(Collectors.toList());
    }
}
