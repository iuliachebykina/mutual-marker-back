package ru.urfu.mutual_marker.api;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "Получение комнаты по id, для авторизации необходимо быть членом комнаты или админом")
    @GetMapping("/room-by-id/{roomId}")
    @PreAuthorize("(hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomById(#roomId)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> getRoom(@PathVariable("roomId") Long roomId) {
        return new ResponseEntity<>(roomService.getRoomById(roomId), HttpStatus.OK);

    }

    @Operation(summary = "Получение комнаты по коду, для авторизации необходимо быть членом комнаты или админом")
    @GetMapping("/room-by-code/{roomCode}")
    @PreAuthorize("(hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomByRoomCode(#roomCode) or hasRole('ROLE_ADMIN'))")
    public ResponseEntity<Room> getRoomByCode(@PathVariable("roomCode") String roomCode) {

        return new ResponseEntity<>(roomService.getRoomByCode(roomCode), HttpStatus.OK);

    }

    @Operation(summary = "Получение списка комнат, доступных пользователю")
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

    @Operation(summary = "Добавление комнаты, доступно преподавателям и админам")
    @PostMapping("/room")
    @PreAuthorize("hasAnyRole('ROLE_TEACHER', 'ROLE_ADMIN')")
    public ResponseEntity<Room> addRoom(@RequestBody AddRoomDto addRoomDto) {
        return new ResponseEntity<>(roomService.addNewRoom(addRoomDto), HttpStatus.OK);

    }

    @Operation(summary = "Обновление имеющейся комнаты")
    @PutMapping("/room")
    @PreAuthorize("(hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomById(#roomId)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> updateRoom(@RequestBody Room room){
        return new ResponseEntity<>(roomService.updateRoom(room), HttpStatus.OK);

    }

    @Operation(summary = "Удаление комнаты, необходимо быть преподавателем, находящимся в комнате или админом")
    @DeleteMapping("/room/{roomId}")
    @PreAuthorize("(hasAnyRole('ROLE_ADMIN', 'ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomById(#roomId)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> deleteRoom(@PathVariable Long roomId) {

        return new ResponseEntity<>(roomService.deleteRoom(roomId), HttpStatus.OK);

    }

    @Operation(summary = "Добавление преподавателя в комнату по коду комнаты. Доступно для преподавателей в комнате и админов")
    @PutMapping("/teacher")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomByRoomCode(#addEntityToRoomDto.roomCode)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> addTeacher(@RequestBody AddEntityToRoomDto addEntityToRoomDto) {

        return new ResponseEntity<>(roomService.addEntity(addEntityToRoomDto.getEntityId(), addEntityToRoomDto.getRoomCode(), TEACHER), HttpStatus.OK);

    }

    @Operation(summary = "Эндпоинт для преподавателей, добавляющихся самостоятельно по коду")
    @PostMapping("/teacher/{roomCode}")
    public ResponseEntity<Room> selfAddTeacher(@CurrentSecurityContext(expression = "authentication.principal.username") String email, @PathVariable String roomCode) {

        return new ResponseEntity<>(roomService.addProfile(email, roomCode, TEACHER), HttpStatus.OK);

    }

    @Operation(summary = "Добавление студента в комнату по коду. Доступно преподавателям в текущей комнате и админам")
    @PutMapping("/student")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomByRoomCode(#addEntityToRoomDto.roomCode)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> addStudent(@RequestBody AddEntityToRoomDto addEntityToRoomDto) {

        return new ResponseEntity<>(roomService.addEntity(addEntityToRoomDto.getEntityId(), addEntityToRoomDto.getRoomCode(), STUDENT), HttpStatus.OK);

    }

    @Operation(summary = "Эндпоинт для студентов, добавляющихся самостоятельно по коду")
    @PostMapping("/student/{roomCode}")
    public ResponseEntity<Room> selfAddStudent(@PathVariable String roomCode, @CurrentSecurityContext(expression = "authentication.principal.username") String email) {

        return new ResponseEntity<>(roomService.addProfile(email, roomCode, STUDENT), HttpStatus.OK);

    }

    @Operation(summary = "Добавление задания в комнату, доступно админам или преподавателями в данной комнате")
    @PutMapping("/task")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomByRoomCode(#addEntityToRoomDto.roomCode)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> addTask(@RequestBody AddEntityToRoomDto addEntityToRoomDto) {

        return new ResponseEntity<>(roomService.addEntity(addEntityToRoomDto.getEntityId(), addEntityToRoomDto.getRoomCode(), TASK), HttpStatus.OK);

    }

    @Operation(summary = "Удаление преподавателя, доступно админам или преподавателям в данной комнате")
    @DeleteMapping("/teacher")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomByRoomCode(#addEntityToRoomDto.roomCode)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> deleteTeacher(@RequestBody AddEntityToRoomDto addEntityToRoomDto) {

        return new ResponseEntity<>(roomService.deleteEntity(addEntityToRoomDto.getEntityId(), addEntityToRoomDto.getRoomCode(), TEACHER), HttpStatus.OK);

    }

    @Operation(summary = "Удаление студента, доступно админам или преподавателям в данной комнате")
    @DeleteMapping("/student")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomByRoomCode(#addEntityToRoomDto.roomCode)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> deleteStudent(@RequestBody AddEntityToRoomDto addEntityToRoomDto) {

        return new ResponseEntity<>(roomService.deleteEntity(addEntityToRoomDto.getEntityId(), addEntityToRoomDto.getRoomCode(), STUDENT), HttpStatus.OK);

    }

    @Operation(summary = "Удаление задания, доступно админам или преподавателям в данной комнате")
    @DeleteMapping("/task")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomByRoomCode(#addEntityToRoomDto.roomCode)) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<Room> deleteTask(@RequestBody AddEntityToRoomDto addEntityToRoomDto) {

        return new ResponseEntity<>(roomService.deleteEntity(addEntityToRoomDto.getEntityId(), addEntityToRoomDto.getRoomCode(), TASK), HttpStatus.OK);

    }
}