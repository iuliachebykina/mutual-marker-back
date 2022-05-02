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
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;
import ru.urfu.mutual_marker.common.ProfileMapper;
import ru.urfu.mutual_marker.dto.ChangeEmail;
import ru.urfu.mutual_marker.dto.ChangePassword;
import ru.urfu.mutual_marker.dto.profileInfo.AdminInfo;
import ru.urfu.mutual_marker.dto.profileInfo.StudentInfo;
import ru.urfu.mutual_marker.dto.profileInfo.TeacherInfo;
import ru.urfu.mutual_marker.jpa.entity.Attachment;
import ru.urfu.mutual_marker.jpa.entity.NumberOfGraded;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Room;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;
import ru.urfu.mutual_marker.service.ProfileService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/profile")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class ProfileApi {
    ProfileMapper profileMapper;
    ProfileService profileService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admins/{email}")
    public ResponseEntity<AdminInfo> getAdmin(@PathVariable String email ){
        try {
            Profile admin = profileService.getProfileByEmail(email, Role.ROLE_ADMIN);
            log.info("Got admin by id: {}", email);
            AdminInfo adminInfo = profileMapper.profileEntityToAdminDto(admin);
            return new ResponseEntity<>(adminInfo, HttpStatus.OK);
        }
        catch (Exception e){
            log.error("Failed to gotten admin with email: {}\ncause: {}", email, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(value = "/admins", params = { "page", "size" })
    public List<AdminInfo> getAllAdmins(@RequestParam("page") int page,
                                        @RequestParam("size") int size){
        Pageable pageable = PageRequest.of(page, size);
        List<Profile> admins = profileService.getAllProfilesByRole(Role.ROLE_ADMIN, pageable);
        log.info("Got all admins");
        return admins
                .stream()
                .map(profileMapper::profileEntityToAdminDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER')")
    @GetMapping("/teachers/{email}")
    public ResponseEntity<TeacherInfo> getTeacher(@PathVariable String email){
        try {
            Profile teacher = profileService.getProfileByEmail(email, Role.ROLE_TEACHER);
            log.info("Got teacher by email: {}", email);
            TeacherInfo teacherInfo = profileMapper.profileEntityToTeacherDto(teacher);
            return new ResponseEntity<>(teacherInfo, HttpStatus.OK);
        }
        catch (Exception e){
            log.error("Failed to gotten teacher with email: {}\ncause: {}", email, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER')")
    @GetMapping(value = "/teachers", params = { "page", "size" })
    public List<TeacherInfo> getAllTeachers(@RequestParam("page") int page,
                                            @RequestParam("size") int size){
        Pageable pageable = PageRequest.of(page, size);
        List<Profile> teachers = profileService.getAllProfilesByRole( Role.ROLE_TEACHER, pageable);
        log.info("Got all teachers");
        return teachers
                .stream()
                .map(profileMapper::profileEntityToTeacherDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER')")
    @GetMapping("/students/{email}")
    public ResponseEntity<StudentInfo> getStudent(@PathVariable String email){
        try {
            Profile student = profileService.getProfileByEmail(email, Role.ROLE_STUDENT);
            log.info("Got student by email: {}", email);
            StudentInfo studentInfo = profileMapper.profileEntityToStudentDto(student);
            return new ResponseEntity<>(studentInfo, HttpStatus.OK);
        }
        catch (Exception e){
            log.error("Failed to gotten student with email: {}\ncause: {}", email, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER')")
    @GetMapping("/students")
    public List<StudentInfo> getAllStudents(@RequestParam("page") int page,
                                            @RequestParam("size") int size){
        Pageable pageable = PageRequest.of(page, size);
        List<Profile> students = profileService.getAllProfilesByRole( Role.ROLE_STUDENT,pageable);
        log.info("Got all students");
        return students
                .stream()
                .map(profileMapper::profileEntityToStudentDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/students")
    @PreAuthorize("#student.getEmail() == authentication.principal.username or hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Profile> updateStudent(@RequestBody Profile student){
        return updateProfile(student, Role.ROLE_STUDENT);
    }

    @PatchMapping("/teachers")
    @PreAuthorize("#teacher.getEmail() == authentication.principal.username or hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Profile> updateTeacher(@RequestBody Profile teacher){
        return updateProfile(teacher, Role.ROLE_TEACHER);
    }

    @PatchMapping("/admins")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Profile> updateAdmin(@RequestBody Profile admin){
        return updateProfile(admin, Role.ROLE_ADMIN);
    }

    private ResponseEntity<Profile> updateProfile(Profile profile, Role role){
        try {
            Profile newAdmin = profileService.updateProfile(profile, role);
            log.info("Updated profile with id: {}", profile.getId());
            return new ResponseEntity<>(newAdmin, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to update profile with id: {}\ncause: {}", profile.getId(), e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/password")
    @PreAuthorize("#changePassword.email == authentication.principal.username")
    private ResponseEntity<Void> updatePassword(@RequestBody ChangePassword changePassword){
        try {
            profileService.updatePassword(changePassword);
            log.info("Updated user's password with email: {}", changePassword.getEmail());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to update user's password with email: {}\ncause: {}", changePassword.getEmail(), e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/email")
    @PreAuthorize("#changeEmail.oldEmail == authentication.principal.username")
    private ResponseEntity<Void> updateEmail(@RequestBody ChangeEmail changeEmail){
        try {
            profileService.updateEmail(changeEmail);
            log.info("Updated user's email: {}", changeEmail.getOldEmail());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to update user's email: {}\ncause: {}", changeEmail.getOldEmail(), e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/students/{email}")
    @PreAuthorize("#email == authentication.principal.username or hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteStudent(@PathVariable String email){
        return deleteProfile(email, Role.ROLE_STUDENT);
    }

    @DeleteMapping("/teachers/{email}")
    @PreAuthorize("#email == authentication.principal.username or hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteTeacher(@PathVariable String email){
        return deleteProfile(email, Role.ROLE_TEACHER);
    }

    @DeleteMapping("/admins/{email}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteAdmin(@PathVariable String email){
        return deleteProfile(email, Role.ROLE_ADMIN);
    }

    private ResponseEntity<Void> deleteProfile(String email, Role role) {
        try {
            profileService.deleteProfile(email, role);
            log.info("Deleted user with email: {}", email);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to delete user with email : {}\ncause: {}", email, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/rooms")
    public List<Room> getRooms(@CurrentSecurityContext(expression="authentication.principal.username") String email){
        log.info("Got rooms by user with email: {}", email);
        return profileService.getRooms(email);
    }

    @GetMapping("/attachments")
    public List<Attachment> getAttachments(@CurrentSecurityContext(expression="authentication.principal.username") String email){
        log.info("Got attachments by user with email: {}", email);
        return profileService.getAttachments(email);
    }

    @GetMapping("/number-of-graded-set")
    public List<NumberOfGraded> getNumberOfGradedSet(@CurrentSecurityContext(expression="authentication.principal.username") String email){
        log.info("Got number of graded set by user with email: {}", email);
        return profileService.getNumberOfGradedSet(email);
    }
}
