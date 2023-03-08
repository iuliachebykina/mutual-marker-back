package ru.urfu.mutual_marker.security;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;


@Configuration
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        jsr250Enabled = true)
public class SecurityConfigurerAdapter {
//
//    ProfileDetailsService profileDetailsService;
//    CustomAuthenticationProvider customAuthenticationProvider;
//    ProfileService profileService;
//    HandlerExceptionResolver handlerExceptionResolver;
//    JwtProvider jwtProvider;
    JwtFilter jwtFilter;
//
//    static Gson gson = createProfileGsonBuilder();

//    private static Gson createProfileGsonBuilder(){
//        GsonBuilder profileBuilder = new GsonBuilder();
//        profileBuilder.addSerializationExclusionStrategy(new ExclusionStrategy() {
//
//            @Override
//            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
//
//                return fieldAttributes.getName().equals("deleted")
//                        || fieldAttributes.getName().equals("password")
//                        || fieldAttributes.getName().equals("attachments")
//                        || fieldAttributes.getName().equals("rooms")
//                        || fieldAttributes.getName().equals("numberOfGradedSet");
//            }
//
//            @Override
//            public boolean shouldSkipClass(Class<?> arg0) {
//                return false;
//            }
//        });
//
//        return profileBuilder.create();
//    }
//
//


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(
                        authz -> authz
                                .antMatchers("/api/login", "/api/token").permitAll()
                                .anyRequest().authenticated()
                                .and()
                                .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                )
                .logout()
                .logoutUrl("/api/logout")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK));
        return http.build();
    }




//
//    private AuthenticationSuccessHandler successHandler() {
//        return (httpServletRequest, httpServletResponse, authentication) -> {
//            Profile profile = profileService.getProfileByEmail(authentication.getName());
//            httpServletResponse.setCharacterEncoding("UTF-8");
//            httpServletResponse.getWriter().append(gson.toJson(profile));
//            httpServletResponse.setStatus(200);
//        };
//    }
//
//    private AuthenticationFailureHandler failureHandler() {
//        return (httpServletRequest, httpServletResponse, e) -> {
//            httpServletResponse.getWriter().append("Authentication failure");
//            httpServletResponse.setStatus(401);
//        };
//    }

}
