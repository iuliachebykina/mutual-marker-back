package ru.urfu.mutual_marker.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.common.TaskMapper;
import ru.urfu.mutual_marker.dto.TaskCreationRequest;
import ru.urfu.mutual_marker.dto.TaskFullInfo;
import ru.urfu.mutual_marker.dto.TaskInfo;
import ru.urfu.mutual_marker.jpa.entity.NumberOfGraded;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Task;
import ru.urfu.mutual_marker.jpa.repository.*;
import ru.urfu.mutual_marker.service.exception.NotFoundException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class TaskService {

    TaskMapper taskMapper;

    MarkStepValueRepository markStepValueRepository;
    RoomRepository roomRepository;

    ProfileRepository profileRepository;
    TaskRepository taskRepository;
    MarkStepRepository markStepRepository;
    NumberOfGradedRepository numberOfGradedRepository;

    AttachmentRepository attachmentRepository;

    public List<TaskInfo> findAllTasks(Long roomId, Pageable pageable) {

        var tasks = taskRepository.findAllByRoom_Id(roomId, pageable);
        return taskMapper.entitiesToInfos(tasks);
    }

    public TaskFullInfo findTask(Long taskId) {

        var task = taskRepository.findById(taskId);

        if (task.isEmpty()) {
            throw new NotFoundException("Task was not found");
        }

        return taskMapper.entityToFullInfo(task.get());
    }

    @Transactional
    public void saveTask(TaskCreationRequest request) {

        var room  = roomRepository.findById(request.getRoomId());

        if (room.isEmpty()) {
            throw new NotFoundException("Room is not found");
        }

        var owner = profileRepository.findByEmail(request.getOwner()).orElse(null);
        var task = taskMapper.creationRequestToEntity(request, owner);
        var attachments = attachmentRepository.findAllByFileNames(request.getAttachments());

        attachments.forEach(task::addAttachment);
        task.setRoom(room.get());
        task.getMarkSteps().forEach(step -> {
            step.setOwner(owner);
            step.addTask(task);
            step.setDeleted(false);
        });
        Task save = taskRepository.save(task);
        var markSteps = markStepRepository.saveAll(task.getMarkSteps());
        markSteps.forEach(markStep -> markStep.getValues().forEach(value -> {
            value.setMarkStep(markStep);
            value.setDeleted(false);
            markStepValueRepository.save(value);
        }));

        Set<Profile> roomStudents = room.get().getStudents();
        roomStudents.forEach(student -> {
            NumberOfGraded numberOfGraded = NumberOfGraded
                    .builder()
                    .task(task)
                    .profile(student)
                    .graded(0)
                    .build();
            numberOfGradedRepository.save(numberOfGraded);
            student.addNumberOfGraded(numberOfGraded);
            task.addNumberOfGraded(numberOfGraded);
            profileRepository.save(student);
            taskRepository.save(task);
        });

        log.info("Create task with id: {}", save.getId());
    }

    @Transactional
    public void deleteTask(Long taskId) {

        var taskOptional = taskRepository.findById(taskId);

        if (taskOptional.isEmpty()) {
            throw new IllegalArgumentException("Task was not found");
        }

        var task = taskOptional.get();
        task.delete();
    }
}
