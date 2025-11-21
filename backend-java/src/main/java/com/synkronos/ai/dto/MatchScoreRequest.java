package com.synkronos.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO for ML service match score request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchScoreRequest {

    private String resumeText;
    private Set<String> jobRequiredSkills;
    private Integer jobMinYearsOfExperience;
    private String jobDescription;
}

