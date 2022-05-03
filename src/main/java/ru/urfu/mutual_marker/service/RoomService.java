package ru.urfu.mutual_marker.service;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.common.RoomMapper;
import ru.urfu.mutual_marker.dto.AddRoomDto;
import ru.urfu.mutual_marker.dto.EntityToRoomDto;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Room;
import ru.urfu.mutual_marker.jpa.entity.Task;
import ru.urfu.mutual_marker.jpa.repository.ProfileRepository;
import ru.urfu.mutual_marker.jpa.repository.RoomRepository;
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

    @Transactional
    public Room getRoomById(Long roomId){
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room == null){
            log.error("Failed to find room with id {}", roomId);
        }
        return room;
    }

    @Transactional
    public List<Room> getAllRooms(){
        return roomRepository.findAll();
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
        try {
            return roomRepository.save(room);
        } catch (Exception e){
            log.error("Failed to update room, exception message: {}, stacktrace: {}",
                    e.getLocalizedMessage(), e.getStackTrace());
            throw new RoomServiceException("Failed to update");
        }
    }

    @Transactional
    public Room deleteRoom(Room room){
        room.setDeleted(true);
        return this.updateRoom(room);
    }

    @Transactional
    public Room addEntity(EntityToRoomDto entityToRoomDto, EntityPassedToRoom entity){
        switch (entity){
            case TASK:
                return addTask((Task) entityToRoomDto.getEntity(), entityToRoomDto.getRoom());
            case STUDENT:
                return addStudent((Profile) entityToRoomDto.getEntity(), entityToRoomDto.getRoom());
            case TEACHER:
                return addTeacher((Profile) entityToRoomDto.getEntity(), entityToRoomDto.getRoom());
        }
        log.error("Failed to recognize type of entity passed to add entity room method");
        throw new IllegalArgumentException("Failed to recognize type of entity passed to add entity");
    }

    @Transactional
    public Room deleteEntity(EntityToRoomDto entityToRoomDto, EntityPassedToRoom entity){
        switch (entity){
            case TASK:
                return deleteTask((Task) entityToRoomDto.getEntity(), entityToRoomDto.getRoom());
            case STUDENT:
                return deleteStudent((Profile) entityToRoomDto.getEntity(), entityToRoomDto.getRoom());
            case TEACHER:
                return deleteTeacher((Profile) entityToRoomDto.getEntity(), entityToRoomDto.getRoom());
        }
        log.error("Failed to recognize type of entity passed to deleteEntity room method");
        throw new IllegalArgumentException("Failed to recognize type of entity passed to delete entity");
    }

    private Room deleteTeacher(Profile teacher, Room room){
        room.removeTeacher(teacher.getId());
        try{
            return roomRepository.save(room);
        } catch (Exception e){
            log.error("Failed to delete teacher from room with id {}, error message {}, stacktrace {}",
                    room.getId(), e.getLocalizedMessage(), e.getStackTrace());
            throw new RoomServiceException("Failed to delete teacher");
        }
    }

    private Room deleteStudent(Profile student, Room room){
        room.removeStudent(student.getId());
        try{
            return roomRepository.save(room);
        } catch (Exception e){
            log.error("Failed to delete student from room with id {}, error message {}, stacktrace {}",
                    room.getId(), e.getLocalizedMessage(), e.getStackTrace());
            throw new RoomServiceException("Failed to delete student");
        }
    }

    private Room deleteTask(Task task, Room room){
        room.removeTask(task.getId());
        try{
            return roomRepository.save(room);
        } catch (Exception e){
            log.error("Failed to delete task from room with id {}, error message {}, stacktrace {}",
                    room.getId(), e.getLocalizedMessage(), e.getStackTrace());
            throw new RoomServiceException("Failed to delete task");
        }
    }

    private Room addTeacher(Profile teacher, Room room){
        room.addTeacher(teacher);
        try{
            return roomRepository.save(room);
        } catch (Exception e){
            log.error("Failed to add teacher to room with id {}, error message {}, stacktrace {}",
                    room.getId(), e.getLocalizedMessage(), e.getStackTrace());
            throw new RoomServiceException("Failed to add teacher");
        }
    }

    private Room addStudent(Profile student, Room room){
        room.addStudent(student);
        try{
            return roomRepository.save(room);
        } catch (Exception e){
            log.error("Failed to add student to room with id {}, error message{}, stacktrace {}",
                    room.getId(), e.getLocalizedMessage(), e.getStackTrace());
            throw new RoomServiceException("Failed to add student");
        }
    }

    private Room addTask(Task task, Room room){
        room.addTask(task);
        try{
            return roomRepository.save(room);
        } catch (Exception e){
            log.error("Failed to add task to room with id {}, error message{}, stacktrace {}",
                    room.getId(), e.getLocalizedMessage(), e.getStackTrace());
            throw new RoomServiceException("Failed to add task");
        }
    }
}
