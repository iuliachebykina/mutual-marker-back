package ru.urfu.mutual_marker.service;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.dto.AddMarkStepDto;
import ru.urfu.mutual_marker.jpa.entity.MarkStep;
import ru.urfu.mutual_marker.jpa.entity.MarkStepValue;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Task;
import ru.urfu.mutual_marker.jpa.repository.MarkStepRepository;
import ru.urfu.mutual_marker.service.exception.MarkStepServiceException;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Data
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MarkStepService { //TODO add error handling for repository methods
    MarkStepRepository markStepRepository;
    EntityManager entityManager;

    @Transactional
    public MarkStep addMarkStep(AddMarkStepDto addMarkStepDto){
        MarkStep toAdd = MarkStep.builder().build();
        addMarkStepDto.getValues().forEach(value ->
                toAdd.getValues().add(MarkStepValue.builder().markStep(toAdd).value(value).build()));
        toAdd.addTask(entityManager.getReference(Task.class, addMarkStepDto.getTaskId()));
        toAdd.setDescription(addMarkStepDto.getDescription());
        toAdd.setOwner(entityManager.getReference(Profile.class, addMarkStepDto.getProfileId()));
        toAdd.setTitle(addMarkStepDto.getTitle());
        return markStepRepository.save(toAdd);
    }

    @Transactional
    public List<MarkStep> addMarkSteps(List<AddMarkStepDto> addMarkStepDtos){
        List<MarkStep> toReturn = new ArrayList<>();
        addMarkStepDtos.forEach(dto -> toReturn.add(addMarkStep(dto)));
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
            log.error("Failed to find markStep for deletion");
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
}
