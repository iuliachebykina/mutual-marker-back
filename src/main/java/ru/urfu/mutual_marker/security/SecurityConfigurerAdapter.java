package ru.urfu.mutual_marker.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;


@Configuration
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        jsr250Enabled = true)
public class SecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    ProfileDetailsService profileDetailsService;
    PasswordEncoder passwordEncoder;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(profileDetailsService)
                .and()
                .inMemoryAuthentication()
                .withUser("admin").password(passwordEncoder.encode("admin"))
                .authorities(Role.ROLE_ADMIN.name())
                .and()
                .withUser("teacher").password(passwordEncoder.encode("teacher"))
                .authorities(Role.ROLE_TEACHER.name())
                .and()
                .withUser("student").password(passwordEncoder.encode("student"))
                .authorities(Role.ROLE_STUDENT.name());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http
                .csrf().disable()
                .httpBasic()
                .and().sessionManagement().disable()
                .formLogin()
                .loginProcessingUrl("/api/login")
                .successHandler(successHandler())
                .failureHandler(failureHandler())
                .and()
                .logout()
                .logoutUrl("/api/logout")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK));
    }

    private AuthenticationSuccessHandler successHandler() {
        return (httpServletRequest, httpServletResponse, authentication) -> {
            httpServletResponse.getWriter().append("OK");
            httpServletResponse.setStatus(200);
        };
    }

    private AuthenticationFailureHandler failureHandler() {
        return (httpServletRequest, httpServletResponse, e) -> {
            httpServletResponse.getWriter().append("Authentication failure");
            httpServletResponse.setStatus(401);
        };
    }

}
