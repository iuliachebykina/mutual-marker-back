package ru.urfu.mutual_marker.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;
import ru.urfu.mutual_marker.security.exception.InvalidRoleException;
import ru.urfu.mutual_marker.service.ProfileService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProfileDetailsService implements UserDetailsService {
    ProfileService profileService;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String[] emailAndRole = StringUtils.split(username, "\\");
        String role = emailAndRole[0];
        String email = Arrays.toString(Arrays.stream(emailAndRole).skip(1).toArray());
        Profile user = profileService.getProfileByEmail(emailAndRole[1]);
        if (user == null) {
            throw new UsernameNotFoundException("No user found with email: " + email);
        }
        if(!user.getRole().toString().equals(role)){
            throw new InvalidRoleException("Invalid role");
        }
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), enabled, accountNonExpired,
                credentialsNonExpired, accountNonLocked, getAuthorities(user.getRole()));
    }

    private static List<GrantedAuthority> getAuthorities (Role role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.name()));
        return authorities;
    }
}
