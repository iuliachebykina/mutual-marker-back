package ru.urfu.mutual_marker.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.urfu.mutual_marker.dto.RegistrationInfo;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;
import ru.urfu.mutual_marker.service.ProfileService;

@RestController
@RequestMapping(value = "/registration")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class RegistrationApi {
    ProfileService profileService;

    @PostMapping("/student")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Profile> registerStudent(@RequestBody RegistrationInfo registrationInfo){
        try {
            Profile student = profileService.saveProfile(registrationInfo, Role.ROLE_STUDENT);
            log.info("Registration student (id: {})", student.getId());
            return new ResponseEntity<>(student, HttpStatus.CREATED);
        } catch (Exception e) {
            log.info("Failed to registration student with username: {}", registrationInfo.getUsername());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping ("/admin")
    public ResponseEntity<Profile> registerAdmin(@RequestBody RegistrationInfo registrationInfo){
        try {
            Profile admin = profileService.saveProfile(registrationInfo, Role.ROLE_ADMIN);
            log.info("Registration admin (id: {})", admin.getId());
            return new ResponseEntity<>(admin, HttpStatus.CREATED);
        } catch (Exception e) {
            log.info("Failed to registration admin with username: {}", registrationInfo.getUsername());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping ("/teacher")
    public ResponseEntity<Profile> registerTeacher(@RequestBody RegistrationInfo registrationInfo){
        try {
            Profile teacher = profileService.saveProfile(registrationInfo, Role.ROLE_TEACHER);
            log.info("Registration teacher (id: {})", teacher.getId());
            return new ResponseEntity<>(teacher, HttpStatus.CREATED);
        } catch (Exception e) {
            log.info("Failed to registration teacher with username: {}", registrationInfo.getUsername());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}