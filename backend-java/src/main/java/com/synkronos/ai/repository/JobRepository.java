package com.synkronos.ai.repository;

import com.synkronos.ai.entity.Job;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Job entity
 */
@Repository
public interface JobRepository extends MongoRepository<Job, String> {

    List<Job> findByRecruiterId(String recruiterId);

    List<Job> findByStatus(Job.JobStatus status);

    @Query("{ 'status': 'ACTIVE', $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } }, { 'companyName': { $regex: ?0, $options: 'i' } } ] }")
    List<Job> searchActiveJobs(String searchTerm);

    @Query("{ 'status': 'ACTIVE', 'requiredSkills': { $in: ?0 } }")
    List<Job> findActiveJobsBySkills(List<String> skills);
}

