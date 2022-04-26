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
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;
import ru.urfu.mutual_marker.jpa.repository.ProfileRepository;

import javax.transaction.Transactional;
import java.util.List;

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
    public Profile getProfileByUsername(String username){
        return profileRepository.findByUsername(username);
    }

    @Transactional
    public List<Profile> getAllProfilesByRole(Role role){
        return profileRepository.findAllByRole(role);
    }

    @Transactional
    public Profile saveProfile(RegistrationInfo registrationInfo, Role role) throws UserExistingException {
        Profile profile = profileRepository.findByUsername(registrationInfo.getUsername());
        if (profile != null){
            throw new UserExistingException(String.format("User with username: %s already existing", registrationInfo.getUsername()));
        }
        profile = profileMapper.registrationInfoToProfileEntity(registrationInfo);

        if(!role.equals(Role.ROLE_STUDENT)){
            profile.setStudentGroup(null);
        }
        profile.setPassword(passwordEncoder.encode(registrationInfo.getPassword()));
        profile.setRole(role);
        profile.setDeleted(false);
        profileRepository.save(profile);
        return profile;
    }

    @Transactional
    public Profile updateProfile(Profile profile) {
        return profileRepository.save(profile);
    }
}
