package ru.urfu.mutual_marker.api;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
public class ProfileApi {
    ProfileMapper profileMapper;
    ProfileService profileService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admins/{id}")
    AdminInfo getAdmin(@PathVariable Long id ){
        Profile admin = profileService.getProfileById(id);
        if(admin.getRole().equals(Role.ROLE_ADMIN))
            return profileMapper.profileEntityToAdminDto(admin);
        return null;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admins")
    List<AdminInfo> getAllAdmins(){
        List<Profile> admins = profileService.getAllProfilesByRole(Role.ROLE_ADMIN);
        return admins
                .stream()
                .map(profileMapper::profileEntityToAdminDto)
                .collect(Collectors.toList());
    }



    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER')")
    @GetMapping("/teachers/{id}")
    TeacherInfo getTeacher(@PathVariable Long id){
        Profile teacher = profileService.getProfileById(id);
        if(teacher.getRole().equals(Role.ROLE_TEACHER))
            return profileMapper.profileEntityToTeacherDto(teacher);
        return null;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER')")
    @GetMapping("/teachers")
    List<TeacherInfo> getAllTeachers(){
        List<Profile> teachers = profileService.getAllProfilesByRole( Role.ROLE_TEACHER);
        return teachers
                .stream()
                .map(profileMapper::profileEntityToTeacherDto)
                .collect(Collectors.toList());
    }




    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER')")
    @GetMapping("/students/{id}")
    StudentInfo getStudent(@PathVariable Long id){
        Profile student = profileService.getProfileById(id);
        if(student.getRole().equals(Role.ROLE_STUDENT))
            return profileMapper.profileEntityToStudentDto(student);
        return null;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STUDENT', 'ROLE_TEACHER')")
    @GetMapping("/students")
    List<StudentInfo> getAllStudents(){
        List<Profile> students = profileService.getAllProfilesByRole( Role.ROLE_STUDENT);
        return students
                .stream()
                .map(profileMapper::profileEntityToStudentDto)
                .collect(Collectors.toList());
    }




    @PutMapping("/students")
    @PreAuthorize("#student.getUsername() == authentication.principal.username or hasAnyRole('ROLE_ADMIN')")
    ResponseEntity<Profile> updateStudent(@RequestBody Profile student){
        try {
            return new ResponseEntity<>(profileService.updateProfile(student), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/teachers")
    @PreAuthorize("#teacher.getUsername() == authentication.principal.username or hasAnyRole('ROLE_ADMIN')")
    ResponseEntity<Profile> updateTeacher(@RequestBody Profile teacher){
        try {
            return new ResponseEntity<>(profileService.updateProfile(teacher), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/admins")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    ResponseEntity<Profile> updateAdmin(@RequestBody Profile admin){
        try {
            return new ResponseEntity<>(profileService.updateProfile(admin), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
