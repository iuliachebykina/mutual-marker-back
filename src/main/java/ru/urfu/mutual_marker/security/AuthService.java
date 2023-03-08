package ru.urfu.mutual_marker.security;

import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.repository.ProfileRepository;
import ru.urfu.mutual_marker.security.exception.InvalidRoleException;

import javax.security.auth.message.AuthException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;
    private final Map<String, String> refreshStorage = new HashMap<>();
    private final JwtProvider jwtProvider;

    @SneakyThrows
    public JwtResponse login(@NonNull JwtRequest authRequest) {
        final Profile user = profileRepository.findByEmail(authRequest.getLogin())
                .orElseThrow(() -> new AuthException("Пользователь не найден"));
        if(!user.getRole().toString().equals(authRequest.getRole())){
            throw new InvalidRoleException(String.format("Invalid role\nactual: %s, but expected: %s", user.getRole(), authRequest.getRole()));
        }
        if (passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String refreshToken = jwtProvider.generateRefreshToken(user);
            refreshStorage.put(user.getEmail(), refreshToken);
            return new JwtResponse(accessToken, refreshToken);
        } else {
            throw new AuthException("Неправильный логин или пароль");
        }
    }

    @SneakyThrows
    public JwtResponse getAccessToken(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final Profile user = profileRepository.findByEmail(login)
                        .orElseThrow(() -> new AuthException("Пользователь не найден"));
                final String accessToken = jwtProvider.generateAccessToken(user);
                return new JwtResponse(accessToken, null);
            }
        }
        return new JwtResponse(null, null);
    }

    @SneakyThrows
    public JwtResponse refresh(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final Profile user = profileRepository.findByEmail(login)
                        .orElseThrow(() -> new AuthException("Пользователь не найден"));
                final String accessToken = jwtProvider.generateAccessToken(user);
                final String newRefreshToken = jwtProvider.generateRefreshToken(user);
                refreshStorage.put(user.getEmail(), newRefreshToken);
                return new JwtResponse(accessToken, newRefreshToken);
            }
        }
        throw new AuthException("Невалидный JWT токен");
    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

}