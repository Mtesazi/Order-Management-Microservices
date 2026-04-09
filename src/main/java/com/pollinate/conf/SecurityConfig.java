package com.pollinate.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    @SuppressWarnings("Convert2MethodRef")
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/favicon.ico").permitAll();
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