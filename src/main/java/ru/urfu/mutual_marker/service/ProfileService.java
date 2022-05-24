package ru.urfu.mutual_marker.service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.common.ProfileMapper;
import ru.urfu.mutual_marker.dto.ChangeEmail;
import ru.urfu.mutual_marker.dto.ChangePassword;
import ru.urfu.mutual_marker.dto.RegistrationInfo;
import ru.urfu.mutual_marker.dto.profileInfo.AdminInfo;
import ru.urfu.mutual_marker.dto.profileInfo.StudentInfo;
import ru.urfu.mutual_marker.dto.profileInfo.TeacherInfo;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;
import ru.urfu.mutual_marker.jpa.repository.ProfileRepository;
import ru.urfu.mutual_marker.security.exception.InvalidRoleException;
import ru.urfu.mutual_marker.security.exception.UserExistingException;
import ru.urfu.mutual_marker.security.exception.UserNotExistingException;
import ru.urfu.mutual_marker.security.exception.WrongPasswordException;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class ProfileService {
    PasswordEncoder passwordEncoder;
    ProfileRepository profileRepository;
    ProfileMapper  profileMapper;

    @Transactional
    public Profile getProfileByEmail(String email, Role role){
        Optional<Profile> profile = profileRepository.findByEmail(email);
        profile.ifPresent(value -> checkRole(value.getRole(), role));
        return profile.orElseThrow(()-> {
            throw new UserNotExistingException(String.format("User with email: %s does not existing", email));
        });

    }

    @Transactional
    public Profile getById(Long profileId){
        try {
            return profileRepository.getById(profileId);
        } catch (EntityNotFoundException e){
            log.error("Failed to get reference to profile with id {}", profileId);
            throw new UserNotExistingException(String.format("User wit id %s not found", profileId));
        }
    }

    @Transactional
    public Profile findById(Long profileId){
        Profile profile = profileRepository.findById(profileId).orElse(null);
        if (profile == null){
            throw new UserNotExistingException(String.format("Failed to find user with id %s", profileId));
        }
        return profile;
    }

    public TeacherInfo getTeacher(String email){
        Profile profileByEmail = getProfileByEmail(email);
        return profileMapper.profileEntityToTeacherDto(profileByEmail);
    }

    public StudentInfo getStudent(String email){
        Profile profileByEmail = getProfileByEmail(email);
        return profileMapper.profileEntityToStudentDto(profileByEmail);
    }

    public AdminInfo getAdmin(String email){
        Profile profileByEmail = getProfileByEmail(email);
        return profileMapper.profileEntityToAdminDto(profileByEmail);
    }

    public TeacherInfo getTeacher(Long id){
        Profile profile = getById(id);
        return profileMapper.profileEntityToTeacherDto(profile);
    }

    public StudentInfo getStudent(Long id){
        Profile profile = getById(id);
        return profileMapper.profileEntityToStudentDto(profile);
    }

    public AdminInfo getAdmin(Long id){
        Profile profile = getById(id);
        return profileMapper.profileEntityToAdminDto(profile);
    }

    @Transactional
    public Profile getProfileByEmail(String email){
        return profileRepository.findByEmail(email).orElseThrow(()-> {
            log.error("Not found profile with email: {}", email);
            throw new UserNotExistingException(String.format("Not found profile with email: %s", email));

        });
    }

    @Transactional
    List<Profile> getAllProfilesByRole(Role role, Pageable pageable){
        return profileRepository.findAllByRole(role, pageable);
    }


    public List<TeacherInfo> getAllTeachers(Pageable pageable){
        return getAllProfilesByRole(Role.ROLE_TEACHER, pageable)
                .stream()
                .map(profileMapper::profileEntityToTeacherDto)
                .collect(Collectors.toList());
    }

    public List<StudentInfo> getAllStudents(Pageable pageable){
        return getAllProfilesByRole(Role.ROLE_STUDENT, pageable)
                .stream()
                .map(profileMapper::profileEntityToStudentDto)
                .collect(Collectors.toList());
    }

    public List<AdminInfo> getAllAdmins(Pageable pageable){
        return getAllProfilesByRole(Role.ROLE_ADMIN, pageable)
                .stream()
                .map(profileMapper::profileEntityToAdminDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Profile saveProfile(RegistrationInfo registrationInfo, Role role){
        Optional<Profile> opt = profileRepository.findByEmail(registrationInfo.getEmail());
        if (opt.isPresent()){
            log.error("Failed to register new user. User with email: {} already existing", registrationInfo.getEmail());
            throw new UserExistingException(String.format("User with email: %s already existing", registrationInfo.getEmail()));
        }
        Profile profile = profileMapper.registrationInfoToProfileEntity(registrationInfo);

        if(!role.equals(Role.ROLE_STUDENT)){
            profile.setStudentGroup(null);
        }

        if(!role.equals(Role.ROLE_TEACHER)){
            profile.setSubject(null);
        }

        profile.setPassword(passwordEncoder.encode(registrationInfo.getPassword()));
        profile.setRole(role);
        profileRepository.save(profile);
        return profile;
    }

    @Transactional
    public void deleteProfile(String email){
        Optional<Profile> opt = profileRepository.findByEmail(email);
        if(opt.isEmpty()){
            log.error("Filed to delete profile. Profile with email: {} does not existing", email);
            throw new UserNotExistingException(String.format("User with email: %s does not existing", email));
        }
        Profile profile = opt.get();
        profile.setDeleted(true);
        profileRepository.save(profile);
    }

    private void checkRole(Role actual, Role expected){
        if(!actual.equals(expected)){
            log.error("User with role: {} cannot be updated or gotten in this method", actual);
            throw new InvalidRoleException(String.format("User with role: %s cannot be updated or gotten in this method", actual));
        }
    }

    @Transactional
    public void updatePassword(ChangePassword changePassword){
        Optional<Profile> opt = profileRepository.findByEmail(changePassword.getEmail());
        if(opt.isEmpty()){
            log.error("User with email: {} does not existing", changePassword.getEmail());
            throw new UserNotExistingException(String.format("User with email: %s does not existing", changePassword.getEmail()));
        }
        Profile profile = opt.get();
        if(passwordEncoder.matches(changePassword.getOldPassword(), profile.getPassword())){
            profile.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
            profileRepository.save(profile);
        }
        else {
            log.error("Wrong old password for user with email: {}", changePassword.getEmail());
            throw new WrongPasswordException(String.format("Wrong old password for user with email: %s", changePassword.getEmail()));
        }
    }

    @Transactional
    public void updateEmail(ChangeEmail changeEmail){
        if(profileRepository.getByEmail(changeEmail.getNewEmail()).isPresent()){
            log.error("User with email: {} already existing", changeEmail.getNewEmail());
            throw new UserExistingException(String.format("User with email: %s already existing", changeEmail.getNewEmail()));
        }

        Optional<Profile> opt = profileRepository.findByEmail(changeEmail.getOldEmail());
        if(opt.isEmpty()){
            log.error("User with email: {} does not existing", changeEmail.getOldEmail());
            throw new UserNotExistingException(String.format("User with email: %s does not existing", changeEmail.getOldEmail()));
        }
        Profile profile = opt.get();
        profile.setEmail(changeEmail.getNewEmail());
        profileRepository.save(profile);

    }

    @Transactional
    public Profile updateProfile(Profile updatedProfile) {
        Optional<Profile> opt = profileRepository.findById(updatedProfile.getId());
        if(opt.isEmpty()){
            log.error("User with id: {} does not existing", updatedProfile.getId());
            throw new UserNotExistingException(String.format("User with id: %s does not existing", updatedProfile.getId()));
        }
        Profile oldProfile = opt.get();
        if(updatedProfile.getPassword() != null && !updatedProfile.getPassword().equals(oldProfile.getPassword())){
            log.warn("In this method not allowed update password. Look at the method updatePassword");
            updatedProfile.setPassword(oldProfile.getPassword());
        }
        if(updatedProfile.getEmail() != null && !updatedProfile.getEmail().equals(oldProfile.getEmail())){
            log.warn("In this method not allowed update email. Look at the method updateEmail");
            updatedProfile.setEmail(oldProfile.getEmail());
        }
        Profile newProfile = profileMapper.updateProfile(updatedProfile, oldProfile);
        profileRepository.save(newProfile);
        return newProfile;
    }

    @Transactional
    List<Profile> getProfilesInRoom(Long roomId, Role role, Pageable pageable) {
        return profileRepository.findAllByRoomsIdAndRole(roomId, role, pageable);
    }

    public List<StudentInfo> getStudentsInRoom(Long roomId, Pageable pageable){
        List<Profile> students = getProfilesInRoom(roomId, Role.ROLE_STUDENT, pageable);
        return students.stream()
                .map(profileMapper::profileEntityToStudentDto)
                .collect(Collectors.toList());
    }

    public List<TeacherInfo> getTeachersInRoom(Long roomId, Pageable pageable){
        List<Profile> teachers = getProfilesInRoom(roomId, Role.ROLE_TEACHER, pageable);
        return teachers.stream()
                .map(profileMapper::profileEntityToTeacherDto)
                .collect(Collectors.toList());
    }


}
