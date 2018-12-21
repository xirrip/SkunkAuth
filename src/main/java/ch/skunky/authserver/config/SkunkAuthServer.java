package ch.skunky.authserver.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * for user service extensions see:
 * https://github.com/Baeldung/spring-security-registration/tree/master/
 * seems very complete
 */

@SpringBootApplication
@EntityScan(basePackages = { "ch.skunky.authserver.config", "ch.skunky.authserver.model"})
@ComponentScan(basePackages = { "ch.skunky.authserver.config", "ch.skunky.authserver.controller", "ch.skunky.authserver.service"})
@EnableJpaRepositories("ch.skunky.authserver.repository")
public class SkunkAuthServer extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SkunkAuthServer.class, args);
    }

}