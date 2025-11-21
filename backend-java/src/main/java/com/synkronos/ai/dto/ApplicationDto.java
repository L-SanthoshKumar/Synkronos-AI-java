package com.synkronos.ai.dto;

import com.synkronos.ai.entity.Application;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for job applications
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDto {

    private String id;
    private String jobId;
    private String jobSeekerId;
    private Application.ApplicationStatus status;
    private Double matchScore;
    private String matchBreakdown;
    private String coverLetter;
    private LocalDateTime appliedAt;
    private JobDto job; // Populated when fetching applications
    private UserDto jobSeeker; // Populated for recruiters
}

