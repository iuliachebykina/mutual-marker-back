package ru.urfu.mutual_marker.service.room;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.common.MarkMapper;
import ru.urfu.mutual_marker.common.RoomMapper;
import ru.urfu.mutual_marker.common.TaskMapper;
import ru.urfu.mutual_marker.dto.mark.*;
import ru.urfu.mutual_marker.dto.room.AddRoomDto;
import ru.urfu.mutual_marker.dto.room.RoomAndRoomGroupDto;
import ru.urfu.mutual_marker.dto.room.RoomDto;
import ru.urfu.mutual_marker.dto.room.RoomGroupDto;
import ru.urfu.mutual_marker.dto.task.TaskInfo;
import ru.urfu.mutual_marker.jpa.entity.*;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;
import ru.urfu.mutual_marker.jpa.repository.*;
import ru.urfu.mutual_marker.service.mark.MarkCalculator;
import ru.urfu.mutual_marker.service.profile.ProfileService;
import ru.urfu.mutual_marker.service.enums.EntityPassedToRoom;
import ru.urfu.mutual_marker.service.exception.InvalidArgumentException;
import ru.urfu.mutual_marker.service.exception.RoomServiceException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoomService {
    RoomRepository roomRepository;
    ProfileService profileService;
    RoomMapper roomMapper;
    TaskRepository taskRepository;
    RoomGroupRepository roomGroupRepository;
    ProfileRepository profileRepository;
    TaskMapper taskMapper;
    ProjectRepository projectRepository;
    MarkMapper markMapper;
    MarkCalculator markCalculator;

    @Transactional
    public Room getRoomById(Long roomId){
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null){
            log.error("Failed to find room with id {}", roomId);
            throw new RoomServiceException("Failed to find room");
        }
        return room;
    }

    @Transactional
    public List<RoomDto> getAllRoomsForProfile(Pageable pageable, String email, Role role){
        try {
            switch (role){
                case ROLE_STUDENT:
                    return getAllRoomsForStudent(pageable, email);
                case ROLE_TEACHER:
                    return getAllRoomsForTeacher(pageable, email);
                default:
                    throw new RoomServiceException("Role is not recognized");
            }
        } catch (Exception e){
            log.error("Failed to find all rooms which contain profile with role {}, email {}, error {}", role, email, e.getLocalizedMessage());
            throw new RoomServiceException("Failed to find all rooms for student");
        }
    }

    @Transactional
    public List<RoomDto> getAllRoomsForStudent(Pageable pageable, String studentEmail){
        List<Room> allByStudentsEmail = roomRepository.findAllByStudentsEmailAndDeletedIsFalse(studentEmail, pageable);
        return getRoomsDto(allByStudentsEmail);
    }

    @Transactional
    public List<RoomDto> getAllRoomsForTeacher(Pageable pageable, String teacherEmail){
        List<Room> allByTeachersEmail = roomRepository.findAllByTeachersEmailAndDeletedIsFalse(teacherEmail, pageable);
        return getRoomsDto(allByTeachersEmail);
    }

    private List<RoomDto> getRoomsDto(List<Room> allByTeachersEmail) {
        List<RoomDto> answer = new ArrayList<>();
        for (Room room : allByTeachersEmail) {
            answer.add(RoomDto.builder()
                    .id(room.getId())
                    .code(room.getCode())
                    .title(room.getTitle())
                    .description(room.getDescription())
                    .membersCount(profileService.getCountOfMembersInRoom(room.getId()))
                    .build());
        }
        return answer;
    }

    @Transactional
    public Room getRoomByCode(String roomCode){
        Optional<Room> room = roomRepository.findByCodeAndDeletedIsFalse(roomCode);
        if (room.isEmpty()){
            log.error("Failed to find room with code {}", roomCode);
            throw new RoomServiceException(String.format("Failed to find room by code %s", roomCode));
        }
        return room.get();
    }

    @Transactional
    public Room addNewRoom(AddRoomDto addRoomDto, String email){
        Room toAdd = new Room();
        toAdd.setTitle(addRoomDto.getTitle());
        if(addRoomDto.getCode() != null
                && !addRoomDto.getCode().isBlank()){
            if(roomRepository.findByCodeAndDeletedIsFalse(addRoomDto.getCode()).isPresent()){
                return null;
            }
            toAdd.setCode(addRoomDto.getCode());
        } else {
            String code = NanoIdUtils.randomNanoId();
            toAdd.setCode(code);
        }


        Profile creator = profileService.getProfileByEmail(email);
        saveTeacherToRoom(toAdd, creator);

        if (addRoomDto.getTeacherId() != null && !Objects.equals(creator.getId(), addRoomDto.getTeacherId())) {
            Profile teacher = profileService.findById(addRoomDto.getTeacherId());
            saveTeacherToRoom(toAdd, teacher);
        }

        if (addRoomDto.getDescription() != null) {
            toAdd.setDescription(addRoomDto.getDescription());
        }

        return roomRepository.save(toAdd);
    }


    @Transactional
    public Room updateRoom(Room room){
        try{
            return roomRepository.save(room);
        } catch (Exception e){
            log.error("Error while updating room with id {}, error message {}, stacktrace {}", room.getId(), e.getLocalizedMessage(),
                    e.getStackTrace());
            throw new RoomServiceException("Failed to update room");
        }
    }

    @Transactional
    public Room deleteRoom(String code){
        Room room = roomRepository.findByCodeAndDeletedIsFalse(code).orElse(null);
        if (room == null){
            log.error("Failed to delete room with code{}", code);
            throw new RoomServiceException("Failed to delete room with code" + code);
        }
        room.setDeleted(true);
        return roomRepository.save(room);
    }

    @Transactional
    public Room addProfile(String email, String roomCode, EntityPassedToRoom entity){
        Profile profile = profileService.getProfileByEmail(email);
        return addEntity(profile.getId(), roomCode, entity);
    }

    @Transactional
    public Room addEntity(Long entityId, String roomCode, EntityPassedToRoom entity){
        Room room = this.getRoomByCode(roomCode);
        switch (entity){
            case TASK:
                return addTask(entityId, room);
            case STUDENT:
                return addStudent(entityId,room);
            case TEACHER:
                return addTeacher(entityId, room);
        }
        log.error("Failed to recognize type of entity passed to add entity room method");
        throw new InvalidArgumentException("Failed to recognize type of entity passed to add entity");
    }

    @Transactional
    public Room deleteEntity(Long entityId, String roomCode, EntityPassedToRoom entity){
        Room room = this.getRoomByCode(roomCode);
        switch (entity){
            case TASK:
                return deleteTask(entityId, room);
            case STUDENT:
                return deleteStudent(entityId, room);
            case TEACHER:
                return deleteTeacher(entityId, room);
        }
        log.error("Failed to recognize type of entity passed to deleteEntity room method");
        throw new IllegalArgumentException("Failed to recognize type of entity passed to delete entity");
    }

    @Transactional
    Room deleteTeacher(Long teacherId, Room room){
        try{
            profileService.deleteRoomFromProfile(room.getId(), teacherId);
            room.removeTeacher(teacherId);
            return roomRepository.save(room);
        } catch (Exception e){
            log.error("Failed to delete teacher from room with id {}, error message {}, stacktrace {}",
                    room.getId(), e.getLocalizedMessage(), e.getStackTrace());
            throw new RoomServiceException("Failed to delete teacher");
        }
    }

    @Transactional
    Room deleteStudent(Long studentId, Room room){
        try{
            profileService.deleteRoomFromProfile(room.getId(), studentId);
            room.removeStudent(studentId);
            return roomRepository.save(room);
        } catch (Exception e){
            log.error("Failed to delete student from room with id {}, error message {}, stacktrace {}",
                    room.getId(), e.getLocalizedMessage(), e.getStackTrace());
            throw new RoomServiceException("Failed to delete student");
        }
    }

    @Transactional
    Room deleteTask(Long taskId, Room room){
        try{
            room.removeTask(taskId);
            return roomRepository.save(room);
        } catch (Exception e){
            log.error("Failed to delete task from room with id {}, error message {}, stacktrace {}",
                    room.getId(), e.getLocalizedMessage(), e.getStackTrace());
            throw new RoomServiceException("Failed to delete task");
        }
    }

    @Transactional
    Room addTeacher(Long teacherId, Room room){
        try{
            Profile teacher = profileService.getById(teacherId);
            saveTeacherToRoom(room, teacher);
            return roomRepository.save(room);
        } catch (Exception e){
            log.error("Failed to add teacher to room with id {}, error message {}, stacktrace {}",
                    room.getId(), e.getLocalizedMessage(), e.getStackTrace());
            throw new RoomServiceException("Failed to add teacher");
        }
    }

    private void saveTeacherToRoom(Room room, Profile teacher) {
        room.addTeacher(teacher);
        teacher.addRoom(room);
    }

    @Transactional
    Room addStudent(Long studentId, Room room){
        try{
            Profile student = profileService.getById(studentId);
            room.addStudent(student);
            return roomRepository.save(room);
        } catch (Exception e){
            log.error("Failed to add student to room with id {}, error message{}, stacktrace {}",
                    room.getId(), e.getLocalizedMessage(), e.getStackTrace());
            throw new RoomServiceException("Failed to add student");
        }
    }

    @Transactional
    Room addTask(Long taskId, Room room){
        try{
            Task task = taskRepository.getById(taskId);
            room.addTask(task);
            return roomRepository.save(room);
        } catch (Exception e){
            log.error("Failed to add task to room with id {}, error message{}, stacktrace {}",
                    room.getId(), e.getLocalizedMessage(), e.getStackTrace());
            throw new RoomServiceException("Failed to add task");
        }
    }

    @Transactional
    public RoomGroupDto createRoomGroup(String roomGroupName, String email) {
        Profile profile = profileService.getProfileByEmail(email);
        RoomGroup roomGroup = RoomGroup.builder()
                .profile(profile)
                .title(roomGroupName)
                .build();
        return getRoomGroupDto(roomGroupRepository.save(roomGroup));
    }


    private RoomGroupDto getRoomGroupDto(RoomGroup roomGroup){
        List<Room> rooms = new ArrayList<>(roomGroup.getRooms());
        return RoomGroupDto.builder()
                .roomGroupId(roomGroup.getId())
                .roomGroupName(roomGroup.getTitle())
                .rooms(getRoomsDto(rooms))
                .build();
    }

    @Transactional
    public RoomGroupDto addRoomToRoomGroup(RoomAndRoomGroupDto roomAndRoomGroupDto) {
        Optional<RoomGroup> roomGroup = roomGroupRepository.findByIdAndDeletedIsFalse(roomAndRoomGroupDto.getRoomGroupId());
        if(roomGroup.isEmpty()){
            throw  new RoomServiceException(String.format("Failed to find room group by id: %s", roomAndRoomGroupDto.getRoomGroupId()));
        }
        Optional<Room> room = roomRepository.findById(roomAndRoomGroupDto.getRoomId());
        if(room.isEmpty()){
            throw  new RoomServiceException(String.format("Failed to find room  by id: %s", roomAndRoomGroupDto.getRoomId()));
        }
        roomGroup.get().addRoom(room.get());
        return getRoomGroupDto(roomGroupRepository.save(roomGroup.get()));
    }

    @Transactional
    public RoomGroupDto deleteRoomFromRoomGroup(RoomAndRoomGroupDto roomAndRoomGroupDto) {
        Optional<RoomGroup> roomGroup = roomGroupRepository.findByIdAndDeletedIsFalse(roomAndRoomGroupDto.getRoomGroupId());
        if(roomGroup.isEmpty()){
            throw  new RoomServiceException(String.format("Failed to find room group by id: %s", roomAndRoomGroupDto.getRoomGroupId()));
        }
        Optional<Room> room = roomRepository.findById(roomAndRoomGroupDto.getRoomId());
        if(room.isEmpty()){
            throw  new RoomServiceException(String.format("Failed to find room  by id: %s", roomAndRoomGroupDto.getRoomId()));
        }
        roomGroup.get().removeRoom(room.get());
        return getRoomGroupDto(roomGroupRepository.save(roomGroup.get()));
    }

    public List<RoomGroupDto> getRoomGroups(String email, Pageable pageable) {
        Profile profile = profileService.getProfileByEmail(email);
        return roomGroupRepository.findByProfile_IdAndDeletedIsFalse(profile.getId(), pageable)
                .stream().map(this::getRoomGroupDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRoomGroup(Long id) {
        Optional<RoomGroup> roomGroup = roomGroupRepository.findById(id);
        if(roomGroup.isEmpty()){
            throw  new RoomServiceException(String.format("Failed to find room group by id: %s", id));
        }
        roomGroup.get().setDeleted(true);
        roomGroup.get().getRooms().forEach(g -> roomGroup.get().removeRoom(g));
        roomGroupRepository.save(roomGroup.get());
    }

    public List<RoomDto> getAllRoomsWithoutGroupForProfile(Pageable pageable, String email, Role role) {
        return getRoomsDto(roomRepository.findAllByDeletedIsFalseAndNotInGroup(email, pageable));
    }

    @Transactional
    public void deleteRoomByStudent(Long roomId, String email) {
        Profile profile = profileService.getProfileByEmail(email);
        profileService.deleteRoomFromProfile(roomId, profile);
        Room room = getRoomById(roomId);
        room.removeStudent(profile.getId());
        roomRepository.save(room);
    }

    @Transactional
    public List<FeedbacksForTaskDto> getAllFeedbacksByTaskForRoom(Long roomId, Long profileId){
        Room room = getRoomById(roomId);
        List<FeedbacksForTaskDto> result = new ArrayList<>();
        for (Task task : room.getTasks()) {
            FeedbacksForTaskDto feedbacksForTaskDto = new FeedbacksForTaskDto();
            TaskInfo dto = taskMapper.entityToInfo(task);
            feedbacksForTaskDto.setTaskInfo(dto);
            Project project = projectRepository.findByStudentIdAndTaskIdAndDeletedIsFalse(profileId, task.getId()).orElse(null);
            if (project == null){
                continue;
            }
            dto.setFinalMark(markCalculator.calculateAndScaleToHundred(project, 2));

            List<MarkFeedbackDto> markFeedbacks = new ArrayList<>();
            for (Mark mark : project.getMarks()){
                MarkFeedbackDto markFeedbackDto = new MarkFeedbackDto();
                MarkDto markDto = markMapper.entityToDto(mark);
                markFeedbackDto.setMark(markDto);
                List<MarkStepFeedbackDto> markStepFeedbackDtos = new ArrayList<>();
                mark.getFeedbacks().forEach(f -> {
                    MarkStepFeedbackDto markStepFeedbackDto = new MarkStepFeedbackDto();
                    markStepFeedbackDto.setComment(f.getComment());
                    markStepFeedbackDto.setValue(f.getValue());
                    MarkStepDto markStepDto = new MarkStepDto();
                    List<Integer> sortedValues = f.getMarkStep().getValues().stream().map(MarkStepValue::getValue).sorted(Integer::compareTo).collect(Collectors.toList());
                    markStepDto.setValues(sortedValues);
                    markStepDto.setDescription(f.getMarkStep().getDescription());
                    markStepDto.setTitle(f.getMarkStep().getTitle());
                    markStepFeedbackDto.setMarkStep(markStepDto);
                    markStepFeedbackDtos.add(markStepFeedbackDto);
                });
                markFeedbackDto.setFeedbacks(markStepFeedbackDtos);
                markFeedbacks.add(markFeedbackDto);
            }
            feedbacksForTaskDto.setFeedbacks(markFeedbacks);
            result.add(feedbacksForTaskDto);
        }
        return result;
    }
}
