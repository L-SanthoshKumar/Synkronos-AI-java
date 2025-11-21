package com.synkronos.ai.dto;

import com.synkronos.ai.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for user information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private User.UserRole role;
    private String phone;
    private String location;
    private String bio;
    private String resumeUrl;
    private Set<String> skills;
    private String currentPosition;
    private Integer yearsOfExperience;
    private String companyName;
    private String companyWebsite;
    private LocalDateTime createdAt;
}

