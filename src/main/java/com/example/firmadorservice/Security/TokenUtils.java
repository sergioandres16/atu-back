package com.example.firmadorservice.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenUtils {
    private final static String ACCESS_TOKEN_SECRET = "KLLOdeV4Gs0TKo1Mf8OKdz0ZM1234567"; //tiene  que ser de tamaño mayor a 32
    private final static Long ACCESS_TOKEN_VALIDITY_SECONDS = 2_592_00L; //30 dias

    public static String createToken(String name) {
        long expirationTime = ACCESS_TOKEN_VALIDITY_SECONDS * 1_000;
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

        Map<String, Object> extra = new HashMap<>();
        extra.put("name",name);
        return Jwts.builder()
                .setSubject(name)
                .setExpiration(expirationDate)
                .addClaims(extra)
                .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes()))
                .compact();
    }

    public static String usernameUsuario(String token){
        String username;
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(ACCESS_TOKEN_SECRET.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            username= String.valueOf(claims.get("name"));
        } catch (JwtException e) {
            return "";
        }
        return username;
    }

    public static UsernamePasswordAuthenticationToken getAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(ACCESS_TOKEN_SECRET.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String name = claims.getSubject();

            return new UsernamePasswordAuthenticationToken(name, null, Collections.emptyList());
        } catch (JwtException e) {
            return null;
        }
    }

}
