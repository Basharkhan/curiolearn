package com.example.curiolearn.auth.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    String token;
    UserDetailsDto userDetailsDto;
}
