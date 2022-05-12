package ru.urfu.mutual_marker.api;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import ru.urfu.mutual_marker.dto.AddEntityToRoomDto;
import ru.urfu.mutual_marker.dto.AddRoomDto;
import ru.urfu.mutual_marker.jpa.entity.Room;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;
import ru.urfu.mutual_marker.service.RoomService;
import ru.urfu.mutual_marker.service.exception.NotFoundException;

import java.util.List;

import static ru.urfu.mutual_marker.service.enums.EntityPassedToRoom.*;

@RestController
@RequestMapping("api/rooms")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomApi {
    RoomService roomService;

    @GetMapping("/room-by-id/{roomId}")
    @PreAuthorize("(hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomById(#roomId)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> getRoom(@PathVariable("roomId") Long roomId) {
        return new ResponseEntity<>(roomService.getRoomById(roomId), HttpStatus.OK);

    }

    @GetMapping("/room-by-code/{roomCode}")
    @PreAuthorize("(hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomByRoomCode(#roomCode) or hasRole('ROLE_ADMIN'))")
    public ResponseEntity<Room> getRoomByCode(@PathVariable("roomCode") String roomCode) {

        return new ResponseEntity<>(roomService.getRoomByCode(roomCode), HttpStatus.OK);

    }

    @GetMapping(value = "/rooms", params = { "page", "size" })
    public List<Room> getAllRooms(@RequestParam("page") int page,
                                  @RequestParam("size") int size,
                                  @CurrentSecurityContext(expression = "authentication.principal.username") String email,
                                  @CurrentSecurityContext(expression = "authentication.authorities") List<SimpleGrantedAuthority> roles) {
        Pageable pageable = PageRequest.of(page, size);
        SimpleGrantedAuthority role = roles.stream().findFirst().orElseThrow(() -> {
            throw new NotFoundException("Not found roles for authorize");
        });
        return roomService.getAllRoomsForProfile(pageable, email, Role.valueOf(role.getAuthority()));
    }

    @PostMapping("/room")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN')")
    public ResponseEntity<Room> addRoom(@RequestBody AddRoomDto addRoomDto) {
        return new ResponseEntity<>(roomService.addNewRoom(addRoomDto), HttpStatus.OK);

    }

    @PutMapping("/room")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomById(#room.id)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> updateRoom(@RequestBody Room room){
        return new ResponseEntity<>(roomService.updateRoom(room), HttpStatus.OK);

    }

    @DeleteMapping("/room/{roomId}")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomById(#roomId)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> deleteRoom(@PathVariable Long roomId) {

        return new ResponseEntity<>(roomService.deleteRoom(roomId), HttpStatus.OK);

    }

    @PutMapping("/teacher")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomById(#addEntityToRoomDto.entityId)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> addTeacher(@RequestBody AddEntityToRoomDto addEntityToRoomDto) {

        return new ResponseEntity<>(roomService.addEntity(addEntityToRoomDto.getEntityId(), addEntityToRoomDto.getRoomCode(), TEACHER), HttpStatus.OK);

    }

    @PostMapping("/teacher/{roomCode}")
    public ResponseEntity<Room> selfAddTeacher(@CurrentSecurityContext(expression = "authentication.principal.username") String email, @PathVariable String roomCode) {

        return new ResponseEntity<>(roomService.addProfile(email, roomCode, TEACHER), HttpStatus.OK);

    }

    @PutMapping("/student")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomById(#addEntityToRoomDto.entityId)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> addStudent(@RequestBody AddEntityToRoomDto addEntityToRoomDto) {

        return new ResponseEntity<>(roomService.addEntity(addEntityToRoomDto.getEntityId(), addEntityToRoomDto.getRoomCode(), STUDENT), HttpStatus.OK);

    }

    @PostMapping("/student/{roomCode}")
    public ResponseEntity<Room> selfAddStudent(@PathVariable String roomCode, @CurrentSecurityContext(expression = "authentication.principal.username") String email) {

        return new ResponseEntity<>(roomService.addProfile(email, roomCode, STUDENT), HttpStatus.OK);

    }

    @PutMapping("/task")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomById(#addEntityToRoomDto.entityId)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> addTask(@RequestBody AddEntityToRoomDto addEntityToRoomDto) {

        return new ResponseEntity<>(roomService.addEntity(addEntityToRoomDto.getEntityId(), addEntityToRoomDto.getRoomCode(), TASK), HttpStatus.OK);

    }

    @DeleteMapping("/teacher")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomById(#addEntityToRoomDto.entityId)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> deleteTeacher(@RequestBody AddEntityToRoomDto addEntityToRoomDto) {

        return new ResponseEntity<>(roomService.deleteEntity(addEntityToRoomDto.getEntityId(), addEntityToRoomDto.getRoomCode(), TEACHER), HttpStatus.OK);

    }

    @DeleteMapping("/student")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomById(#addEntityToRoomDto.entityId)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> deleteStudent(@RequestBody AddEntityToRoomDto addEntityToRoomDto) {

        return new ResponseEntity<>(roomService.deleteEntity(addEntityToRoomDto.getEntityId(), addEntityToRoomDto.getRoomCode(), STUDENT), HttpStatus.OK);

    }

    @DeleteMapping("/task")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomById(#addEntityToRoomDto.entityId)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> deleteTask(@RequestBody AddEntityToRoomDto addEntityToRoomDto) {

        return new ResponseEntity<>(roomService.deleteEntity(addEntityToRoomDto.getEntityId(), addEntityToRoomDto.getRoomCode(), TASK), HttpStatus.OK);

    }
}