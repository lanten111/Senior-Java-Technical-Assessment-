package com.makhadoni.customer.service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;

@EnableWebFluxSecurity
@Configuration
public class WebSecurityConfig {

    private ReactiveAuthenticationManager authenticationManager;

    private SecurityContextRepository contextRepository;

    public WebSecurityConfig(ReactiveAuthenticationManager authenticationManager, SecurityContextRepository contextRepository) {
        this.authenticationManager = authenticationManager;
        this.contextRepository = contextRepository;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authenticationManager(authenticationManager)
                .securityContextRepository(contextRepository)
                .cors(corsSpec -> corsSpec.disable())
                .authorizeExchange(authorizeExchangeSpec ->  authorizeExchangeSpec
                        .pathMatchers("api/login", "api/signup").permitAll()
                        .anyExchange()
                        .authenticated())
                .formLogin(formLoginSpec -> formLoginSpec.disable())
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
