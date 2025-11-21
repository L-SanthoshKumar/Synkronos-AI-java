package com.synkronos.ai.utils;

import com.synkronos.ai.dto.ApplicationDto;
import com.synkronos.ai.dto.JobDto;
import com.synkronos.ai.dto.UserDto;
import com.synkronos.ai.entity.Application;
import com.synkronos.ai.entity.Job;
import com.synkronos.ai.entity.User;

/**
 * Utility class for mapping entities to DTOs
 */
public class MapperUtil {

    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getRole())
            .phone(user.getPhone())
            .location(user.getLocation())
            .bio(user.getBio())
            .resumeUrl(user.getResumeUrl())
            .skills(user.getSkills())
            .currentPosition(user.getCurrentPosition())
            .yearsOfExperience(user.getYearsOfExperience())
            .companyName(user.getCompanyName())
            .companyWebsite(user.getCompanyWebsite())
            .createdAt(user.getCreatedAt())
            .build();
    }

    public static JobDto mapToJobDto(Job job) {
        return JobDto.builder()
            .id(job.getId())
            .recruiterId(job.getRecruiterId())
            .title(job.getTitle())
            .description(job.getDescription())
            .companyName(job.getCompanyName())
            .location(job.getLocation())
            .employmentType(job.getEmploymentType())
            .minSalary(job.getMinSalary())
            .maxSalary(job.getMaxSalary())
            .currency(job.getCurrency())
            .requiredSkills(job.getRequiredSkills())
            .minYearsOfExperience(job.getMinYearsOfExperience())
            .educationLevel(job.getEducationLevel())
            .status(job.getStatus())
            .createdAt(job.getCreatedAt())
            .expiresAt(job.getExpiresAt())
            .build();
    }

    public static ApplicationDto mapToApplicationDto(Application application) {
        return ApplicationDto.builder()
            .id(application.getId())
            .jobId(application.getJobId())
            .jobSeekerId(application.getJobSeekerId())
            .status(application.getStatus())
            .matchScore(application.getMatchScore())
            .matchBreakdown(application.getMatchBreakdown())
            .coverLetter(application.getCoverLetter())
            .appliedAt(application.getAppliedAt())
            .build();
    }
}

