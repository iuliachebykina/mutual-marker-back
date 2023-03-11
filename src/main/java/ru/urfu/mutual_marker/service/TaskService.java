package ru.urfu.mutual_marker.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.urfu.mutual_marker.common.TaskMapper;
import ru.urfu.mutual_marker.dto.task.TaskCreationRequest;
import ru.urfu.mutual_marker.dto.task.TaskFullInfo;
import ru.urfu.mutual_marker.dto.task.TaskInfo;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Task;
import ru.urfu.mutual_marker.jpa.repository.ProfileRepository;
import ru.urfu.mutual_marker.jpa.repository.ProjectRepository;
import ru.urfu.mutual_marker.jpa.repository.RoomRepository;
import ru.urfu.mutual_marker.jpa.repository.TaskRepository;
import ru.urfu.mutual_marker.jpa.repository.mark.MarkRepository;
import ru.urfu.mutual_marker.jpa.repository.mark.MarkStepRepository;
import ru.urfu.mutual_marker.jpa.repository.mark.MarkStepValueRepository;
import ru.urfu.mutual_marker.security.exception.UserNotExistingException;
import ru.urfu.mutual_marker.service.exception.NotFoundException;
import ru.urfu.mutual_marker.service.mark.MarkCalculator;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    MarkCalculator markCalculator;
    ProjectRepository projectRepository;

    public List<TaskInfo> findAllTasks(Long roomId, Pageable pageable) {

        var tasks = taskRepository.findAllByRoom_IdAndDeletedIsFalse(roomId, pageable);
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var profile = profileRepository.findByEmailAndDeletedIsFalse(principal.getUsername());
        if(profile.isEmpty()){
            throw new UserNotExistingException(String.format("Profile with email: %s does not existing", principal.getUsername()));
        }
        tasks.sort(Comparator.comparing(Task::getCloseDate).reversed());

        List<TaskInfo> infos = taskMapper.listOfEntitiesToDtos(tasks);
        return getTaskInfos(profile.get(), tasks);
    }

    public List<TaskInfo> findCompletedTasks(Long roomId, Pageable pageable, UserDetails principal) {
        var profile = profileRepository.findByEmailAndDeletedIsFalse(principal.getUsername());
        if(profile.isEmpty()){
            throw new UserNotExistingException(String.format("Profile with email: %s does not existing", principal.getUsername()));
        }
        var tasks = taskRepository.findCompletedTask(roomId, profile.get().getId(), pageable);
        return getTaskInfos(profile.get(), tasks);
    }

    public List<TaskInfo> findUncompletedTasks(Long roomId, Pageable pageable, UserDetails principal) {
        var profile = profileRepository.findByEmailAndDeletedIsFalse(principal.getUsername());
        if(profile.isEmpty()){
            throw new UserNotExistingException(String.format("Profile with email: %s does not existing", principal.getUsername()));
        }
        var tasks = taskRepository.findUncompletedTask(roomId, profile.get().getId(), pageable);
        return getTaskInfos(profile.get(), tasks);
    }

    private List<TaskInfo> getTaskInfos(Profile profile, List<Task> tasks) {
        var infos = taskMapper.listOfEntitiesToDtos(tasks);
        return infos.stream()
                .map(info -> {
                    Long numberOfGradedWorks = markRepository.countAllByOwnerIdAndProjectTaskId(profile.getId(), info.getId());
                    boolean isUploadedProject = projectRepository.findByStudentAndTask_IdAndDeletedIsFalse(profile, info.getId()).isPresent();
                    long leftToGrade = info.getMinNumberOfGraded() - numberOfGradedWorks;
                    return info.toBuilder()
                            .isUploadedProject(isUploadedProject)
                            .numberOfWorksLeftToGrade(leftToGrade > 0 ? leftToGrade : 0)
                            .finalMark(markCalculator.calculateMarkForProjectByTask(info.getId(), profile.getId(), 2))
                            .build();
                })
                .collect(Collectors.toList());
    }

    public TaskFullInfo findTask(Long taskId) {

        var task = taskRepository.findById(taskId);

        if (task.isEmpty()) {
            throw new NotFoundException("Task was not found");
        }

        TaskFullInfo taskFullInfo = taskMapper.entityToFullInfoDto(task.get());

        UserDetails currentUserDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = profileService.getProfileByEmail(currentUserDetails.getUsername()).getId();

        Long numberOfGradedWorks = markRepository.countAllByOwnerIdAndProjectTaskId(currentUserId, taskFullInfo.getId());
        long leftToGrade = taskFullInfo.getMinNumberOfGraded() - numberOfGradedWorks;
        taskFullInfo = taskFullInfo.toBuilder().numberOfWorksLeftToGrade(leftToGrade > 0 ? leftToGrade : 0)
                .finalMark(markCalculator.calculateMarkForProjectByTask(task.get().getId(), currentUserId, 2)).build();
        return taskFullInfo;
    }

    @Transactional
    public TaskInfo saveTask(TaskCreationRequest request, Boolean appendAttachments) {

        var room  = roomRepository.findById(request.getRoomId());

        if (room.isEmpty()) {
            throw new NotFoundException("Room is not found");
        }

        var owner = profileRepository.findByEmailAndDeletedIsFalse(request.getOwner()).orElse(null);
        var task = taskMapper.creationRequestToEntity(request, owner);
        task.setRoom(room.get());

        var markSteps = markStepRepository.saveAll(task.getMarkSteps());
        markSteps.forEach(markStep -> markStep.getValues().forEach(value -> {
            value.setMarkStep(markStep);
            value.setDeleted(false);
            markStepValueRepository.save(value);
        }));

        if (appendAttachments){
            attachmentService.appendExistingAttachmentsToTask(request.getAttachments(), task);
        }

        Task save = taskRepository.save(task);

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
        var owner = profileRepository.findByEmailAndDeletedIsFalse(request.getOwner()).orElse(null);

        Task save = taskRepository.save(taskMapper.creationRequestToExistingEntity(task.get(), request, owner));
        return taskMapper.entityToInfo(save);
    }

    public TaskInfo appendNewAttachmentsToTask(UserDetails principal, Long taskId, List<MultipartFile> files){
        return taskMapper.entityToInfo(attachmentService.apppendNewAttachmentsToTask(principal, files, taskId));
    }
}
