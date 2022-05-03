package ru.urfu.mutual_marker.api;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import ru.urfu.mutual_marker.dto.EntityToRoomDto;
import ru.urfu.mutual_marker.dto.AddRoomDto;
import ru.urfu.mutual_marker.jpa.entity.Room;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;
import ru.urfu.mutual_marker.service.RoomService;
import ru.urfu.mutual_marker.service.exception.RoomServiceException;

import java.util.List;
import java.util.Set;

import static ru.urfu.mutual_marker.service.enums.EntityPassedToRoom.*;

@RestController
@RequestMapping("api/rooms")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomApi {
    RoomService roomService;

    @GetMapping("/room/{roomId}")
    @PreAuthorize("(hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoom(#roomId)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> getRoom(@PathVariable("roomId") Long roomId) {
        try {
            return new ResponseEntity<>(roomService.getRoomById(roomId), HttpStatus.OK);
        } catch (RoomServiceException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/rooms", params = { "page", "size" })
    public List<Room> getAllRooms(@RequestParam("page") int page,
                                  @RequestParam("size") int size,
                                  @CurrentSecurityContext(expression = "authentication.principal.username") String email,
                                  @CurrentSecurityContext(expression = "authentication.authorities") List<SimpleGrantedAuthority> roles) {
        Pageable pageable = PageRequest.of(page, size);
        SimpleGrantedAuthority role = roles.stream().findFirst().orElse(null);
        return roomService.getAllRoomsForProfile(pageable, email, Role.valueOf(role.getAuthority()));
    }

    @PostMapping("/room")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN')")
    public ResponseEntity<Room> addRoom(@RequestBody AddRoomDto addRoomDto) {
        try {
            return new ResponseEntity<>(roomService.addNewRoom(addRoomDto), HttpStatus.OK);
        } catch (RoomServiceException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/room")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoom(#room.id)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> updateRoom(@RequestBody Room room){
        try{
            return new ResponseEntity<>(roomService.updateRoom(room), HttpStatus.OK);
        } catch (RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/room/{roomId}")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoom(#roomId)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> deleteRoom(@PathVariable Long roomId){
        try{
            return new ResponseEntity<>(roomService.deleteRoom(roomId), HttpStatus.OK);
        } catch (RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/teacher")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoom(#entityToRoomDto.room.id)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> addTeacher(@RequestBody EntityToRoomDto entityToRoomDto){
        try{
            return new ResponseEntity<>(roomService.addEntity(entityToRoomDto, TEACHER), HttpStatus.OK);
        } catch (IllegalArgumentException | RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/student")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoom(#entityToRoomDto.room.id)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> addStudent(@RequestBody EntityToRoomDto entityToRoomDto){
        try{
            return new ResponseEntity<>(roomService.addEntity(entityToRoomDto, STUDENT), HttpStatus.OK);
        } catch (IllegalArgumentException | RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/task")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoom(#entityToRoomDto.room.id)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> addTask(@RequestBody EntityToRoomDto entityToRoomDto){
        try{
            return new ResponseEntity<>(roomService.addEntity(entityToRoomDto, TASK), HttpStatus.OK);
        } catch (IllegalArgumentException | RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/teacher")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoom(#entityToRoomDto.room.id)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> deleteTeacher(@RequestBody EntityToRoomDto entityToRoomDto){
        try{
            return new ResponseEntity<>(roomService.deleteEntity(entityToRoomDto, TEACHER), HttpStatus.OK);
        } catch (IllegalArgumentException | RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/student")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoom(#entityToRoomDto.room.id)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> deleteStudent(@RequestBody EntityToRoomDto entityToRoomDto){
        try{
            return new ResponseEntity<>(roomService.deleteEntity(entityToRoomDto, STUDENT), HttpStatus.OK);
        } catch (IllegalArgumentException | RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/task")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoom(#entityToRoomDto.room.id)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> deleteTask(@RequestBody EntityToRoomDto entityToRoomDto){
        try{
            return new ResponseEntity<>(roomService.deleteEntity(entityToRoomDto, TASK), HttpStatus.OK);
        } catch (IllegalArgumentException | RoomServiceException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}