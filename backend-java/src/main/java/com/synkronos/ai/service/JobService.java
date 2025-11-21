package com.synkronos.ai.service;

import com.synkronos.ai.dto.JobDto;
import com.synkronos.ai.entity.Job;
import com.synkronos.ai.repository.JobRepository;
import com.synkronos.ai.utils.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for job management operations
 */
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    /**
     * Create a new job posting
     */
    @Transactional
    public JobDto createJob(JobDto jobDto, String recruiterId) {
        Job job = Job.builder()
            .recruiterId(recruiterId)
            .title(jobDto.getTitle())
            .description(jobDto.getDescription())
            .companyName(jobDto.getCompanyName())
            .location(jobDto.getLocation())
            .employmentType(jobDto.getEmploymentType())
            .minSalary(jobDto.getMinSalary())
            .maxSalary(jobDto.getMaxSalary())
            .currency(jobDto.getCurrency())
            .requiredSkills(jobDto.getRequiredSkills())
            .minYearsOfExperience(jobDto.getMinYearsOfExperience())
            .educationLevel(jobDto.getEducationLevel())
            .status(Job.JobStatus.ACTIVE)
            .expiresAt(LocalDateTime.now().plusMonths(3))
            .build();

        job = jobRepository.save(job);
        return MapperUtil.mapToJobDto(job);
    }

    /**
     * Get job by ID
     */
    public JobDto getJobById(String id) {
        Job job = jobRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
        return MapperUtil.mapToJobDto(job);
    }

    /**
     * Get all active jobs
     */
    public List<JobDto> getAllActiveJobs() {
        return jobRepository.findByStatus(Job.JobStatus.ACTIVE)
            .stream()
            .map(MapperUtil::mapToJobDto)
            .collect(Collectors.toList());
    }

    /**
     * Search jobs
     */
    public List<JobDto> searchJobs(String searchTerm) {
        return jobRepository.searchActiveJobs(searchTerm)
            .stream()
            .map(MapperUtil::mapToJobDto)
            .collect(Collectors.toList());
    }

    /**
     * Get jobs by recruiter
     */
    public List<JobDto> getJobsByRecruiter(String recruiterId) {
        return jobRepository.findByRecruiterId(recruiterId)
            .stream()
            .map(MapperUtil::mapToJobDto)
            .collect(Collectors.toList());
    }

    /**
     * Update job
     */
    @Transactional
    public JobDto updateJob(String id, JobDto jobDto, String recruiterId) {
        Job job = jobRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));

        if (!job.getRecruiterId().equals(recruiterId)) {
            throw new RuntimeException("Unauthorized to update this job");
        }

        if (jobDto.getTitle() != null) job.setTitle(jobDto.getTitle());
        if (jobDto.getDescription() != null) job.setDescription(jobDto.getDescription());
        if (jobDto.getLocation() != null) job.setLocation(jobDto.getLocation());
        if (jobDto.getEmploymentType() != null) job.setEmploymentType(jobDto.getEmploymentType());
        if (jobDto.getMinSalary() != null) job.setMinSalary(jobDto.getMinSalary());
        if (jobDto.getMaxSalary() != null) job.setMaxSalary(jobDto.getMaxSalary());
        if (jobDto.getRequiredSkills() != null) job.setRequiredSkills(jobDto.getRequiredSkills());
        if (jobDto.getMinYearsOfExperience() != null) job.setMinYearsOfExperience(jobDto.getMinYearsOfExperience());
        if (jobDto.getEducationLevel() != null) job.setEducationLevel(jobDto.getEducationLevel());
        if (jobDto.getStatus() != null) job.setStatus(jobDto.getStatus());

        job = jobRepository.save(job);
        return MapperUtil.mapToJobDto(job);
    }

    /**
     * Delete job
     */
    @Transactional
    public void deleteJob(String id, String recruiterId) {
        Job job = jobRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));

        if (!job.getRecruiterId().equals(recruiterId)) {
            throw new RuntimeException("Unauthorized to delete this job");
        }

        jobRepository.delete(job);
    }
}

