package ru.urfu.mutual_marker.service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.common.ProfileMapper;
import ru.urfu.mutual_marker.dto.RegistrationInfo;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;
import ru.urfu.mutual_marker.jpa.repository.ProfileRepository;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProfileService {
    PasswordEncoder passwordEncoder;
    ProfileRepository profileRepository;
    ProfileMapper  profileMapper;

    public Profile getProfileById(Long id){
        return profileRepository.findById(id).orElse(null);
    }

    public Profile getProfileByUsername(String username){
        return profileRepository.findByUsername(username);
    }

    public List<Profile> getAllProfilesByRole(Role role){
        return profileRepository.findAllByRole(role);
    }

    public Profile saveProfile(RegistrationInfo registrationInfo) {
        Profile profile = profileRepository.findByUsername(registrationInfo.getUsername());
        if (profile != null){
            return null;
        }
        profile = profileMapper.registrationInfoToProfileEntity(registrationInfo);

        profile.setPassword(passwordEncoder.encode(registrationInfo.getPassword()));
        profile.setDeleted(false);
        profileRepository.save(profile);
        return profile;
    }

}
