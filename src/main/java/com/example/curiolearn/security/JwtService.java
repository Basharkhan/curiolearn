package com.example.curiolearn.security;

import com.example.curiolearn.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final KeyPair keyPair;

    public String generateToken(User user) {
        List<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getName().name())
                .toList();

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS512)
                .compact();
    }

    public String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public String extractUserEmail(String token) {
        return getClaims(token).getSubject();
    }

    public List<String> extractRoles(String token) {
        Object rolesObj = getClaims(token).get("roles");
        if (rolesObj instanceof List<?> rolesList) {
            return rolesList.stream().map(Object::toString).toList();
        }
        return List.of();
    }

    public boolean validateToken(String token) {
        try {
            return !getClaims(token).getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Updated to handle multiple roles
    public Authentication getAuthentication(String token) {
        String email = extractUserEmail(token);
        List<String> roles = extractRoles(token);

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();

        return new UsernamePasswordAuthenticationToken(email, null, authorities);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(keyPair.getPublic())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
