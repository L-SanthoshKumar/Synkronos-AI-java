package com.synkronos.ai.config;

import com.synkronos.ai.entity.Job;
import com.synkronos.ai.entity.User;
import com.synkronos.ai.repository.JobRepository;
import com.synkronos.ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Seed data for demo users and jobs
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            seedUsers();
            seedJobs();
            log.info("Seed data created successfully");
        }
    }

    private void seedUsers() {
        // Demo Job Seeker
        Set<String> skills1 = new HashSet<>();
        skills1.add("Java");
        skills1.add("Spring Boot");
        skills1.add("React");
        skills1.add("MongoDB");

        User jobSeeker = User.builder()
            .email("jobseeker@demo.com")
            .password(passwordEncoder.encode("password123"))
            .firstName("John")
            .lastName("Doe")
            .role(User.UserRole.JOB_SEEKER)
            .phone("+1234567890")
            .location("San Francisco, CA")
            .bio("Experienced full-stack developer with 5+ years of experience")
            .skills(skills1)
            .currentPosition("Senior Software Engineer")
            .yearsOfExperience(5)
            .isActive(true)
            .isEmailVerified(true)
            .createdAt(LocalDateTime.now())
            .build();

        userRepository.save(jobSeeker);

        // Demo Recruiter
        User recruiter = User.builder()
            .email("recruiter@demo.com")
            .password(passwordEncoder.encode("password123"))
            .firstName("Jane")
            .lastName("Smith")
            .role(User.UserRole.RECRUITER)
            .phone("+1234567891")
            .location("New York, NY")
            .companyName("TechCorp Inc")
            .companyWebsite("https://techcorp.com")
            .isActive(true)
            .isEmailVerified(true)
            .createdAt(LocalDateTime.now())
            .build();

        userRepository.save(recruiter);

        log.info("Demo users created: jobseeker@demo.com / recruiter@demo.com (password: password123)");
    }

    private void seedJobs() {
        User recruiter = userRepository.findByEmail("recruiter@demo.com").orElse(null);
        if (recruiter == null) return;

        Set<String> skills1 = new HashSet<>();
        skills1.add("Java");
        skills1.add("Spring Boot");
        skills1.add("Microservices");
        skills1.add("AWS");

        Job job1 = Job.builder()
            .recruiterId(recruiter.getId())
            .title("Senior Backend Developer")
            .description("We are looking for an experienced backend developer to join our team. " +
                "You will work on building scalable microservices using Java and Spring Boot.")
            .companyName("TechCorp Inc")
            .location("San Francisco, CA")
            .employmentType("FULL_TIME")
            .minSalary(new BigDecimal("120000"))
            .maxSalary(new BigDecimal("180000"))
            .currency("USD")
            .requiredSkills(skills1)
            .minYearsOfExperience(5)
            .educationLevel("BACHELORS")
            .status(Job.JobStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusMonths(3))
            .build();

        jobRepository.save(job1);

        Set<String> skills2 = new HashSet<>();
        skills2.add("React");
        skills2.add("TypeScript");
        skills2.add("Node.js");
        skills2.add("GraphQL");

        Job job2 = Job.builder()
            .recruiterId(recruiter.getId())
            .title("Full Stack Developer")
            .description("Join our team as a full stack developer. Work with modern technologies " +
                "including React, TypeScript, and Node.js to build amazing web applications.")
            .companyName("TechCorp Inc")
            .location("Remote")
            .employmentType("FULL_TIME")
            .minSalary(new BigDecimal("100000"))
            .maxSalary(new BigDecimal("150000"))
            .currency("USD")
            .requiredSkills(skills2)
            .minYearsOfExperience(3)
            .educationLevel("BACHELORS")
            .status(Job.JobStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusMonths(3))
            .build();

        jobRepository.save(job2);

        log.info("Demo jobs created");
    }
}

