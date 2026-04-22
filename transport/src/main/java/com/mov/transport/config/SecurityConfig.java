package com.mov.transport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtAuthFilter jwtAuthFilter(){
        return new JwtAuthFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Allow authentication and registration
                        .requestMatchers("/login", "/register").permitAll()

                        // Allow all frontend static files and resources
                        .requestMatchers("/", "/login.html", "/map.html", "/driver.html", "/admin.html").permitAll()
                        .requestMatchers("/*.js", "/*.css", "/images/**", "/favicon.ico").permitAll()

                        // Role-protected API endpoints
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/driver/**").hasAuthority("DRIVER")
                        .requestMatchers("/student/**").hasAuthority("STUDENT")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}