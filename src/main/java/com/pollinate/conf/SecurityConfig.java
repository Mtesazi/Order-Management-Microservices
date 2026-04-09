package com.pollinate.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

/**
 * Configures HTTP security and authentication for the application.
 * - All /api/** endpoints require HTTP Basic authentication
 * - H2 console at /h2-console/** is exempt from authentication
 * - CSRF is disabled to support simple API use cases
 * - Frameoptions allow same-origin frames for H2 console UI
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .authorizeHttpRequests(auth -> {
                    // Allow H2 console access without authentication
                    auth.requestMatchers(
                            RegexRequestMatcher.regexMatcher("^/h2-console(/.*)?$")
                    ).permitAll();
                    auth.anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}