package ru.urfu.mutual_marker.api;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.urfu.mutual_marker.common.ProfileMapper;
import ru.urfu.mutual_marker.dto.profile.Admin;
import ru.urfu.mutual_marker.dto.profile.Student;
import ru.urfu.mutual_marker.dto.profile.Teacher;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;
import ru.urfu.mutual_marker.jpa.repository.ProfileRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/profile_api")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ProfileApi {
    ProfileRepository profileRepository;
    ProfileMapper profileMapper;

    @GetMapping("/admins/{id}")
    Admin getAdmin(@PathVariable Long id){
        Optional<Profile> admin = profileRepository.findByIdAndRole(id, Role.ADMIN);
        return admin.map(profileMapper::profileEntityToAdminDto).orElse(null);
    }

    @GetMapping("/admins")
    List<Admin> getAllAdmins(){
        List<Profile> admins = profileRepository.findAllByRole( Role.ADMIN);
        return admins
                .stream()
                .map(profileMapper::profileEntityToAdminDto)
                .collect(Collectors.toList());
    }


    @GetMapping("/teachers/{id}")
    Teacher getTeacher(@PathVariable Long id){
        Optional<Profile> teacher = profileRepository.findByIdAndRole(id, Role.TEACHER);
        return teacher.map(profileMapper::profileEntityToTeacherDto).orElse(null);
    }

    @GetMapping("/teachers")
    List<Teacher> getAllTeachers(){
        List<Profile> teachers = profileRepository.findAllByRole( Role.TEACHER);
        return teachers
                .stream()
                .map(profileMapper::profileEntityToTeacherDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/students/{id}")
    Student getStudent(@PathVariable Long id){
        Optional<Profile> student = profileRepository.findByIdAndRole(id, Role.STUDENT);
        return student.map(profileMapper::profileEntityToStudentDto).orElse(null);
    }

    @GetMapping("/students")
    List<Student> getAllStudents(){
        List<Profile> students = profileRepository.findAllByRole( Role.STUDENT);
        return students
                .stream()
                .map(profileMapper::profileEntityToStudentDto)
                .collect(Collectors.toList());
    }





    
    

}
