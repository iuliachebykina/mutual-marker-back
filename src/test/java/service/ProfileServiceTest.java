package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.urfu.mutual_marker.common.ProfileMapper;
import ru.urfu.mutual_marker.dto.profile.ChangePassword;
import ru.urfu.mutual_marker.dto.registration.RegistrationInfo;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;
import ru.urfu.mutual_marker.jpa.repository.ProfileRepository;
import ru.urfu.mutual_marker.security.exception.UserExistingException;
import ru.urfu.mutual_marker.security.exception.UserNotExistingException;
import ru.urfu.mutual_marker.security.exception.WrongPasswordException;
import ru.urfu.mutual_marker.service.profile.ProfileService;

import java.util.Optional;

@ContextConfiguration(classes = {
        ProfileService.class
})
@ExtendWith({MockitoExtension.class, SpringExtension.class})
public class ProfileServiceTest {
    @Autowired
    ProfileService profileService;

    @MockBean
    PasswordEncoder passwordEncoder;
    @MockBean
    ProfileRepository profileRepository;
    @MockBean
    ProfileMapper profileMapper;

    @Test
    public void when_profile_with_email_is_present_then_saveProfile_throws_exception(){
        Mockito.when(profileRepository.findByEmailAndDeletedIsFalse(Mockito.anyString())).thenReturn(Optional.of(Profile.builder().build()));
        RegistrationInfo registrationInfo = RegistrationInfo.builder().email("Test@mail.ru").build();

        Assertions.assertThrows(UserExistingException.class, () -> profileService.saveProfile(registrationInfo, Role.ROLE_STUDENT));
    }

    @Test
    public void when_profile_with_email_is_not_present_then_updatePassword_throws_exception(){
        Mockito.when(profileRepository.findByEmailAndDeletedIsFalse(Mockito.anyString())).thenReturn(Optional.empty());
        ChangePassword changePassword = new ChangePassword();
        changePassword.setEmail("Test");

        Assertions.assertThrows(UserNotExistingException.class, () -> profileService.updatePassword(changePassword));
    }

    @Test
    public void when_passwords_do_not_match_then_updatePassword_throws_exception(){
        Profile profile = Profile.builder().id(1L).password("Test").build();
        Mockito.when(profileRepository.findByEmailAndDeletedIsFalse(Mockito.anyString())).thenReturn(Optional.of(profile));
        ChangePassword changePassword = new ChangePassword();
        changePassword.setEmail("Test");
        changePassword.setNewPassword("TestNew1");
        changePassword.setOldPassword("Test123");

        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(Boolean.FALSE);

        Assertions.assertThrows(WrongPasswordException.class, () -> profileService.updatePassword(changePassword));
    }
}
