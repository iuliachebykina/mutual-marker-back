package ru.urfu.mutual_marker.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
public class RegistrationApi {
    ProfileService profileService;

    @PostMapping("/student")
    @PreAuthorize("permitAll()")
    ResponseEntity<Profile> registerStudent(@RequestBody RegistrationInfo student){
        try {
            return new ResponseEntity<>(profileService.saveProfile(student, Role.ROLE_STUDENT), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping ("/admin")
    ResponseEntity<Profile> registerAdmin(@RequestBody RegistrationInfo admin){
        try {
            return new ResponseEntity<>(profileService.saveProfile(admin, Role.ROLE_ADMIN), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping ("/teacher")
    ResponseEntity<Profile> registerTeacher(@RequestBody RegistrationInfo teacher){
        try {
            return new ResponseEntity<>(profileService.saveProfile(teacher, Role.ROLE_TEACHER), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
