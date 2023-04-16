package ru.urfu.mutual_marker.service.task;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.urfu.mutual_marker.common.TaskMapper;
import ru.urfu.mutual_marker.dto.task.MarkStep;
import ru.urfu.mutual_marker.dto.task.TaskCreationRequest;
import ru.urfu.mutual_marker.dto.task.TaskFullInfo;
import ru.urfu.mutual_marker.dto.task.TaskInfo;
import ru.urfu.mutual_marker.jpa.entity.MarkStepValue;
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
import ru.urfu.mutual_marker.security.jwt.JwtAuthentication;
import ru.urfu.mutual_marker.service.attachment.AttachmentService;
import ru.urfu.mutual_marker.service.exception.NotFoundException;
import ru.urfu.mutual_marker.service.mark.MarkCalculator;
import ru.urfu.mutual_marker.service.mark.MarkStepService;
import ru.urfu.mutual_marker.service.profile.ProfileService;

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
    MarkStepService markStepService;

    public List<TaskInfo> findAllTasks(Long roomId, Pageable pageable) {

        var tasks = taskRepository.findAllByRoom_IdAndDeletedIsFalse(roomId, pageable);
        JwtAuthentication authentication = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        var profile = profileRepository.findByEmailAndDeletedIsFalse(authentication.getUsername());
        if(profile.isEmpty()){
            throw new UserNotExistingException(String.format("Profile with email: %s does not existing", authentication.getUsername()));
        }
        tasks.sort(Comparator.comparing(Task::getCloseDate).reversed());

        List<TaskInfo> infos = taskMapper.listOfEntitiesToDtos(tasks);
        return getTaskInfos(profile.get(), tasks);
    }

    public List<TaskInfo> findCompletedTasks(Long roomId, Pageable pageable, String username) {
        var profile = profileRepository.findByEmailAndDeletedIsFalse(username);
        if(profile.isEmpty()){
            throw new UserNotExistingException(String.format("Profile with email: %s does not existing", username));
        }
        var tasks = taskRepository.findCompletedTask(roomId, profile.get().getId(), pageable);
        return getTaskInfos(profile.get(), tasks);
    }

    public List<TaskInfo> findUncompletedTasks(Long roomId, Pageable pageable, String username) {
        var profile = profileRepository.findByEmailAndDeletedIsFalse(username);
        if(profile.isEmpty()){
            throw new UserNotExistingException(String.format("Profile with email: %s does not existing", username));
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

        JwtAuthentication authentication = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = profileService.getProfileByEmail(authentication.getUsername()).getId();
        List<MarkStep> markSteps = markStepService.getAllMarkStepsByTask(task.get());
        Long numberOfGradedWorks = markRepository.countAllByOwnerIdAndProjectTaskId(currentUserId, taskFullInfo.getId());
        long leftToGrade = taskFullInfo.getMinNumberOfGraded() - numberOfGradedWorks;
        taskFullInfo = taskFullInfo.toBuilder().numberOfWorksLeftToGrade(leftToGrade > 0 ? leftToGrade : 0)
                .finalMark(markCalculator.calculateMarkForProjectByTask(task.get().getId(), currentUserId, 2))
                .markSteps(markSteps)
                .build();
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
        List<ru.urfu.mutual_marker.jpa.entity.MarkStep> markSteps = markStepService.toEntity(request.getMarkSteps(), task);
        markStepRepository.saveAll(markSteps);
        double maxGrade = 0d;
        for (ru.urfu.mutual_marker.jpa.entity.MarkStep ms : task.getMarkSteps()) {
            maxGrade += ms.getValues().stream().map(MarkStepValue::getValue).max(Integer::compareTo)
                    .orElseThrow(() -> new RuntimeException("Ошибка при расчете максимальной оценки - не найдены шаги оценки"))
                    .doubleValue();
             markStepValueRepository.saveAll(ms.getValues());
        }
        task.setMaxGrade(maxGrade);

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

    @Transactional
    public TaskInfo updateTask(Long taskId, TaskCreationRequest request) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isEmpty()) {
            throw new IllegalArgumentException("Task was not found");
        }
        var owner = profileRepository.findByEmailAndDeletedIsFalse(request.getOwner()).orElse(null);

        Task save = taskRepository.save(taskMapper.creationRequestToExistingEntity(task.get(), request, owner));
        return taskMapper.entityToInfo(save);
    }

    public TaskInfo appendNewAttachmentsToTask(String username, Long taskId, List<MultipartFile> files){
        return taskMapper.entityToInfo(attachmentService.apppendNewAttachmentsToTask(username, files, taskId));
    }
}
