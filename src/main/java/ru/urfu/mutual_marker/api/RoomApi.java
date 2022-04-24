package ru.urfu.mutual_marker.api;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/getRoom/{roomId}")
    public ResponseEntity<Room> getRoom(@PathVariable Long roomId) {
        try {
            return new ResponseEntity<>(roomService.getRoomById(roomId), HttpStatus.OK);
        } catch (RoomServiceException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAllRooms")
    public ResponseEntity<List<Room>> getAllRooms() {
        return new ResponseEntity<>(roomService.getAllRooms(), HttpStatus.OK);
    }

    @PostMapping("/addRoom")
    public ResponseEntity<Room> addRoom(@RequestBody AddRoomDto addRoomDto) {
        try {
            return new ResponseEntity<>(roomService.addNewRoom(addRoomDto), HttpStatus.OK);
        } catch (RoomServiceException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateRoom")
    public ResponseEntity<Room> updateRoom(@RequestBody Room room){
        try{
            return new ResponseEntity<>(roomService.updateRoom(room), HttpStatus.OK);
        } catch (RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/deleteRoom")
    public ResponseEntity<Room> deleteRoom(@RequestBody Room room){
        try{
            return new ResponseEntity<>(roomService.deleteRoom(room), HttpStatus.OK);
        } catch (RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/addTeacher")
    public ResponseEntity<Room> addTeacher(@RequestBody EntityToRoomDto entityToRoomDto){
        try{
            return new ResponseEntity<>(roomService.addEntity(entityToRoomDto, TEACHER), HttpStatus.OK);
        } catch (IllegalArgumentException | RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/addStudent")
    public ResponseEntity<Room> addStudent(@RequestBody EntityToRoomDto entityToRoomDto){
        try{
            return new ResponseEntity<>(roomService.addEntity(entityToRoomDto, STUDENT), HttpStatus.OK);
        } catch (IllegalArgumentException | RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/addTask")
    public ResponseEntity<Room> addTask(@RequestBody EntityToRoomDto entityToRoomDto){
        try{
            return new ResponseEntity<>(roomService.addEntity(entityToRoomDto, TASK), HttpStatus.OK);
        } catch (IllegalArgumentException | RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/deleteTeacher")
    public ResponseEntity<Room> deleteTeacher(@RequestBody EntityToRoomDto entityToRoomDto){
        try{
            return new ResponseEntity<>(roomService.deleteEntity(entityToRoomDto, TEACHER), HttpStatus.OK);
        } catch (IllegalArgumentException | RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/deleteStudent")
    public ResponseEntity<Room> deleteStudent(@RequestBody EntityToRoomDto entityToRoomDto){
        try{
            return new ResponseEntity<>(roomService.deleteEntity(entityToRoomDto, STUDENT), HttpStatus.OK);
        } catch (IllegalArgumentException | RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/deleteTask")
    public ResponseEntity<Room> deleteTask(@RequestBody EntityToRoomDto entityToRoomDto){
        try{
            return new ResponseEntity<>(roomService.deleteEntity(entityToRoomDto, TASK), HttpStatus.OK);
        } catch (IllegalArgumentException | RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}