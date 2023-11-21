package com.makhadoni.customer.service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;

@EnableWebFluxSecurity
@Configuration
public class WebSecurityConfig {

    private final ReactiveAuthenticationManager authenticationManager;

    private final SecurityContextRepository contextRepository;

    public WebSecurityConfig(ReactiveAuthenticationManager authenticationManager, SecurityContextRepository contextRepository) {
        this.authenticationManager = authenticationManager;
        this.contextRepository = contextRepository;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authenticationManager(authenticationManager)
                .securityContextRepository(contextRepository)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .authorizeExchange(authorizeExchangeSpec ->  authorizeExchangeSpec
                        .pathMatchers("/api/auth/*","/swagger-ui/**").permitAll()
                        .pathMatchers( "/swagger-doc/**").permitAll()
                        .anyExchange()
                        .authenticated())
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(csrfSpec ->
                        csrfSpec.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()).disable()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
