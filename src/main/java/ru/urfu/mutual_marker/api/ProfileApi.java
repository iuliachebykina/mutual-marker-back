package ru.urfu.mutual_marker.api;


import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.urfu.mutual_marker.dto.ChangeEmail;
import ru.urfu.mutual_marker.dto.ChangePassword;
import ru.urfu.mutual_marker.dto.profileInfo.AdminInfo;
import ru.urfu.mutual_marker.dto.profileInfo.ProfileInfo;
import ru.urfu.mutual_marker.dto.profileInfo.StudentInfo;
import ru.urfu.mutual_marker.dto.profileInfo.TeacherInfo;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.security.RoomAccessEvaluator;
import ru.urfu.mutual_marker.service.ProfileService;
import ru.urfu.mutual_marker.service.exception.RoomServiceException;

import java.util.List;

@RestController
@RequestMapping(value = "/api/profile")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ProfileApi {
    ProfileService profileService;
    RoomAccessEvaluator roomAccessEvaluator;

    @Operation(summary = "Получение инфы об админе по почте")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admins/{email}")
    public ResponseEntity<AdminInfo> getAdmin(@PathVariable String email) {
        AdminInfo adminInfo = profileService.getAdmin(email);
        log.info("Got admin by email: {}", email);
        return new ResponseEntity<>(adminInfo, HttpStatus.OK);
    }

    @Operation(summary = "Получение инфы об админе по id")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin-by-id/{id}")
    public ResponseEntity<AdminInfo> getAdmin(@PathVariable Long id) {
        AdminInfo adminInfo = profileService.getAdmin(id);
        log.info("Got admin by id: {}", id);
        return new ResponseEntity<>(adminInfo, HttpStatus.OK);
    }


    @Operation(summary = "Получение инфы обо всех админах")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(value = "/admins", params = { "page", "size" })
    public List<AdminInfo> getAllAdmins(@RequestParam("page") int page,
                                        @RequestParam("size") int size){
        Pageable pageable = PageRequest.of(page, size);
        log.info("Got all admins");
        return profileService.getAllAdmins(pageable);
    }

    @Operation(summary = "Получение инфы об учителе по почте")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/teachers/{email}")
    public ResponseEntity<TeacherInfo> getTeacher(@PathVariable String email) {

        TeacherInfo teacherInfo = profileService.getTeacher(email);
        log.info("Got teacher by email: {}", email);
        return new ResponseEntity<>(teacherInfo, HttpStatus.OK);

    }

    @Operation(summary = "Получение инфы об учителе по id")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/teacher-by-id/{id}")
    public ResponseEntity<TeacherInfo> getTeacher(@PathVariable Long id) {

        TeacherInfo teacherInfo = profileService.getTeacher(id);
        log.info("Got teacher by id: {}", id);
        return new ResponseEntity<>(teacherInfo, HttpStatus.OK);

    }

    @Operation(summary = "Получение инфы обо всех учителях")
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/teachers", params = { "page", "size" })
    public List<TeacherInfo> getAllTeachers(@RequestParam("page") int page,
                                            @RequestParam("size") int size){
        Pageable pageable = PageRequest.of(page, size);
        log.info("Got all teachers");
        return profileService.getAllTeachers(pageable);
    }

    @Operation(summary = "Получение инфы о студенте по почте")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/students/{email}")
    public ResponseEntity<StudentInfo> getStudent(@PathVariable String email) {

        StudentInfo studentInfo = profileService.getStudent(email);
        log.info("Got student by email: {}", email);
        return new ResponseEntity<>(studentInfo, HttpStatus.OK);

    }

    @Operation(summary = "Получение инфы о студенте по id")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/student-by-id/{id}")
    public ResponseEntity<StudentInfo> getStudent(@PathVariable Long id) {

        StudentInfo studentInfo = profileService.getStudent(id);
        log.info("Got student by id: {}", id);
        return new ResponseEntity<>(studentInfo, HttpStatus.OK);

    }

    @Operation(summary = "Получение инфы о всех студентах")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/students")
    public List<StudentInfo> getAllStudents(@RequestParam("page") int page,
                                            @RequestParam("size") int size){
        Pageable pageable = PageRequest.of(page, size);
        log.info("Got all students");
        return profileService.getAllStudents(pageable);
    }


    @Operation(summary = "Обновление профиля авторизованного пользователя. ОБНОВЛЯТЬ ПОЧТУ И ПАРОЛЬ В ЭТОМ МЕТОДЕ НЕЛЬЗЯ")
    @PatchMapping()
    @PreAuthorize("#profile.getEmail() == authentication.principal.username or hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Profile> updateProfile(@RequestBody Profile profile) {
        Profile newProfile = profileService.updateProfile(profile);
        log.info("Updated profile with id: {}", profile.getId());
        return new ResponseEntity<>(newProfile, HttpStatus.OK);
    }


    @Operation(summary = "Обновление пароля авторизованного пользователя")
    @PostMapping("/password")
    @PreAuthorize("#changePassword.email == authentication.principal.username or hasAnyRole('ROLE_ADMIN')")
    private ResponseEntity<Profile> updatePassword(@RequestBody ChangePassword changePassword) {
        profileService.updatePassword(changePassword);
        log.info("Updated user's password with email: {}", changePassword.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @Operation(summary = "Обновление почты авторизованного пользователя")
    @PostMapping("/email")
    @PreAuthorize("#changeEmail.oldEmail == authentication.principal.username or hasAnyRole('ROLE_ADMIN')")
    private ResponseEntity<Profile> updateEmail(@RequestBody ChangeEmail changeEmail) {
        profileService.updateEmail(changeEmail);
        log.info("Updated user's email: {}", changeEmail.getOldEmail());
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @Operation(summary = "Удаление профиля по почте. Может удалять сам пользователь или администратор")
    @DeleteMapping("/{email}")
    @PreAuthorize("#email == authentication.principal.username or hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Profile> deleteProfile(@PathVariable String email) {
        profileService.deleteProfile(email);
        log.info("Deleted user with email: {}", email);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @Operation(summary = "Получение всех студентов в комнате по id комнаты")
    @GetMapping("/room/students/{roomId}")
   // @PreAuthorize("(hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomById(#roomId)) or hasRole('ROLE_ADMIN')")
    public List<StudentInfo> getStudentsInRoom(@PathVariable Long roomId, @RequestParam("page") int page,
                                               @RequestParam("size") int size){
        if(!roomAccessEvaluator.isMemberOfRoomById(roomId)){
            throw new RoomServiceException(roomId.toString());
        }
        Pageable pageable = PageRequest.of(page, size);
        log.info("Got all students in room with id: {}", roomId);
        return profileService.getStudentsInRoom(roomId, pageable);
    }

    @Operation(summary = "Получение всех учителей в комнате по id комнаты")
    @GetMapping("/room/teachers/{roomId}")
//     @PreAuthorize("(hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER') and @roomAccessEvaluator.isMemberOfRoomById(#roomId)) or hasRole('ROLE_ADMIN')")
    public List<TeacherInfo> getTeachersInRoom(@PathVariable Long roomId, @RequestParam("page") int page,
                                               @RequestParam("size") int size){
        if(!roomAccessEvaluator.isMemberOfRoomById(roomId)){
            throw new RoomServiceException(roomId.toString());
        }
        Pageable pageable = PageRequest.of(page, size);
        log.info("Got all teachers in room with id: {}", roomId);
        return profileService.getTeachersInRoom(roomId, pageable);
    }

    @Operation(summary = "Поиск преподавателя по имени или почте")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/room/teachers/search/{search}")
    public List<ProfileInfo> getTeachersByNameOrEmail(@PathVariable String search, @RequestParam("page") int page,
                                                     @RequestParam("size") int size){
        Pageable pageable = PageRequest.of(page, size);
        return profileService.searchTeachers(search, pageable);


    }

    @Operation(summary = "Поиск студента по имени или почте")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/room/students/search/{search}")
    public List<ProfileInfo> getStudentsByNameOrEmail(@PathVariable String search, @RequestParam("page") int page,
                                                     @RequestParam("size") int size){
        Pageable pageable = PageRequest.of(page, size);
        return profileService.searchStudents(search, pageable);


    }

    @Operation(summary = "Получение количества участников в комнате по ид комнаты")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/room/count-of-members/{roomId}")
    public Long getCountOfMembersInRoom(@PathVariable Long roomId){
        return profileService.getCountOfMembersInRoom(roomId);
    }
}
