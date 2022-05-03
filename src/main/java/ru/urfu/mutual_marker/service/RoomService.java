package ru.urfu.mutual_marker.service;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.common.RoomMapper;
import ru.urfu.mutual_marker.dto.EntityToRoomDto;
import ru.urfu.mutual_marker.dto.AddRoomDto;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Room;
import ru.urfu.mutual_marker.jpa.entity.Task;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;
import ru.urfu.mutual_marker.jpa.repository.ProfileRepository;
import ru.urfu.mutual_marker.jpa.repository.RoomRepository;
import ru.urfu.mutual_marker.service.enums.EntityPassedToRoom;
import ru.urfu.mutual_marker.service.exception.RoomServiceException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Service
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoomService {
    //Get many to many repository, acquire all ids and get entities through them
    //Inefficient but it kinda works i guess, dont wanna sleep but gotta stick to at least some kind of schedule
    RoomRepository roomRepository;
    ProfileRepository profileRepository;
    RoomMapper roomMapper;

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
    public Room addNewRoom(AddRoomDto addRoomDto){
        Room toAdd = roomMapper.addRoomDtoToRoom(addRoomDto);
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
