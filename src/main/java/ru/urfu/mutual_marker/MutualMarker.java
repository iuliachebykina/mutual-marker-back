package ru.urfu.mutual_marker;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@OpenAPIDefinition(servers = {@Server(url = "http://localhost:8080", description = "Default Server URL")})

@SpringBootApplication
@EnableJpaAuditing
public class MutualMarker {

    public static void main(String[] args) {
        SpringApplication.run(MutualMarker.class, args);
    }

}
