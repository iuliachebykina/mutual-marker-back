package ru.urfu.mutual_marker.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.common.RoomMapper;
import ru.urfu.mutual_marker.dto.AddRoomDto;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Room;
import ru.urfu.mutual_marker.jpa.entity.Task;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;
import ru.urfu.mutual_marker.jpa.repository.ProfileRepository;
import ru.urfu.mutual_marker.jpa.repository.RoomRepository;
import ru.urfu.mutual_marker.jpa.repository.TaskRepository;
import ru.urfu.mutual_marker.service.enums.EntityPassedToRoom;
import ru.urfu.mutual_marker.service.exception.RoomServiceException;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoomService {
    RoomRepository roomRepository;
    ProfileRepository profileRepository;
    RoomMapper roomMapper;
    TaskRepository taskRepository;

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
    public List<Room> getAllRoomsForProfile(Pageable pageable, String email, Role role){
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
    public List<Room> getAllRoomsForStudent(Pageable pageable, String studentEmail){
        return roomRepository.findAllByStudentsEmail(studentEmail, pageable);
    }

    @Transactional
    public List<Room> getAllRoomsForTeacher(Pageable pageable, String teacherEmail){
        return roomRepository.findAllByTeachersEmail(teacherEmail, pageable);
    }

    @Transactional
    public Room getRoomByCode(String roomCode){
        Room room = roomRepository.findByCode(roomCode);
        if (room == null){
            log.error("Failed to find room with code {}", roomCode);
            throw new RoomServiceException(String.format("Failed to find room by code %s", roomCode));
        }
        return room;
    }

    @Transactional
    public Room addNewRoom(AddRoomDto addRoomDto){
        Room toAdd = roomMapper.addRoomDtoToRoom(addRoomDto);
        String code = NanoIdUtils.randomNanoId();
        toAdd.setCode(code);
        if (addRoomDto.getTeacherId() != null) {
            Profile teacher = profileRepository.getById(addRoomDto.getTeacherId());
            toAdd.getTeachers().add(teacher);
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
    public Room deleteRoom(Long roomId){
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null){
            log.error("Failed to delete room with id {}", roomId);
            throw new RoomServiceException("Failed to delete room with id" + roomId);
        }
        room.setDeleted(true);
        return roomRepository.save(room);
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
        throw new IllegalArgumentException("Failed to recognize type of entity passed to add entity");
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
            Profile teacher = profileRepository.getById(teacherId);
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
            Profile student = profileRepository.getById(studentId);
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
}
