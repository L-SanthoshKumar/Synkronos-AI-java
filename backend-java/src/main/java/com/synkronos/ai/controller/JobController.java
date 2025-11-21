package com.synkronos.ai.controller;

import com.synkronos.ai.dto.JobDto;
import com.synkronos.ai.service.JobService;
import com.synkronos.ai.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for job management endpoints
 */
@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "Job posting and management APIs")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*")
public class JobController {

    private final JobService jobService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new job posting", description = "Recruiters can create job postings")
    public ResponseEntity<JobDto> createJob(@RequestBody JobDto jobDto, Authentication authentication) {
        String recruiterId = getUserIdFromAuth(authentication);
        return ResponseEntity.ok(jobService.createJob(jobDto, recruiterId));
    }

    @GetMapping
    @Operation(summary = "Get all active jobs", description = "Get list of all active job postings")
    public ResponseEntity<List<JobDto>> getAllActiveJobs() {
        return ResponseEntity.ok(jobService.getAllActiveJobs());
    }

    @GetMapping("/search")
    @Operation(summary = "Search jobs", description = "Search jobs by keyword")
    public ResponseEntity<List<JobDto>> searchJobs(@RequestParam String q) {
        return ResponseEntity.ok(jobService.searchJobs(q));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job by ID")
    public ResponseEntity<JobDto> getJobById(@PathVariable String id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @GetMapping("/recruiter/my-jobs")
    @Operation(summary = "Get jobs by current recruiter")
    public ResponseEntity<List<JobDto>> getMyJobs(Authentication authentication) {
        String recruiterId = getUserIdFromAuth(authentication);
        return ResponseEntity.ok(jobService.getJobsByRecruiter(recruiterId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update job posting")
    public ResponseEntity<JobDto> updateJob(@PathVariable String id, @RequestBody JobDto jobDto, 
                                           Authentication authentication) {
        String recruiterId = getUserIdFromAuth(authentication);
        return ResponseEntity.ok(jobService.updateJob(id, jobDto, recruiterId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete job posting")
    public ResponseEntity<Void> deleteJob(@PathVariable String id, Authentication authentication) {
        String recruiterId = getUserIdFromAuth(authentication);
        jobService.deleteJob(id, recruiterId);
        return ResponseEntity.noContent().build();
    }

    private String getUserIdFromAuth(Authentication authentication) {
        String email = authentication.getName();
        return userService.getUserByEmail(email).getId();
    }
}

