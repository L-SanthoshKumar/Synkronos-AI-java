package com.synkronos.ai.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * User entity representing both job seekers and recruiters
 */
@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String firstName;
    private String lastName;

    @Indexed
    private UserRole role; // JOB_SEEKER or RECRUITER

    private String phone;
    private String location;
    private String bio;

    // Job Seeker specific fields
    private String resumeUrl;
    private Set<String> skills = new HashSet<>();
    private String currentPosition;
    private Integer yearsOfExperience;

    // Recruiter specific fields
    private String companyName;
    private String companyWebsite;

    private Boolean isActive = true;
    private Boolean isEmailVerified = false;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum UserRole {
        JOB_SEEKER,
        RECRUITER,
        ADMIN
    }
}

