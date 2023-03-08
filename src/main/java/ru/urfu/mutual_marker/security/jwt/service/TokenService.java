package ru.urfu.mutual_marker.security.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.urfu.mutual_marker.security.jwt.jpa.Token;
import ru.urfu.mutual_marker.security.jwt.jpa.TokenRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;


    public void put(String login, String refreshToken) {
        Optional<Token> tokenOpt = tokenRepository.findByLogin(login);
        Token token;
        if (tokenOpt.isPresent()) {
            token = tokenOpt.get();
            token.setToken(refreshToken);
        } else {
            token = new Token();
            token.setToken(refreshToken);
            token.setLogin(login);

        }
        tokenRepository.save(token);

    }

    public String get(String login) {
        Optional<Token> token = tokenRepository.findByLogin(login);
        if (token.isEmpty())
            return null;
        return token.get().getToken();
    }


}