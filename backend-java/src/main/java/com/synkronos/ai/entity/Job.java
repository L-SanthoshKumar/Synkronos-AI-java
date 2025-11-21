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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Job posting entity
 */
@Document(collection = "jobs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    private String id;

    @Indexed
    private String recruiterId; // User ID of the recruiter

    private String title;
    private String description;
    private String companyName;
    private String location;
    private String employmentType; // FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP

    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String currency = "USD";

    private Set<String> requiredSkills = new HashSet<>();
    private Integer minYearsOfExperience;
    private String educationLevel; // HIGH_SCHOOL, BACHELORS, MASTERS, PHD

    private JobStatus status = JobStatus.ACTIVE;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime expiresAt;

    public enum JobStatus {
        ACTIVE,
        CLOSED,
        DRAFT
    }
}

