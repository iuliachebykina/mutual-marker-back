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
import ru.urfu.mutual_marker.exception.InvalidRoleException;
import ru.urfu.mutual_marker.exception.UserExistingException;
import ru.urfu.mutual_marker.exception.UserNotExistingException;
import ru.urfu.mutual_marker.exception.WrongPasswordException;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;
import ru.urfu.mutual_marker.jpa.repository.ProfileRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

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
        return profile.orElse(null);
    }

    @Transactional
    public Profile getProfileByEmail(String email){
        return profileRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public List<Profile> getAllProfilesByRole(Role role, Pageable pageable){
        return profileRepository.findAllByRole(role, pageable);
    }

    @Transactional
    public Profile saveProfile(RegistrationInfo registrationInfo, Role role){
        Optional<Profile> opt = profileRepository.findByEmail(registrationInfo.getEmail());
        if (opt.isPresent()){
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
    public void deleteProfile(String email, Role role){
        Optional<Profile> opt = profileRepository.findByEmail(email);
        if(opt.isEmpty()){
            throw new UserNotExistingException(String.format("User with email: %s does not existing", email));
        }
        Profile profile = opt.get();
        checkRole(profile.getRole(), role);
        profile.setDeleted(true);
        profileRepository.save(profile);
    }

    private void checkRole(Role actual, Role expected){
        if(!actual.equals(expected)){
            throw new InvalidRoleException(String.format("User with role: %s cannot be updated or gotten in this method", actual));
        }
    }

    @Transactional
    public void updatePassword(ChangePassword changePassword){
        Optional<Profile> opt = profileRepository.findByEmail(changePassword.getEmail());
        if(opt.isEmpty()){
            throw new UserNotExistingException(String.format("User with email: %s does not existing", changePassword.getEmail()));
        }
        Profile profile = opt.get();
        if(passwordEncoder.matches(changePassword.getOldPassword(), profile.getPassword())){
            profile.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
            profileRepository.save(profile);
        }
        else {
            throw new WrongPasswordException(String.format("Wrong old password for user with email: %s", changePassword.getEmail()));
        }
    }

    @Transactional
    public void updateEmail(ChangeEmail changeEmail){
        if(profileRepository.getByEmail(changeEmail.getNewEmail()).isPresent()){
            throw new UserExistingException(String.format("User with email: %s already existing", changeEmail.getNewEmail()));
        }

        Optional<Profile> opt = profileRepository.findByEmail(changeEmail.getOldEmail());
        if(opt.isEmpty()){
            throw new UserNotExistingException(String.format("User with email: %s does not existing", changeEmail.getOldEmail()));
        }
        Profile profile = opt.get();
        profile.setEmail(changeEmail.getNewEmail());
        profileRepository.save(profile);

    }

    @Transactional
    public Profile updateProfile(Profile updatedProfile, Role role) {
        Optional<Profile> opt = profileRepository.findById(updatedProfile.getId());
        if(opt.isEmpty()){
            throw new UserNotExistingException(String.format("User with id: %s does not existing", updatedProfile.getId()));
        }
        Profile oldProfile = opt.get();
        checkRole(oldProfile.getRole(), role);
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
    public List<Profile> getProfilesInRoom(Long roomId, Role role, Pageable pageable) {
        return profileRepository.findAllByRoomsIdAndRole(roomId, role, pageable);
    }
}
