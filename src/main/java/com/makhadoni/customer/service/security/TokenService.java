package com.makhadoni.customer.service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TokenService {

    @Value("${spring.security.token.secret:IS5IobIItq0pRX9JL9TvdQ40Oa93u2Wojlign4V3L30}")
    private  String secret;

    @Value("${spring.security.token.issuer:GK}")
    private String issuer;

    @Value("${spring.security.token.expiresInMinutes:5}")
    private String expiresInMinutes;

    private String crateToken(String username){

        Map<String, Object> roles = new HashMap<>();
        roles.put("roles",List.of("ADMIN"));

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setClaims(roles)
                .setIssuer(issuer)
                .setExpiration(getDate(LocalDateTime.now().plusMinutes(Integer.parseInt(expiresInMinutes))))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Map<String, String> getToken(String username){
        Map<String, String> token = new HashMap<>();
        token.put("token", crateToken(username));
        return token;
    }

    public String getUsernameFromToken(String token){
        return getClaims(token).getSubject();
    }

    public Mono<Boolean> valid(String token){
        try {
            Claims claims = getClaims(token);
            return Mono.just(claims.getExpiration().after(new Date()));
        }catch (ExpiredJwtException e){
            return Mono.just(false);
        }
    }

    public List<String> getRoles(String token){
        return getClaims(token).get("roles", List.class);
    }

    private Claims getClaims(String token){
        return Jwts.parserBuilder().setSigningKey(secret).build().parseClaimsJws(token).getBody();
    }

    private Date getDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


}
