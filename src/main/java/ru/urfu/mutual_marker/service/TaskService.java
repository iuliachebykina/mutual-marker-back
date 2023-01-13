package ru.urfu.mutual_marker.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.common.TaskMapper;
import ru.urfu.mutual_marker.dto.AttachmentInfoDto;
import ru.urfu.mutual_marker.dto.TaskCreationRequest;
import ru.urfu.mutual_marker.dto.TaskFullInfo;
import ru.urfu.mutual_marker.dto.TaskInfo;
import ru.urfu.mutual_marker.jpa.entity.Task;
import ru.urfu.mutual_marker.jpa.repository.*;
import ru.urfu.mutual_marker.security.exception.UserNotExistingException;
import ru.urfu.mutual_marker.service.exception.NotFoundException;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
    ProfileService profileService;
    MarkRepository markRepository;
    AttachmentService attachmentService;

    public List<TaskInfo> findAllTasks(Long roomId, Pageable pageable) {

        var tasks = taskRepository.findAllByRoom_Id(roomId, pageable);
        UserDetails currentUserDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = profileService.getProfileByEmail(currentUserDetails.getUsername()).getId();
        tasks.sort(Comparator.comparing(Task::getCloseDate).reversed());

        List<TaskInfo> infos = taskMapper.entitiesToInfos(tasks);
        infos.forEach(info -> {
            Long numberOfGradedWorks = markRepository.countAllByOwnerIdAndProjectTaskId(currentUserId, info.getId());
            Long leftToGrade = info.getMinNumberOfGraded() - numberOfGradedWorks;
            info = info.toBuilder().numberOfWorksLeftToGrade(leftToGrade > 0 ? leftToGrade : 0).build();
        });
        return infos;
    }

    public List<TaskInfo> findCompletedTasks(Long roomId, Pageable pageable, UserDetails principal) {
        var profile = profileRepository.findByEmail(principal.getUsername());
        if(profile.isEmpty()){
            throw new UserNotExistingException(String.format("Profile with email: %s does not existing", principal.getUsername()));
        }
        var tasks = taskRepository.findCompletedByRoom(roomId, profile.get().getId(), pageable);
        return taskMapper.entitiesToInfos(tasks);
    }

    public TaskFullInfo findTask(Long taskId) {

        var task = taskRepository.findById(taskId);

        if (task.isEmpty()) {
            throw new NotFoundException("Task was not found");
        }

        TaskFullInfo taskFullInfo = taskMapper.entityToFullInfo(task.get());
        for (AttachmentInfoDto attachment : taskFullInfo.getAttachments()) {
            String description = attachmentService.getDescription(attachment.getDescription());
            attachment.setDescription(description);
        }

        UserDetails currentUserDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = profileService.getProfileByEmail(currentUserDetails.getUsername()).getId();

        Long numberOfGradedWorks = markRepository.countAllByOwnerIdAndProjectTaskId(currentUserId, taskFullInfo.getId());
        Long leftToGrade = taskFullInfo.getMinNumberOfGraded() - numberOfGradedWorks;
        taskFullInfo = taskFullInfo.toBuilder().numberOfWorksLeftToGrade(leftToGrade > 0 ? leftToGrade : 0).build();
        return taskFullInfo;
    }

    @Transactional
    public TaskInfo saveTask(TaskCreationRequest request) {

        var room  = roomRepository.findById(request.getRoomId());

        if (room.isEmpty()) {
            throw new NotFoundException("Room is not found");
        }

        var owner = profileRepository.findByEmail(request.getOwner()).orElse(null);
        var task = taskMapper.creationRequestToEntity(request, owner);
        task.setRoom(room.get());

        Task save = taskRepository.save(task);
        var markSteps = markStepRepository.saveAll(task.getMarkSteps());
        markSteps.forEach(markStep -> markStep.getValues().forEach(value -> {
            value.setMarkStep(markStep);
            value.setDeleted(false);
            markStepValueRepository.save(value);
        }));

        log.info("Create task with id: {}", save.getId());
        return taskMapper.entityToInfo(save);

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

    public TaskInfo updateTask(Long taskId, TaskCreationRequest request) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isEmpty()) {
            throw new IllegalArgumentException("Task was not found");
        }
        var owner = profileRepository.findByEmail(request.getOwner()).orElse(null);

        Task save = taskRepository.save(taskMapper.creationRequestToExistingEntity(task.get(), request, owner));
        return taskMapper.entityToInfo(save);
    }
}
