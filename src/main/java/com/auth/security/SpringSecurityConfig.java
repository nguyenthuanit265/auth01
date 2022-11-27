package com.auth.security;//package com.auth.security;

import com.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Slf4j
public class SpringSecurityConfig {

    private final UserService userService;

    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Inject bean by constructor
     */
    public SpringSecurityConfig(UserService userService, JwtAuthFilter jwtAuthFilter) {

        this.userService = userService;
        this.jwtAuthFilter = jwtAuthFilter;
    }


    /**
     * Config security filter chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Enable CORS
        http.cors();

        // Config  CSRF, XSS, Click jacking and so on
        http.csrf().disable();

        // Config SessionManagement
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Config url authentication
        http.antMatcher("/api/**").authorizeHttpRequests()
                .antMatchers("/api/**").permitAll();


        // Config Exception handler
        http.exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        // Add filter
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // Disable page login default
        http.httpBasic().disable();

        return http.build();
    }

    /**
     * Config webSecurity -> Should config resource because if used for URL matching
     * -> are also ignored and no security context will be set and can not protect endpoints for Cross-Site Scripting, XSS attacks, content-sniffing.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .antMatchers("/ignore-h2/**", "/ignore-h2/h2-console");
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> (UserDetails) userService.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User %s not found", username)));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

}
