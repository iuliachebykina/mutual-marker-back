package ru.urfu.mutual_marker.service;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.dto.AddMarkStepDto;
import ru.urfu.mutual_marker.jpa.entity.MarkStep;
import ru.urfu.mutual_marker.jpa.entity.MarkStepValue;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Task;
import ru.urfu.mutual_marker.jpa.repository.MarkStepRepository;

import java.util.List;

@Service
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MarkStepService {
    MarkStepRepository markStepRepository;

    public void addMarkStep(List<Integer> values, Task task, String desc, Profile owner, String title){
        MarkStep toAdd = MarkStep.builder().build();
        values.forEach(value -> {
            toAdd.getValues().add(MarkStepValue.builder().markStep(toAdd).value(value).build());
        });
        toAdd.addTask(task);
        toAdd.setDescription(desc);
        toAdd.setOwner(owner);
        toAdd.setTitle(title);
        markStepRepository.save(toAdd);
    }

    public void addMarkSteps(List<AddMarkStepDto> addMarkStepDtos){
        addMarkStepDtos.forEach(dto -> {
            addMarkStep(dto.getValues(),
                    Task.builder().id(dto.getTaskId()).build(),
                    dto.getDescription(),
                    Profile.builder().id(dto.getProfileId()).build(),
                    dto.getTitle());
        });
    }
}
