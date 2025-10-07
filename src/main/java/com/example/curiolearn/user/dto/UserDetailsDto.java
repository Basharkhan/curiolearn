package com.example.curiolearn.user.dto;

import lombok.*;

import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDto {
    private String email;
    private String fullName;
    private Set<String> roles; // use role names, not entities
}
