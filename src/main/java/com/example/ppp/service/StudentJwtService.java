package com.example.ppp.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;

import java.security.Key;
import java.util.Date;

@Log4j2
public   class StudentJwtService {
    private  final String  secret = "MHmKoHeFr!Qu^sB*(&ScF#tPl)6_pt6*$7%!";
    private final Key key = Keys.hmacShaKeyFor(secret.getBytes());

    public String createToken(String subject) {
        String jws = Jwts.builder()
                .setSubject(subject)
                .setExpiration(new Date((new Date()).getTime() + 3600000)) // 1시간 후 만료
                .signWith(key)
                .compact();
        return jws;
    }

    public Jws<Claims> verifyToken(String token) throws JwtException {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        return jws;
    }

    public long getRemainingSeconds(String token) throws JwtException {
        Jws<Claims> jws = verifyToken(token);
        long difference = jws.getBody().getExpiration().getTime() - System.currentTimeMillis();
        return difference / 1000; // 초 단위로 변환
    }
}