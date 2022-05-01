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
import ru.urfu.mutual_marker.common.ProfileMapper;
import ru.urfu.mutual_marker.dto.ChangePassword;
import ru.urfu.mutual_marker.dto.profileInfo.AdminInfo;
import ru.urfu.mutual_marker.dto.profileInfo.StudentInfo;
import ru.urfu.mutual_marker.dto.profileInfo.TeacherInfo;
import ru.urfu.mutual_marker.jpa.entity.Profile;
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
    @GetMapping("/admins/{id}")
    public ResponseEntity<AdminInfo> getAdmin(@PathVariable Long id ){
        try {
            Profile admin = profileService.getProfileById(id, Role.ROLE_ADMIN);
            log.info("Got admin by id: {}", id);
            AdminInfo adminInfo = profileMapper.profileEntityToAdminDto(admin);
            return new ResponseEntity<>(adminInfo, HttpStatus.OK);
        }
        catch (Exception e){
            log.error("cause: {}", e.getMessage());
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
    @GetMapping("/teachers/{id}")
    public ResponseEntity<TeacherInfo> getTeacher(@PathVariable Long id){
        try {
            Profile teacher = profileService.getProfileById(id, Role.ROLE_TEACHER);
            log.info("Got teacher by id: {}", id);
            TeacherInfo teacherInfo = profileMapper.profileEntityToTeacherDto(teacher);
            return new ResponseEntity<>(teacherInfo, HttpStatus.OK);
        }
        catch (Exception e){
            log.error("cause: {}", e.getMessage());
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
    @GetMapping("/students/{id}")
    public ResponseEntity<StudentInfo> getStudent(@PathVariable Long id){
        try {
            Profile student = profileService.getProfileById(id, Role.ROLE_STUDENT);
            log.info("Got student by id: {}", id);
            StudentInfo studentInfo = profileMapper.profileEntityToStudentDto(student);
            return new ResponseEntity<>(studentInfo, HttpStatus.OK);
        }
        catch (Exception e){
            log.error("cause: {}", e.getMessage());
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


    @PostMapping("/students/password")
    @PreAuthorize("#changePassword.email == authentication.principal.username")
    public ResponseEntity<Void> updateStudentsPassword(@RequestBody ChangePassword changePassword){
        return updatePassword(changePassword, Role.ROLE_STUDENT);
    }

    @PostMapping("/teachers/password")
    @PreAuthorize("#changePassword.email == authentication.principal.username")
    public ResponseEntity<Void> updateTeachersPassword(@RequestBody ChangePassword changePassword){
        return updatePassword(changePassword, Role.ROLE_TEACHER);
    }

    @PostMapping("/admins/password")
    @PreAuthorize("#changePassword.email == authentication.principal.username")
    public ResponseEntity<Void> updateAdminsPassword(@RequestBody ChangePassword changePassword){
        return updatePassword(changePassword, Role.ROLE_ADMIN);
    }

    private ResponseEntity<Void> updatePassword(ChangePassword changePassword, Role role){
        try {
            profileService.updatePassword(changePassword, role);
            log.info("Updated user's password with email: {}", changePassword.getEmail());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to user's password with email: {}\ncause: {}", changePassword.getEmail(), e.getMessage());
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
}
