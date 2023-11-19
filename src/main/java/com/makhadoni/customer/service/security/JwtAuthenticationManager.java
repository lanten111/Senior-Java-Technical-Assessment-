package com.makhadoni.customer.service.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.security.sasl.AuthenticationException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private TokenService tokenService;

    public JwtAuthenticationManager(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        return tokenService.valid(authToken)
            .filter(valid -> valid)
                .switchIfEmpty(Mono.empty())
            .map(valid -> {
                List<String> role = tokenService.getRoles(authToken);
                return new UsernamePasswordAuthenticationToken(
                        tokenService.getUsernameFromToken(authToken), null, role.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                );
            });
    }
}