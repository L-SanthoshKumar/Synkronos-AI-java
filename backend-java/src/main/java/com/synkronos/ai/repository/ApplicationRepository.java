package com.synkronos.ai.repository;

import com.synkronos.ai.entity.Application;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Application entity
 */
@Repository
public interface ApplicationRepository extends MongoRepository<Application, String> {

    List<Application> findByJobSeekerId(String jobSeekerId);

    List<Application> findByJobId(String jobId);

    Optional<Application> findByJobIdAndJobSeekerId(String jobId, String jobSeekerId);

    long countByJobId(String jobId);
}

