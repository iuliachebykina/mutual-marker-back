package ru.urfu.mutual_marker.service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.common.ProfileMapper;
import ru.urfu.mutual_marker.dto.RegistrationInfo;
import ru.urfu.mutual_marker.exception.UserExistingException;
import ru.urfu.mutual_marker.exception.UserNotExistingException;
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
    public Profile getProfileById(Long id){
        return profileRepository.findById(id).orElse(null);
    }

    @Transactional
    public Profile getProfileByEmail(String email){
        return profileRepository.findByEmail(email);
    }

    @Transactional
    public List<Profile> getAllProfilesByRole(Role role){
        return profileRepository.findAllByRole(role);
    }

    @Transactional
    public Profile saveProfile(RegistrationInfo registrationInfo, Role role) throws UserExistingException {
        Profile profile = profileRepository.findByEmail(registrationInfo.getEmail());
        if (profile != null){
            throw new UserExistingException(String.format("User with email: %s already existing", registrationInfo.getEmail()));
        }
        profile = profileMapper.registrationInfoToProfileEntity(registrationInfo);

        if(!role.equals(Role.ROLE_STUDENT)){
            profile.setStudentGroup(null);

        }

        if(!role.equals(Role.ROLE_TEACHER)){
            profile.setSubject(null);
        }

        profile.setPassword(passwordEncoder.encode(registrationInfo.getPassword()));
        profile.setRole(role);
        profile.setDeleted(false);
        profileRepository.save(profile);
        return profile;
    }

    @Transactional
    public Profile updateProfile(Profile updatedProfile) {
        Optional<Profile> opt = profileRepository.findById(updatedProfile.getId());
        if(opt.isEmpty()){
            log.error("Failed to update profile with id: {}", updatedProfile.getId());
            throw new UserNotExistingException(String.format("User with id: %s does not existing", updatedProfile.getId()));
        }
        Profile oldProfile = opt.get();
        Profile newProfile = profileMapper.updateProfile(updatedProfile, oldProfile);
        log.info("Successful updated profile with id: {}", updatedProfile.getId());
        return newProfile;
    }
}
