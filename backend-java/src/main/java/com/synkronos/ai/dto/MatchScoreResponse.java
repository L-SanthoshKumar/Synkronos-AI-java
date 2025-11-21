package com.synkronos.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchScoreResponse {

    private Double overallScore;                         // 0-100
    private Map<String, Double> skillMatchScores;        // Individual skill match percentages
    private Map<String, Double> breakdown;               // Detailed breakdown (experience, skills, etc.)
    private Set<String> extractedSkills;                 // Skills extracted from resume
}
