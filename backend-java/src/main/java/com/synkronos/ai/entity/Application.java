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

/**
 * Job application entity linking job seekers to jobs
 */
@Document(collection = "applications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id
    private String id;

    @Indexed
    private String jobId;

    @Indexed
    private String jobSeekerId;

    private ApplicationStatus status = ApplicationStatus.PENDING;

    private Double matchScore; // AI-generated match score (0-100)
    private String matchBreakdown; // JSON string with score breakdown

    private String coverLetter;

    @CreatedDate
    private LocalDateTime appliedAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum ApplicationStatus {
        PENDING,
        REVIEWING,
        SHORTLISTED,
        INTERVIEW_SCHEDULED,
        REJECTED,
        ACCEPTED
    }
}

