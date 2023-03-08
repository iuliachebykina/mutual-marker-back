package ru.urfu.mutual_marker.security.jwt.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByLogin(String login);
}
