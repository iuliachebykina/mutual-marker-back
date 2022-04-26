package ru.urfu.mutual_marker.api;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.urfu.mutual_marker.dto.EntityToRoomDto;
import ru.urfu.mutual_marker.dto.AddRoomDto;
import ru.urfu.mutual_marker.jpa.entity.Room;
import ru.urfu.mutual_marker.service.RoomService;
import ru.urfu.mutual_marker.service.exception.RoomServiceException;

import java.util.List;

import static ru.urfu.mutual_marker.service.enums.EntityPassedToRoom.*;

@RestController
@RequestMapping("/rooms")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomApi {
    RoomService roomService;

    @GetMapping("/room/{roomId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER')")
    public ResponseEntity<Room> getRoom(@PathVariable Long roomId) {
        try {
            return new ResponseEntity<>(roomService.getRoomById(roomId), HttpStatus.OK);
        } catch (RoomServiceException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<Room>> getAllRooms() {
        return new ResponseEntity<>(roomService.getAllRooms(), HttpStatus.OK);
    }

    @PostMapping("/room")
    public ResponseEntity<Room> addRoom(@RequestBody AddRoomDto addRoomDto) {
        try {
            return new ResponseEntity<>(roomService.addNewRoom(addRoomDto), HttpStatus.OK);
        } catch (RoomServiceException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/room")
    public ResponseEntity<Room> updateRoom(@RequestBody Room room){
        try{
            return new ResponseEntity<>(roomService.updateRoom(room), HttpStatus.OK);
        } catch (RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/room")
    public ResponseEntity<Room> deleteRoom(@RequestBody Room room){
        try{
            return new ResponseEntity<>(roomService.deleteRoom(room), HttpStatus.OK);
        } catch (RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/teacher")
    public ResponseEntity<Room> addTeacher(@RequestBody EntityToRoomDto entityToRoomDto){
        try{
            return new ResponseEntity<>(roomService.addEntity(entityToRoomDto, TEACHER), HttpStatus.OK);
        } catch (IllegalArgumentException | RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/student")
    public ResponseEntity<Room> addStudent(@RequestBody EntityToRoomDto entityToRoomDto){
        try{
            return new ResponseEntity<>(roomService.addEntity(entityToRoomDto, STUDENT), HttpStatus.OK);
        } catch (IllegalArgumentException | RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/task")
    public ResponseEntity<Room> addTask(@RequestBody EntityToRoomDto entityToRoomDto){
        try{
            return new ResponseEntity<>(roomService.addEntity(entityToRoomDto, TASK), HttpStatus.OK);
        } catch (IllegalArgumentException | RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/teacher")
    public ResponseEntity<Room> deleteTeacher(@RequestBody EntityToRoomDto entityToRoomDto){
        try{
            return new ResponseEntity<>(roomService.deleteEntity(entityToRoomDto, TEACHER), HttpStatus.OK);
        } catch (IllegalArgumentException | RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/student")
    public ResponseEntity<Room> deleteStudent(@RequestBody EntityToRoomDto entityToRoomDto){
        try{
            return new ResponseEntity<>(roomService.deleteEntity(entityToRoomDto, STUDENT), HttpStatus.OK);
        } catch (IllegalArgumentException | RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/task")
    public ResponseEntity<Room> deleteTask(@RequestBody EntityToRoomDto entityToRoomDto){
        try{
            return new ResponseEntity<>(roomService.deleteEntity(entityToRoomDto, TASK), HttpStatus.OK);
        } catch (IllegalArgumentException | RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}