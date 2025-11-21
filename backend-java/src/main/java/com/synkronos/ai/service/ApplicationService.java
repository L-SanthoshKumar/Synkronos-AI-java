package com.synkronos.ai.service;

import com.synkronos.ai.dto.ApplicationDto;
import com.synkronos.ai.dto.JobDto;
import com.synkronos.ai.dto.MatchScoreRequest;
import com.synkronos.ai.dto.MatchScoreResponse;
import com.synkronos.ai.entity.Application;
import com.synkronos.ai.entity.Job;
import com.synkronos.ai.entity.User;
import com.synkronos.ai.repository.ApplicationRepository;
import com.synkronos.ai.repository.JobRepository;
import com.synkronos.ai.repository.UserRepository;
import com.synkronos.ai.utils.MapperUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for job application management and AI scoring
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${ml.service.url:http://ml-engine:5000}")
    private String mlServiceUrl;

    /**
     * Apply to a job with AI scoring
     */
    @Transactional
    public ApplicationDto applyToJob(String jobId, String jobSeekerId, String coverLetter) {
        // Check if already applied
        if (applicationRepository.findByJobIdAndJobSeekerId(jobId, jobSeekerId).isPresent()) {
            throw new RuntimeException("Already applied to this job");
        }

        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));

        User jobSeeker = userRepository.findById(jobSeekerId)
            .orElseThrow(() -> new RuntimeException("Job seeker not found"));

        // Get AI match score
        Double matchScore = null;
        String matchBreakdown = null;

        try {
            MatchScoreResponse scoreResponse = getMatchScore(jobSeeker, job);
            matchScore = scoreResponse.getOverallScore();
            matchBreakdown = objectMapper.writeValueAsString(scoreResponse.getBreakdown());
        } catch (Exception e) {
            log.error("Error getting match score from ML service: {}", e.getMessage());
            // Continue without score if ML service fails
        }

        Application application = Application.builder()
            .jobId(jobId)
            .jobSeekerId(jobSeekerId)
            .status(Application.ApplicationStatus.PENDING)
            .matchScore(matchScore)
            .matchBreakdown(matchBreakdown)
            .coverLetter(coverLetter)
            .build();

        application = applicationRepository.save(application);
        return MapperUtil.mapToApplicationDto(application);
    }

    /**
     * Get match score from ML service
     */
    private MatchScoreResponse getMatchScore(User jobSeeker, Job job) {
        
        // Build resume text from user profile
        StringBuilder resumeText = new StringBuilder();
        if (jobSeeker.getFirstName() != null) resumeText.append(jobSeeker.getFirstName()).append(" ");
        if (jobSeeker.getLastName() != null) resumeText.append(jobSeeker.getLastName()).append("\n");
        if (jobSeeker.getCurrentPosition() != null) resumeText.append(jobSeeker.getCurrentPosition()).append("\n");
        if (jobSeeker.getBio() != null) resumeText.append(jobSeeker.getBio()).append("\n");
        if (jobSeeker.getYearsOfExperience() != null) {
            resumeText.append(jobSeeker.getYearsOfExperience()).append(" years of experience\n");
        }
        if (jobSeeker.getSkills() != null && !jobSeeker.getSkills().isEmpty()) {
            resumeText.append("Skills: ").append(String.join(", ", jobSeeker.getSkills())).append("\n");
        }
        if (jobSeeker.getResumeUrl() != null) {
            resumeText.append("Resume available at: ").append(jobSeeker.getResumeUrl());
        }
        
        MatchScoreRequest request = MatchScoreRequest.builder()
            .resumeText(resumeText.toString())
            .jobRequiredSkills(job.getRequiredSkills())
            .jobMinYearsOfExperience(job.getMinYearsOfExperience())
            .jobDescription(job.getDescription())
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MatchScoreRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<MatchScoreResponse> response = restTemplate.postForEntity(
                mlServiceUrl + "/predict-score",
                entity,
                MatchScoreResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("ML service error: {}", e.getMessage());
            // Return default score if ML service fails
            return MatchScoreResponse.builder()
                .overallScore(50.0)
                .breakdown(new HashMap<>())
                .build();
        }
    }

    /**
     * Get applications by job seeker
     */
    public List<ApplicationDto> getApplicationsByJobSeeker(String jobSeekerId) {
        List<Application> applications = applicationRepository.findByJobSeekerId(jobSeekerId);
        return applications.stream()
            .map(app -> {
                ApplicationDto dto = MapperUtil.mapToApplicationDto(app);
                Job job = jobRepository.findById(app.getJobId()).orElse(null);
                if (job != null) {
                    dto.setJob(MapperUtil.mapToJobDto(job));
                }
                return dto;
            })
            .collect(Collectors.toList());
    }

    /**
     * Get applications for a job (recruiter view)
     */
    public List<ApplicationDto> getApplicationsByJob(String jobId, String recruiterId) {
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getRecruiterId().equals(recruiterId)) {
            throw new RuntimeException("Unauthorized to view applications for this job");
        }

        List<Application> applications = applicationRepository.findByJobId(jobId);
        return applications.stream()
            .map(app -> {
                ApplicationDto dto = MapperUtil.mapToApplicationDto(app);
                dto.setJob(MapperUtil.mapToJobDto(job));
                User jobSeeker = userRepository.findById(app.getJobSeekerId()).orElse(null);
                if (jobSeeker != null) {
                    dto.setJobSeeker(MapperUtil.mapToUserDto(jobSeeker));
                }
                return dto;
            })
            .collect(Collectors.toList());
    }

    /**
     * Update application status
     */
    @Transactional
    public ApplicationDto updateApplicationStatus(String applicationId, Application.ApplicationStatus status, String recruiterId) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));

        Job job = jobRepository.findById(application.getJobId())
            .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getRecruiterId().equals(recruiterId)) {
            throw new RuntimeException("Unauthorized to update this application");
        }

        application.setStatus(status);
        application = applicationRepository.save(application);

        ApplicationDto dto = MapperUtil.mapToApplicationDto(application);
        dto.setJob(MapperUtil.mapToJobDto(job));
        User jobSeeker = userRepository.findById(application.getJobSeekerId()).orElse(null);
        if (jobSeeker != null) {
            dto.setJobSeeker(MapperUtil.mapToUserDto(jobSeeker));
        }

        return dto;
    }
}

