package com.synkronos.ai.dto;

import com.synkronos.ai.entity.Job;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for job postings
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDto {

    private String id;
    private String recruiterId;
    private String title;
    private String description;
    private String companyName;
    private String location;
    private String employmentType;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String currency;
    private Set<String> requiredSkills;
    private Integer minYearsOfExperience;
    private String educationLevel;
    private Job.JobStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}

