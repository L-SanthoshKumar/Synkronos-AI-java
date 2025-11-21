package com.synkronos.ai.controller;

import com.synkronos.ai.dto.ApplicationDto;
import com.synkronos.ai.entity.Application;
import com.synkronos.ai.service.ApplicationService;
import com.synkronos.ai.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for job application endpoints
 */
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Job application management APIs")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Apply to a job", description = "Job seekers can apply to jobs with AI scoring")
    public ResponseEntity<ApplicationDto> applyToJob(@RequestBody Map<String, String> request,
                                                     Authentication authentication) {
        String jobSeekerId = getUserIdFromAuth(authentication);
        String jobId = request.get("jobId");
        String coverLetter = request.get("coverLetter");
        return ResponseEntity.ok(applicationService.applyToJob(jobId, jobSeekerId, coverLetter));
    }

    @GetMapping("/my-applications")
    @Operation(summary = "Get my applications", description = "Get all applications by current job seeker")
    public ResponseEntity<List<ApplicationDto>> getMyApplications(Authentication authentication) {
        String jobSeekerId = getUserIdFromAuth(authentication);
        return ResponseEntity.ok(applicationService.getApplicationsByJobSeeker(jobSeekerId));
    }

    @GetMapping("/job/{jobId}")
    @Operation(summary = "Get applications for a job", description = "Recruiters can view applications for their jobs")
    public ResponseEntity<List<ApplicationDto>> getApplicationsByJob(@PathVariable String jobId,
                                                                     Authentication authentication) {
        String recruiterId = getUserIdFromAuth(authentication);
        return ResponseEntity.ok(applicationService.getApplicationsByJob(jobId, recruiterId));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update application status", description = "Recruiters can update application status")
    public ResponseEntity<ApplicationDto> updateApplicationStatus(@PathVariable String id,
                                                                  @RequestBody Map<String, String> request,
                                                                  Authentication authentication) {
        String recruiterId = getUserIdFromAuth(authentication);
        Application.ApplicationStatus status = Application.ApplicationStatus.valueOf(request.get("status"));
        return ResponseEntity.ok(applicationService.updateApplicationStatus(id, status, recruiterId));
    }

    private String getUserIdFromAuth(Authentication authentication) {
        String email = authentication.getName();
        return userService.getUserByEmail(email).getId();
    }
}

