package ru.urfu.mutual_marker.api;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.urfu.mutual_marker.common.ProfileMapper;
import ru.urfu.mutual_marker.dto.ChangePassword;
import ru.urfu.mutual_marker.dto.profileInfo.AdminInfo;
import ru.urfu.mutual_marker.dto.profileInfo.StudentInfo;
import ru.urfu.mutual_marker.dto.profileInfo.TeacherInfo;
import ru.urfu.mutual_marker.exception.InvalidRoleException;
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
            Profile admin = profileService.getProfileById(id);
            if(!(admin.getRole().equals(Role.ROLE_ADMIN))) {
                log.error("Wrong id: {} to get ADMIN", id);
                throw new InvalidRoleException(String.format("Profile with id: %d does not have the ADMIN role", id));
            }
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
    @GetMapping("/admins")
    public List<AdminInfo> getAllAdmins(){
        List<Profile> admins = profileService.getAllProfilesByRole(Role.ROLE_ADMIN);
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
            Profile teacher = profileService.getProfileById(id);
            if(!(teacher.getRole().equals(Role.ROLE_TEACHER))) {
                log.error("Wrong id: {} to get TEACHER", id);
                throw new InvalidRoleException(String.format("Profile with id: %d does not have the TEACHER role", id));
            }
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
    @GetMapping("/teachers")
    public List<TeacherInfo> getAllTeachers(){
        List<Profile> teachers = profileService.getAllProfilesByRole( Role.ROLE_TEACHER);
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
            Profile student = profileService.getProfileById(id);
            if(!(student.getRole().equals(Role.ROLE_STUDENT))) {
                log.error("Wrong id: {} to get STUDENT", id);
                throw new InvalidRoleException(String.format("Profile with id: %d does not have the STUDENT role", id));
            }
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
    public List<StudentInfo> getAllStudents(){
        List<Profile> students = profileService.getAllProfilesByRole( Role.ROLE_STUDENT);
        log.info("Got all students");
        return students
                .stream()
                .map(profileMapper::profileEntityToStudentDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/students")
    @PreAuthorize("#student.getEmail() == authentication.principal.username or hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Profile> updateStudent(@RequestBody Profile student){
        try {
            Profile newStudent = profileService.updateProfile(student, Role.ROLE_STUDENT);
            log.info("Updated student with id: {}", student.getId());
            return new ResponseEntity<>(newStudent, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to update student with id: {} \ncause: {}", student.getId(), e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/teachers")
    @PreAuthorize("#teacher.getEmail() == authentication.principal.username or hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Profile> updateTeacher(@RequestBody Profile teacher){
        try {
            Profile newTeacher = profileService.updateProfile(teacher, Role.ROLE_TEACHER);
            log.info("Updated teacher with id: {}", teacher.getId());
            return new ResponseEntity<>(newTeacher, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to update teacher with id: {}\ncause: {}", teacher.getId(), e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/admins")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Profile> updateAdmin(@RequestBody Profile admin){
        try {
            Profile newAdmin = profileService.updateProfile(admin, Role.ROLE_ADMIN);
            log.info("Updated admin with id: {}", admin.getId());
            return new ResponseEntity<>(newAdmin, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to update admin with id: {}\ncause: {}", admin.getId(), e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    @PostMapping("/students/password")
    @PreAuthorize("#changePassword.email == authentication.principal.username or hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Boolean> updateStudentsPassword(@RequestBody ChangePassword changePassword){
        return updatePassword(changePassword, Role.ROLE_STUDENT);
    }

    @PostMapping("/teachers/password")
    @PreAuthorize("#changePassword.email == authentication.principal.username or hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Boolean> updateTeachersPassword(@RequestBody ChangePassword changePassword){
        return updatePassword(changePassword, Role.ROLE_TEACHER);
    }

    @PostMapping("/admins/password")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Boolean> updateAdminsPassword(@RequestBody ChangePassword changePassword){
        return updatePassword(changePassword, Role.ROLE_ADMIN);
    }

    private ResponseEntity<Boolean> updatePassword(ChangePassword changePassword, Role role){
        try {
            profileService.updatePassword(changePassword, role);
            log.info("Updated user's password with email: {}", changePassword.getEmail());
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to user's password with email: {}\ncause: {}", changePassword.getEmail(), e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
