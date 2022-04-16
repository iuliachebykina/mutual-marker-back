package ru.urfu.mutual_marker.api;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.urfu.mutual_marker.common.ProfileMapper;
import ru.urfu.mutual_marker.dto.RegistrationInfo;
import ru.urfu.mutual_marker.dto.profileInfo.AdminInfo;
import ru.urfu.mutual_marker.dto.profileInfo.StudentInfo;
import ru.urfu.mutual_marker.dto.profileInfo.TeacherInfo;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;
import ru.urfu.mutual_marker.service.ProfileService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/profile_api")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProfileApi {
    ProfileMapper profileMapper;
    ProfileService profileService;

    @Secured({"ROLE_ADMIN"})
    @GetMapping("/admins/{id}")
    AdminInfo getAdmin(@PathVariable Long id ){
        Profile admin = profileService.getProfileById(id);
        if(admin.getRole().equals(Role.ROLE_ADMIN))
            return profileMapper.profileEntityToAdminDto(admin);
        return null;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/admins")
    List<AdminInfo> getAllAdmins(){
        List<Profile> admins = profileService.getAllProfilesByRole(Role.ROLE_ADMIN);
        return admins
                .stream()
                .map(profileMapper::profileEntityToAdminDto)
                .collect(Collectors.toList());
    }

    @Secured("ROLE_ADMIN")
    @PostMapping ("/admins")
    ResponseEntity<Profile> addAdmin(@RequestBody RegistrationInfo admin){
        try {
            return new ResponseEntity<>(profileService.saveProfile(admin), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
    @GetMapping("/teachers/{id}")
    TeacherInfo getTeacher(@PathVariable Long id){
        Profile teacher = profileService.getProfileById(id);
        if(teacher.getRole().equals(Role.ROLE_TEACHER))
            return profileMapper.profileEntityToTeacherDto(teacher);
        return null;
    }

    @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
    @GetMapping("/teachers")
    List<TeacherInfo> getAllTeachers(){
        List<Profile> teachers = profileService.getAllProfilesByRole( Role.ROLE_TEACHER);
        return teachers
                .stream()
                .map(profileMapper::profileEntityToTeacherDto)
                .collect(Collectors.toList());
    }

    @RolesAllowed("ROLE_ADMIN")
    @PostMapping ("/teachers")
    ResponseEntity<Profile> addTeacher(@RequestBody RegistrationInfo teacher){
        try {
            return new ResponseEntity<>(profileService.saveProfile(teacher), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
    @GetMapping("/students/{id}")
    StudentInfo getStudent(@PathVariable Long id){
        Profile student = profileService.getProfileById(id);
        if(student.getRole().equals(Role.ROLE_STUDENT))
            return profileMapper.profileEntityToStudentDto(student);
        return null;
    }

    @Secured({"ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER"})
    @GetMapping("/students")
    List<StudentInfo> getAllStudents(){
        List<Profile> students = profileService.getAllProfilesByRole( Role.ROLE_STUDENT);
        return students
                .stream()
                .map(profileMapper::profileEntityToStudentDto)
                .collect(Collectors.toList());
    }

    @PermitAll
    @PostMapping ("/students")
    ResponseEntity<Profile> addStudent(@RequestBody RegistrationInfo student){
        try {
            return new ResponseEntity<>(profileService.saveProfile(student), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
