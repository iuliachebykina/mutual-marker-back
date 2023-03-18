package ru.urfu.mutual_marker.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.common.RoomMapper;
import ru.urfu.mutual_marker.dto.room.AddRoomDto;
import ru.urfu.mutual_marker.dto.room.RoomAndRoomGroupDto;
import ru.urfu.mutual_marker.dto.room.RoomDto;
import ru.urfu.mutual_marker.dto.room.RoomGroupDto;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Room;
import ru.urfu.mutual_marker.jpa.entity.RoomGroup;
import ru.urfu.mutual_marker.jpa.entity.Task;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;
import ru.urfu.mutual_marker.jpa.repository.RoomGroupRepository;
import ru.urfu.mutual_marker.jpa.repository.RoomRepository;
import ru.urfu.mutual_marker.jpa.repository.TaskRepository;
import ru.urfu.mutual_marker.service.enums.EntityPassedToRoom;
import ru.urfu.mutual_marker.service.exception.InvalidArgumentException;
import ru.urfu.mutual_marker.service.exception.RoomServiceException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
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
    public Room addNewRoom(AddRoomDto addRoomDto){
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

        if (addRoomDto.getTeacherId() != null) {
            Profile teacher = profileService.findById(addRoomDto.getTeacherId());
            toAdd.getTeachers().add(teacher);
            teacher.addRoom(toAdd);
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

    private Room deleteTeacher(Long teacherId, Room room){
        try{
            room.removeTeacher(teacherId);
            return roomRepository.save(room);
        } catch (Exception e){
            log.error("Failed to delete teacher from room with id {}, error message {}, stacktrace {}",
                    room.getId(), e.getLocalizedMessage(), e.getStackTrace());
            throw new RoomServiceException("Failed to delete teacher");
        }
    }

    private Room deleteStudent(Long studentId, Room room){
        try{
            room.removeStudent(studentId);
            return roomRepository.save(room);
        } catch (Exception e){
            log.error("Failed to delete student from room with id {}, error message {}, stacktrace {}",
                    room.getId(), e.getLocalizedMessage(), e.getStackTrace());
            throw new RoomServiceException("Failed to delete student");
        }
    }

    private Room deleteTask(Long taskId, Room room){
        try{
            room.removeTask(taskId);
            return roomRepository.save(room);
        } catch (Exception e){
            log.error("Failed to delete task from room with id {}, error message {}, stacktrace {}",
                    room.getId(), e.getLocalizedMessage(), e.getStackTrace());
            throw new RoomServiceException("Failed to delete task");
        }
    }

    private Room addTeacher(Long teacherId, Room room){
        try{
            Profile teacher = profileService.getById(teacherId);
            room.addTeacher(teacher);
            return roomRepository.save(room);
        } catch (Exception e){
            log.error("Failed to add teacher to room with id {}, error message {}, stacktrace {}",
                    room.getId(), e.getLocalizedMessage(), e.getStackTrace());
            throw new RoomServiceException("Failed to add teacher");
        }
    }

    private Room addStudent(Long studentId, Room room){
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

    private Room addTask(Long taskId, Room room){
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

    public void deleteRoomGroup(Long id) {
        Optional<RoomGroup> roomGroup = roomGroupRepository.findById(id);
        if(roomGroup.isEmpty()){
            throw  new RoomServiceException(String.format("Failed to find room group by id: %s", id));
        }
        roomGroup.get().setDeleted(true);
        roomGroupRepository.save(roomGroup.get());
    }

    public List<RoomDto> getAllRoomsWithoutGroupForProfile(Pageable pageable, String email, Role role) {
        return getRoomsDto(roomRepository.findAllByDeletedIsFalseAndNotInGroup(email, pageable));
    }
}
