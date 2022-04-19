package ru.urfu.mutual_marker.api;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.urfu.mutual_marker.common.ProfileMapper;
import ru.urfu.mutual_marker.dto.profileInfo.AdminInfo;
import ru.urfu.mutual_marker.dto.profileInfo.StudentInfo;
import ru.urfu.mutual_marker.dto.profileInfo.TeacherInfo;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;
import ru.urfu.mutual_marker.service.ProfileService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/profile")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
@Slf4j
public class ProfileApi {
    ProfileMapper profileMapper;
    ProfileService profileService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admins/{id}")
    AdminInfo getAdmin(@PathVariable Long id ){
        Profile admin = profileService.getProfileById(id);
        if(admin.getRole().equals(Role.ROLE_ADMIN)) {
            log.info("Got admin by id: {}", id);
            return profileMapper.profileEntityToAdminDto(admin);
        }
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admins")
    List<AdminInfo> getAllAdmins(){
        List<Profile> admins = profileService.getAllProfilesByRole(Role.ROLE_ADMIN);
        log.info("Got all admins");
        return admins
                .stream()
                .map(profileMapper::profileEntityToAdminDto)
                .collect(Collectors.toList());
    }



    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER')")
    @GetMapping("/teachers/{id}")
    TeacherInfo getTeacher(@PathVariable Long id){
        Profile teacher = profileService.getProfileById(id);
        if(teacher.getRole().equals(Role.ROLE_TEACHER)) {
            log.info("Got teacher by id: {}", id);
            return profileMapper.profileEntityToTeacherDto(teacher);
        }
        return null;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER')")
    @GetMapping("/teachers")
    List<TeacherInfo> getAllTeachers(){
        List<Profile> teachers = profileService.getAllProfilesByRole( Role.ROLE_TEACHER);
        log.info("Got all teachers");
        return teachers
                .stream()
                .map(profileMapper::profileEntityToTeacherDto)
                .collect(Collectors.toList());
    }




    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER')")
    @GetMapping("/students/{id}")
    StudentInfo getStudent(@PathVariable Long id){
        Profile student = profileService.getProfileById(id);
        if(student.getRole().equals(Role.ROLE_STUDENT)) {
            log.info("Got student by id: {}", id);
            return profileMapper.profileEntityToStudentDto(student);
        }
        return null;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER')")
    @GetMapping("/students")
    List<StudentInfo> getAllStudents(){
        List<Profile> students = profileService.getAllProfilesByRole( Role.ROLE_STUDENT);
        log.info("Got all students");
        return students
                .stream()
                .map(profileMapper::profileEntityToStudentDto)
                .collect(Collectors.toList());
    }




    @PutMapping("/students")
    @PreAuthorize("#student.getUsername() == authentication.principal.username or hasAnyRole('ROLE_ADMIN')")
    ResponseEntity<Profile> updateStudent(@RequestBody Profile student){
        try {
            Profile newStudent = profileService.updateProfile(student);
            log.info("Updated student with id: {}", student.getId());
            return new ResponseEntity<>(newStudent, HttpStatus.CREATED);
        } catch (Exception e) {
            log.info("Failed to update student with id: {}", student.getId());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/teachers")
    @PreAuthorize("#teacher.getUsername() == authentication.principal.username or hasAnyRole('ROLE_ADMIN')")
    ResponseEntity<Profile> updateTeacher(@RequestBody Profile teacher){
        try {
            Profile newTeacher = profileService.updateProfile(teacher);
            log.info("Updated teacher with id: {}", teacher.getId());
            return new ResponseEntity<>(newTeacher, HttpStatus.CREATED);
        } catch (Exception e) {
            log.info("Failed to update teacher with id: {}", teacher.getId());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/admins")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    ResponseEntity<Profile> updateAdmin(@RequestBody Profile admin){
        try {
            Profile newAdmin = profileService.updateProfile(admin);
            log.info("Updated admin with id: {}", admin.getId());
            return new ResponseEntity<>(newAdmin, HttpStatus.CREATED);
        } catch (Exception e) {
            log.info("Failed to update admin with id: {}", admin.getId());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
