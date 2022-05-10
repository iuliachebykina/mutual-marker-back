package ru.urfu.mutual_marker.api;


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
import ru.urfu.mutual_marker.dto.profileInfo.StudentInfo;
import ru.urfu.mutual_marker.dto.profileInfo.TeacherInfo;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;
import ru.urfu.mutual_marker.service.ProfileService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/profile")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ProfileApi {
    ProfileService profileService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admins/{email}")
    public ResponseEntity<Object> getAdmin(@PathVariable String email ){
        try {
            AdminInfo adminInfo = profileService.getAdmin(email);
            log.info("Got admin by id: {}", email);
            return new ResponseEntity<>(adminInfo, HttpStatus.OK);
        }
        catch (Exception e){
            log.error("Failed to gotten admin with email: {}\ncause: {}", email, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(value = "/admins", params = { "page", "size" })
    public List<AdminInfo> getAllAdmins(@RequestParam("page") int page,
                                        @RequestParam("size") int size){
        Pageable pageable = PageRequest.of(page, size);
        log.info("Got all admins");
        return profileService.getAllAdmins(pageable);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/teachers/{email}")
    public ResponseEntity<Object> getTeacher(@PathVariable String email){
        try {
            TeacherInfo teacherInfo = profileService.getTeacher(email);
            log.info("Got teacher by email: {}", email);
            return new ResponseEntity<>(teacherInfo, HttpStatus.OK);
        }
        catch (Exception e){
            log.error("Failed to gotten teacher with email: {}\ncause: {}", email, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/teachers", params = { "page", "size" })
    public List<TeacherInfo> getAllTeachers(@RequestParam("page") int page,
                                            @RequestParam("size") int size){
        Pageable pageable = PageRequest.of(page, size);
        log.info("Got all teachers");
        return profileService.getAllTeachers(pageable);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/students/{email}")
    public ResponseEntity<Object> getStudent(@PathVariable String email){

        StudentInfo studentInfo = profileService.getStudent(email);
        log.info("Got student by email: {}", email);
        return new ResponseEntity<>(studentInfo, HttpStatus.OK);

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/students")
    public List<StudentInfo> getAllStudents(@RequestParam("page") int page,
                                            @RequestParam("size") int size){
        Pageable pageable = PageRequest.of(page, size);
        log.info("Got all students");
        return profileService.getAllStudents(pageable);
    }

    @PatchMapping("/students")
    @PreAuthorize("#student.getEmail() == authentication.principal.username or hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Object> updateStudent(@RequestBody Profile student){
        return updateProfile(student, Role.ROLE_STUDENT);
    }

    @PatchMapping("/teachers")
    @PreAuthorize("#teacher.getEmail() == authentication.principal.username or hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Object> updateTeacher(@RequestBody Profile teacher) {
        return updateProfile(teacher, Role.ROLE_TEACHER);
    }

    @PatchMapping("/admins")
    @PreAuthorize("#admin.getEmail() == authentication.principal.username")
    public ResponseEntity<Object> updateAdmin(@RequestBody Profile admin) {
        return updateProfile(admin, Role.ROLE_ADMIN);
    }

    private ResponseEntity<Object> updateProfile(Profile profile, Role role) {

        Profile newAdmin = profileService.updateProfile(profile, role);
        log.info("Updated profile with id: {}", profile.getId());
        return new ResponseEntity<>(newAdmin, HttpStatus.OK);

    }

    @PostMapping("/password")
    @PreAuthorize("#changePassword.email == authentication.principal.username")
    private ResponseEntity<Object> updatePassword(@RequestBody ChangePassword changePassword) {

        profileService.updatePassword(changePassword);
        log.info("Updated user's password with email: {}", changePassword.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PostMapping("/email")
    @PreAuthorize("#changeEmail.oldEmail == authentication.principal.username")
    private ResponseEntity<Object> updateEmail(@RequestBody ChangeEmail changeEmail) {

        profileService.updateEmail(changeEmail);
        log.info("Updated user's email: {}", changeEmail.getOldEmail());
        return new ResponseEntity<>(HttpStatus.OK);

    }


    @DeleteMapping("/students/{email}")
    @PreAuthorize("#email == authentication.principal.username or hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Object> deleteStudent(@PathVariable String email){
        return deleteProfile(email, Role.ROLE_STUDENT);
    }

    @DeleteMapping("/teachers/{email}")
    @PreAuthorize("#email == authentication.principal.username or hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Object> deleteTeacher(@PathVariable String email){
        return deleteProfile(email, Role.ROLE_TEACHER);
    }

    @DeleteMapping("/admins/{email}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Object> deleteAdmin(@PathVariable String email){
        return deleteProfile(email, Role.ROLE_ADMIN);
    }

    private ResponseEntity<Object> deleteProfile(String email, Role role) {

        profileService.deleteProfile(email, role);
        log.info("Deleted user with email: {}", email);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping("/room/students/{roomId}")
    @PreAuthorize("@roomAccessEvaluator.isMemberOfRoom(#roomId) or hasRole('ROLE_ADMIN')")
    public List<StudentInfo> getStudentsInRoom(@PathVariable Long roomId, @RequestParam("page") int page,
                                           @RequestParam("size") int size){
        Pageable pageable = PageRequest.of(page, size);
        log.info("Got all students in room with id: {}", roomId);
        return profileService.getStudentsInRoom(roomId, pageable);
    }

    @GetMapping("/room/teachers/{roomId}")
    @PreAuthorize("@roomAccessEvaluator.isMemberOfRoom(#roomId) or hasRole('ROLE_ADMIN')")
    public List<TeacherInfo> getTeachersInRoom(@PathVariable Long roomId, @RequestParam("page") int page,
                                           @RequestParam("size") int size){
        Pageable pageable = PageRequest.of(page, size);
        log.info("Got all teachers in room with id: {}", roomId);
        return profileService.getTeachersInRoom(roomId, pageable);
    }
}
